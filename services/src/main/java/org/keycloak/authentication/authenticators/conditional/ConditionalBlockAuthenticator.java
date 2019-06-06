package org.keycloak.authentication.authenticators.conditional;

import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.authentication.AuthenticationFlowContext;

public interface ConditionalBlockAuthenticator extends Authenticator {
    boolean matchCondition(AuthenticationFlowContext context);

    default void authenticate(AuthenticationFlowContext context) {
        // authenticate is not called for ConditionalBlockAuthenticators
    }

    default boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }
}
