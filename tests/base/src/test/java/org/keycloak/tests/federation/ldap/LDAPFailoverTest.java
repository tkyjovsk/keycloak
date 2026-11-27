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


import jakarta.ws.rs.core.Response;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.models.LDAPConstants;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.injection.LifeCycle;
import org.keycloak.testframework.oauth.OAuthClient;
import org.keycloak.testframework.oauth.annotations.InjectOAuthClient;
import org.keycloak.testframework.realm.ClientBuilder;
import org.keycloak.testframework.realm.ClientConfig;
import org.keycloak.testframework.realm.ManagedRealm;
import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;
import org.keycloak.testframework.ui.annotations.InjectPage;
import org.keycloak.testframework.ui.page.LoginPage;
import org.keycloak.testframework.ui.page.LoginPasswordUpdatePage;
import org.keycloak.testframework.util.ApiUtil;
import org.keycloak.tests.federation.ldap.LDAPFailoverServer.ConnectionType;
import org.keycloak.tests.federation.ldap.LDAPFailoverServer.Fault;
import org.keycloak.testsuite.util.oauth.AccessTokenResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.keycloak.tests.federation.ldap.LDAPFailoverServer.PASSWORD;
import static org.keycloak.tests.federation.ldap.LDAPFailoverServer.SERVICE_DN;
import static org.keycloak.tests.federation.ldap.LDAPFailoverServer.USERNAME;
import static org.keycloak.tests.federation.ldap.LDAPFailoverServer.USER_DN;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@KeycloakIntegrationTest(config = LDAPFailoverTest.ServerConfig.class)
public class LDAPFailoverTest {

    @InjectRealm(lifecycle = LifeCycle.METHOD)
    ManagedRealm realm;

    @InjectOAuthClient(config = ClientConfiguration.class, lifecycle = LifeCycle.METHOD)
    OAuthClient oauth;

    @InjectPage
    LoginPage loginPage;

    @InjectPage
    LoginPasswordUpdatePage passwordUpdatePage;

    private LDAPFailoverServer ldap;
    private ComponentRepresentation provider;

    @BeforeEach
    void startDirectory() throws Exception {
        ldap = new LDAPFailoverServer();
        ldap.init();
        ldap.start();
        oauth.getDriver().manage().deleteAllCookies();
    }

