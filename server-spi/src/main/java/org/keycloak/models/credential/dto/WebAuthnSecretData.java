/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
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
 *
 */

package org.keycloak.models.credential.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class WebAuthnSecretData {

    // These are in fact no secret data, however we don't want to display them in admin console etc. Doublecheck if it's ok
    private final String attestationStatement;
    private final String credentialPublicKey;

    @JsonCreator
    public WebAuthnSecretData(@JsonProperty("attestationStatement") String attestationStatement,
                              @JsonProperty("credentialPublicKey") String credentialPublicKey) {
        this.attestationStatement = attestationStatement;
        this.credentialPublicKey = credentialPublicKey;
    }


    public String getAttestationStatement() {
        return attestationStatement;
    }

    public String getCredentialPublicKey() {
        return credentialPublicKey;
    }


    @Override
    public String toString() {
        return "WebAuthnSecretData { " +
                "attestationStatement='" + attestationStatement + '\'' +
                ", credentialPublicKey='" + credentialPublicKey + '\'' +
                " }";
    }
}
