package org.keycloak.migration.migrators;

import org.jboss.logging.Logger;
import org.keycloak.migration.ModelVersion;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MigrateTo8_0_0  implements Migration {

    public static final ModelVersion VERSION = new ModelVersion("8.0.0");

    private static final Logger LOG = Logger.getLogger(MigrateTo8_0_0.class);

    @Override
    public ModelVersion getVersion() {
        return VERSION;
    }

    @Override
    public void migrate(KeycloakSession session) {
        session.realms().getRealms().stream().forEach(r -> {
            migrateRealm(session, r, false);
        });
    }

    @Override
    public void migrateImport(KeycloakSession session, RealmModel realm, RealmRepresentation rep, boolean skipUserDependent) {
        // No-op. Migration of optional authentication executions was already handled in RepresentationToModel.importRealm
    }

    protected void migrateRealm(KeycloakSession session, RealmModel realm, boolean jsn) {
        for (AuthenticationFlowModel authFlow : realm.getAuthenticationFlows()) {
            for (AuthenticationExecutionModel authExecution : realm.getAuthenticationExecutions(authFlow.getId())) {
                // Those were OPTIONAL executions in previous version
                if (authExecution.getRequirement() == AuthenticationExecutionModel.Requirement.CONDITIONAL) {
                    migrateOptionalAuthenticationExecution(realm, authFlow, authExecution, true);
                }
            }
        }
    }


    public static void migrateOptionalAuthenticationExecution(RealmModel realm, AuthenticationFlowModel parentFlow, AuthenticationExecutionModel optionalExecution, boolean updateOptionalExecution) {
        LOG.debugf("Migrating optional execution '%s' of flow '%s' of realm '%s' to subflow", optionalExecution.getAuthenticator(), parentFlow.getAlias(), realm.getName());

        AuthenticationFlowModel conditionalOTP = new AuthenticationFlowModel();
        conditionalOTP.setTopLevel(false);
        conditionalOTP.setBuiltIn(parentFlow.isBuiltIn());
        conditionalOTP.setAlias(parentFlow.getAlias() + " - " + optionalExecution.getAuthenticator() + " - Conditional");
        conditionalOTP.setDescription("Flow to determine if the " + optionalExecution.getAuthenticator() + " authenticator should be used or not.");
        conditionalOTP.setProviderId("basic-flow");
        conditionalOTP = realm.addAuthenticationFlow(conditionalOTP);

        AuthenticationExecutionModel execution = new AuthenticationExecutionModel();
        execution.setParentFlow(parentFlow.getId());
        execution.setRequirement(AuthenticationExecutionModel.Requirement.CONDITIONAL);
        execution.setFlowId(conditionalOTP.getId());
        execution.setPriority(optionalExecution.getPriority());
        execution.setAuthenticatorFlow(true);
        realm.addAuthenticatorExecution(execution);

        execution = new AuthenticationExecutionModel();
        execution.setParentFlow(conditionalOTP.getId());
        execution.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        execution.setAuthenticator("conditional-user-configured");
        execution.setPriority(10);
        execution.setAuthenticatorFlow(false);
        realm.addAuthenticatorExecution(execution);

        // Move optionalExecution as child of newly created parent flow
        optionalExecution.setParentFlow(conditionalOTP.getId());
        optionalExecution.setRequirement(AuthenticationExecutionModel.Requirement.REQUIRED);
        optionalExecution.setPriority(20);

        // In case of DB migration, we're updating existing execution, which is already in DB.
        // In case of JSON migration, the execution is not yet in DB and will be added later
        if (updateOptionalExecution) {
            realm.updateAuthenticatorExecution(optionalExecution);
        }
    }
}