    @AfterEach
    void stopDirectory() throws Exception {
        if (ldap != null) {
            ldap.close();
        }
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void healthyPrimary(ConnectionType connectionType) {
        configure(connectionType);
        assertLogin(PASSWORD);
        assertThat(ldap.binds(true, USER_DN), hasSize(1));
        assertThat(ldap.binds(false, USER_DN), empty());
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void invalidPasswordDoesNotTryNextServer(ConnectionType connectionType) {
        configure(connectionType);
        assertInvalidPassword();
        assertThat(ldap.binds(true, USER_DN), hasSize(1));
        assertThat(ldap.binds(false, USER_DN), empty());
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void userBindTimeoutFailsOver(ConnectionType connectionType) {
        configure(connectionType);
        ldap.fault(Fault.USER_BIND_TIMEOUT);
        assertLookup();
        assertThat(ldap.binds(false, SERVICE_DN), empty());
        ldap.clearBinds();
        assertLogin(PASSWORD);
        assertUserBindFailover();
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void serviceAccountAndUserBindFailOver(ConnectionType connectionType) {
        configure(connectionType);
        ldap.fault(Fault.ALL_BINDS_TIMEOUT);
        assertLookup();
        assertThat(ldap.binds(false, SERVICE_DN), not(empty()));
        ldap.clearBinds();
        assertLogin(PASSWORD);
        assertUserBindFailover();
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void invalidPasswordIsRejectedAfterFailover(ConnectionType connectionType) {
        configure(connectionType);
        ldap.fault(Fault.USER_BIND_TIMEOUT);
        assertInvalidPassword();
        assertUserBindFailover();
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void passwordPolicyControlSurvivesFailover(ConnectionType connectionType) {
        configure(connectionType);
        enablePasswordPolicy();
        ldap.fault(Fault.USER_BIND_TIMEOUT);
        assertLogin(PASSWORD);
        assertUserBindFailover();
        assertThat(ldap.binds(false, USER_DN).stream().map(LDAPFailoverServer.BindAttempt::passwordPolicyRequested).toList(), everyItem(is(true)));
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void healthyServerFirst(ConnectionType connectionType) {
        configure(connectionType);
        provider.getConfig().putSingle(LDAPConstants.CONNECTION_URL, ldap.url(false, connectionType) + " " + ldap.url(true, connectionType));
        updateProvider();
        ldap.fault(Fault.USER_BIND_TIMEOUT);
        assertLogin(PASSWORD);
        assertThat(ldap.binds(true, USER_DN), empty());
        assertThat(ldap.binds(false, USER_DN), hasSize(1));
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void refusedConnectionFailsOver(ConnectionType connectionType) {
        configure(connectionType);
        ldap.refusePrimaryConnections();
        assertLogin(PASSWORD);
        assertThat(ldap.binds(true, USER_DN), empty());
        assertThat(ldap.binds(false, USER_DN), hasSize(1));
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void recoveredPrimaryIsUsedForNextLogin(ConnectionType connectionType) {
        configure(connectionType);
        ldap.fault(Fault.USER_BIND_TIMEOUT);
        assertLogin(PASSWORD);
        assertUserBindFailover();
        ldap.fault(Fault.NONE);
        ldap.clearBinds();
        assertLogin(PASSWORD);
        assertThat(ldap.binds(true, USER_DN), hasSize(1));
        assertThat(ldap.binds(false, USER_DN), empty());
    }

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    void forcedPasswordChangeAfterFailover(ConnectionType connectionType) {
        configure(connectionType);
        enablePasswordPolicy();
        ldap.setPwdReset(USER_DN, true);
        ldap.fault(Fault.USER_BIND_TIMEOUT);

        AccessTokenResponse grant = oauth.doPasswordGrantRequest(USERNAME, PASSWORD);
        assertThat(grant.getError(), is("invalid_grant"));
        assertThat(grant.getErrorDescription(), is("Account is not fully set up"));
        assertThat(grant.getAccessToken(), is((String) null));
        assertUserBindFailover();

        oauth.openLoginForm();
        loginPage.fillLogin(USERNAME, PASSWORD);
        loginPage.submit();
        passwordUpdatePage.assertCurrent();
        passwordUpdatePage.changePassword("Changed-password1", "Changed-password1");
        assertThat(oauth.parseLoginResponse().getCode(), notNullValue());
        assertLogin("Changed-password1");
        AccessTokenResponse oldPassword = oauth.doPasswordGrantRequest(USERNAME, PASSWORD);
        assertThat(oldPassword.getError(), is("invalid_grant"));
        assertThat(oldPassword.getAccessToken(), is((String) null));
    }

    private void configure(ConnectionType connectionType) {
        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.putSingle(LDAPConstants.CONNECTION_URL, ldap.url(true, connectionType) + " " + ldap.url(false, connectionType));
        config.putSingle(LDAPConstants.USERS_DN, "ou=People,dc=keycloak,dc=org");
        config.putSingle(LDAPConstants.BIND_DN, SERVICE_DN);
        config.putSingle(LDAPConstants.BIND_CREDENTIAL, PASSWORD);
        config.putSingle(LDAPConstants.AUTH_TYPE, LDAPConstants.AUTH_TYPE_SIMPLE);
        config.putSingle(LDAPConstants.EDIT_MODE, UserStorageProvider.EditMode.WRITABLE.name());
        config.putSingle(LDAPConstants.USERNAME_LDAP_ATTRIBUTE, "uid");
        config.putSingle(LDAPConstants.RDN_LDAP_ATTRIBUTE, "uid");
        config.putSingle(LDAPConstants.UUID_LDAP_ATTRIBUTE, "entryUUID");
        config.putSingle(LDAPConstants.USER_OBJECT_CLASSES, "inetOrgPerson");
        config.putSingle(LDAPConstants.CONNECTION_POOLING, "false");
        config.putSingle(LDAPConstants.CONNECTION_TIMEOUT, "2000");
        config.putSingle(LDAPConstants.READ_TIMEOUT, "2000");
        config.putSingle(LDAPConstants.START_TLS, "false");
        config.putSingle(LDAPConstants.ENABLE_LDAP_PASSWORD_POLICY, "false");
        config.putSingle(UserStorageProviderModel.CACHE_POLICY, UserStorageProviderModel.CachePolicy.NO_CACHE.name());
        provider = new ComponentRepresentation();
        provider.setName("failover-ldap");
        provider.setProviderId("ldap");
        provider.setProviderType(UserStorageProvider.class.getName());
        provider.setConfig(config);
        try (Response response = realm.admin().components().add(provider)) {
            assertThat(response.getStatus(), is(201));
            provider.setId(ApiUtil.getCreatedId(response));
        }
        ldap.clearBinds();
    }

    private void enablePasswordPolicy() {
        provider.getConfig().putSingle(LDAPConstants.ENABLE_LDAP_PASSWORD_POLICY, "true");
        updateProvider();
    }

    private void updateProvider() {
        realm.admin().components().component(provider.getId()).update(provider);
        ldap.clearBinds();
    }

    private void assertLookup() {
        assertThat(realm.admin().users().searchByUsername(USERNAME, true), hasSize(1));
    }

    private void assertLogin(String password) {
        AccessTokenResponse grant = oauth.doPasswordGrantRequest(USERNAME, password);
        assertThat(grant.getErrorDescription(), grant.getStatusCode(), is(200));
        assertThat(grant.getAccessToken(), notNullValue());
    }

    private void assertInvalidPassword() {
        AccessTokenResponse grant = oauth.doPasswordGrantRequest(USERNAME, "wrong-password");
        assertThat(grant.getError(), is("invalid_grant"));
        assertThat(grant.getErrorDescription(), is("Invalid user credentials"));
        assertThat(grant.getAccessToken(), is((String) null));
    }

    private void assertUserBindFailover() {
        assertThat(ldap.binds(true, USER_DN), hasSize(1));
        assertThat(ldap.binds(false, USER_DN), hasSize(1));
    }

    public static class ClientConfiguration implements ClientConfig {
        @Override
        public ClientBuilder configure(ClientBuilder client) {
            return client.clientId("ldap-failover").secret("secret").directAccessGrantsEnabled(true);
        }
    }

    public static class ServerConfig implements KeycloakServerConfig {
        @Override
        public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder config) {
            return config.option("truststore-paths", LDAPFailoverServer.trustedCertificate().toString());
        }
    }
}
