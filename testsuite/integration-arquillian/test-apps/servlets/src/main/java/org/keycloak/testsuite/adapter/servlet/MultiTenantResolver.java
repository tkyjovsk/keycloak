/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.adapter.servlet;

import org.junit.Assert;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author Juraci Paixão Kröhling <juraci at kroehling.de>
 */
public class MultiTenantResolver implements KeycloakConfigResolver {

    protected static final boolean AUTH_SERVER_SSL_REQUIRED = Boolean.parseBoolean(System.getProperty("auth.server.ssl.required", "true"));
    protected static final String AUTH_SERVER_SCHEME = AUTH_SERVER_SSL_REQUIRED ? "https" : "http";
    protected static final String AUTH_SERVER_PORT = AUTH_SERVER_SSL_REQUIRED ? System.getProperty("auth.server.https.port", "8543") : System.getProperty("auth.server.http.port", "8180");

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request request) {

        String path = request.getURI();
        int multitenantIndex = path.indexOf("multi-tenant/");
        if (multitenantIndex == -1) {
            throw new IllegalStateException("Not able to resolve realm from the request path!");
        }

        String realm = path.substring(path.indexOf("multi-tenant/")).split("/")[1];
        if (realm.contains("?")) {
            realm = realm.split("\\?")[0];
        }
        
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/" + realm + "-keycloak.json");

        if (is == null) {
            throw new IllegalStateException("Not able to find the file /" + realm + "-keycloak.json");
        }

        try {
            return KeycloakDeploymentBuilder.build(httpsAwareConfigurationStream(is));
        } catch (IOException e) {
            throw new AssertionError("There's a problem with loading keycloak configuration", e);
        }
    }

    protected static InputStream httpsAwareConfigurationStream(InputStream input) throws IOException {
        if (!AUTH_SERVER_SSL_REQUIRED) {
            return input;
        }
        PipedInputStream in = new PipedInputStream();
        final PipedOutputStream out = new PipedOutputStream(in);
        try (PrintWriter pw = new PrintWriter(out)) {
            try (Scanner s = new Scanner(input)) {
                while (s.hasNextLine()) {
                    String lineWithReplaces = s.nextLine().replace("http://localhost:8180/auth", AUTH_SERVER_SCHEME + "://localhost:" + AUTH_SERVER_PORT + "/auth");
                    pw.println(lineWithReplaces);
                }
            }
        }
        return in;
    }

}
