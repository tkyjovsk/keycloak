/*
 * Copyright 2026 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.tests.federation.ldap;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.keycloak.common.util.PemUtils;
import org.keycloak.testframework.https.CertificatesConfigBuilder;
import org.keycloak.testframework.https.ManagedCertificates;
import org.keycloak.util.ldap.LDAPEmbeddedServer;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.message.BindRequest;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.ldap.LdapSession;
import org.apache.directory.server.ldap.handlers.request.BindRequestHandler;
import org.apache.directory.server.ldap.handlers.response.BindResponseHandler;
import org.apache.directory.server.protocol.shared.transport.Transport;

/**
 * Two real LDAP frontends share one directory, including entry UUIDs and password-policy state.
 * Faults suppress bind responses after decoding LDAP, so LDAPS faults happen after the TLS handshake.
 */
final class LDAPFailoverServer extends LDAPEmbeddedServer implements AutoCloseable {

    static final String USERNAME = "failover-user";
    static final String USER_DN = "uid=" + USERNAME + ",ou=People,dc=keycloak,dc=org";
    static final String SERVICE_DN = "uid=failover-service,dc=keycloak,dc=org";
    static final String PASSWORD = "Password1";
    static final String PASSWORD_POLICY_OID = "1.3.6.1.4.1.42.2.27.8.5.1";

    enum Fault {
        NONE, USER_BIND_TIMEOUT, ALL_BINDS_TIMEOUT
    }

    enum ConnectionType {
        LDAP, LDAPS
    }

    record BindAttempt(String dn, boolean passwordPolicyRequested) {
    }

    private final List<BindAttempt> primaryBinds = new CopyOnWriteArrayList<>();
    private final List<BindAttempt> secondaryBinds = new CopyOnWriteArrayList<>();
    private volatile Fault fault = Fault.NONE;
    private LdapServer primary;

    LDAPFailoverServer() {
        super(properties());
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY_DSF, DSF_INMEMORY);
        properties.setProperty(PROPERTY_BIND_HOST, "localhost");
        properties.setProperty(PROPERTY_BIND_PORT, "0");
        properties.setProperty(PROPERTY_BIND_LDAPS_PORT, "0");
        properties.setProperty(PROPERTY_ENABLE_SSL, "true");
        properties.setProperty("keystoreFile", TlsMaterial.CERTIFICATES.getServerKeyStorePath());
        properties.setProperty("certificatePassword", TlsMaterial.CERTIFICATES.getServerKeyStorePassword());
        properties.setProperty(PROPERTY_PPOLICY_ENABLED, "true");
        properties.setProperty(PROPERTY_PPOLICY_MUST_CHANGE, "true");
        // Like LDAPRule's mustChange fixture, use a non-admin service account so a user's password
        // update does not immediately mark the new password as another administrative reset.
        properties.setProperty(PROPERTY_ENABLE_ACCESS_CONTROL, "false");
        return properties;
    }

    @Override
    protected LdapServer createLdapServer() {
        return endpoint(false);
    }

    private LdapServer endpoint(boolean isPrimary) {
        LdapServer server = super.createLdapServer();
        server.setBindHandlers(new BindRequestHandler() {
            @Override
            public void handle(LdapSession session, BindRequest request) throws Exception {
                String dn = request.getName();
                (isPrimary ? primaryBinds : secondaryBinds).add(new BindAttempt(dn, request.hasControl(PASSWORD_POLICY_OID)));
                if (isPrimary && (fault == Fault.ALL_BINDS_TIMEOUT || fault == Fault.USER_BIND_TIMEOUT && new Dn(dn).equals(new Dn(USER_DN)))) {
                    // Do not block an ApacheDS worker. The client times out and closes the session.
                    return;
                }
                super.handle(session, request);
            }
        }, new BindResponseHandler());
        return server;
    }

    @Override
    public void start() throws Exception {
        super.start();
        primary = endpoint(true);
        primary.start();
        addUser(SERVICE_DN, "failover-service");
        addUser(USER_DN, USERNAME);
        clearBinds();
    }

    private void addUser(String dn, String username) throws Exception {
        directoryService.getAdminSession().add(new DefaultEntry(directoryService.getSchemaManager(), dn,
                "objectClass: inetOrgPerson", "uid: " + username, "cn: Failover User", "givenName: Failover",
                "sn: User", "mail: " + username + "@example.org", "userPassword: " + PASSWORD));
        if (directoryService.getAdminSession().lookup(new Dn(dn), "pwdReset").containsAttribute("pwdReset")) {
            directoryService.getAdminSession().modify(new Dn(dn), new DefaultModification(ModificationOperation.REMOVE_ATTRIBUTE, "pwdReset"));
        }
    }

    void fault(Fault fault) {
        this.fault = fault;
    }

    String url(boolean isPrimary, ConnectionType connectionType) {
        LdapServer server = isPrimary ? primary : ldapServer;
        Transport transport = List.of(server.getTransports()).stream()
                .filter(t -> t.isSSLEnabled() == (connectionType == ConnectionType.LDAPS)).findFirst().orElseThrow();
        int port = ((InetSocketAddress) transport.getAcceptor().getLocalAddress()).getPort();
        return (connectionType == ConnectionType.LDAPS ? "ldaps" : "ldap") + "://localhost:" + port;
    }

    void refusePrimaryConnections() {
        primary.stop();
    }

    List<BindAttempt> binds(boolean isPrimary, String dn) {
        return (isPrimary ? primaryBinds : secondaryBinds).stream().filter(b -> b.dn().equalsIgnoreCase(dn)).toList();
    }

    void clearBinds() {
        primaryBinds.clear();
        secondaryBinds.clear();
    }

    @Override
    public void close() throws Exception {
        try {
            if (primary != null && primary.isStarted()) {
                primary.stop();
            }
        } finally {
            if (ldapServer != null) {
                stop();
            } else if (directoryService != null) {
                shutdownDirectoryService();
            }
        }
    }

    static Path trustedCertificate() {
        return TlsMaterial.CERTIFICATE;
    }

    private static class TlsMaterial {
        private static final ManagedCertificates CERTIFICATES = new ManagedCertificates(new CertificatesConfigBuilder().tlsEnabled(true));
        private static final Path CERTIFICATE = exportCertificate();

        private static Path exportCertificate() {
            try {
                KeyStore store = KeyStore.getInstance(CERTIFICATES.getKeystoreFormat().name());
                try (InputStream input = Files.newInputStream(Path.of(CERTIFICATES.getServerKeyStorePath()))) {
                    store.load(input, CERTIFICATES.getServerKeyStorePassword().toCharArray());
                }
                Certificate certificate = store.getCertificate("server-key");
                Path path = Files.createTempFile("ldap-failover-certificate-", ".pem");
                Files.writeString(path, PemUtils.BEGIN_CERT + "\n" + PemUtils.encodeCertificate(certificate) + "\n" + PemUtils.END_CERT + "\n");
                path.toFile().deleteOnExit();
                return path;
            } catch (Exception e) {
                throw new IllegalStateException("Could not prepare the LDAP server certificate", e);
            }
        }
    }
}
