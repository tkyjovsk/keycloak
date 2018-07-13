package org.keycloak.performance.templates.idm.authorization;

import java.util.List;
import static java.util.stream.Collectors.toSet;
import org.keycloak.performance.dataset.idm.authorization.Policy;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.ResourcePermission;
import org.keycloak.performance.dataset.idm.authorization.ScopePermission;
import org.keycloak.performance.iteration.ListOfLists;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ScopePermissionTemplate extends PolicyTemplate<ScopePermission, ScopePermissionRepresentation> {

    private final int scopePermissionsPerResourceServer;
    private final int scopesPerScopePermission;
    private final int policiesPerScopePermission;

    public ScopePermissionTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.scopePermissionsPerResourceServer = getConfiguration().getInt("scopePermissionsPerResourceServer", 0);
        this.scopesPerScopePermission = getConfiguration().getInt("scopesPerScopePermission", 1);
        this.policiesPerScopePermission = getConfiguration().getInt("policiesPerScopePermission", 0);
    }

    public int getScopePermissionsPerResourceServer() {
        return scopePermissionsPerResourceServer;
    }

    public int getScopesPerScopePermission() {
        return scopesPerScopePermission;
    }

    public int getPoliciesPerScopePermission() {
        return policiesPerScopePermission;
    }

    @Override
    public int getEntityCountPerParent() {
        return scopePermissionsPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("scopePermissionsPerResourceServer: %s", scopePermissionsPerResourceServer));
        validateInt().minValue(scopePermissionsPerResourceServer, 0);
        // TODO add range check for usersPerRealm
        validateInt().minValue(scopesPerScopePermission, 1);
        validateInt().minValue(policiesPerScopePermission, 0);
    }

    @Override
    public ScopePermission newEntity(ResourceServer parentEntity, int index) {
        return new ScopePermission(parentEntity, index, new ScopePermissionRepresentation());
    }

    @Override
    public void processMappings(ScopePermission permission) {

        permission.setScopes(new RandomSublist<>(
                permission.getResourceServer().getScopes(), // original list
                permission.hashCode(), // random seed
                scopesPerScopePermission, // sublist size
                false // unique randoms?
        ));
        permission.getRepresentation().setScopes(
                permission.getScopes().stream()
                        .map(r -> r.getId()).filter(id -> id != null).collect(toSet())
        );

        permission.setPolicies(new RandomSublist<>(
                permission.getResourceServer().getAllPolicies(), // original list
                permission.hashCode(), // random seed
                policiesPerScopePermission, // sublist size
                false // unique randoms?
        ));
        permission.getRepresentation().setPolicies(permission.getPolicies()
                .stream().map(p -> p.getId())
                .filter(id -> id != null) // need non-null policy IDs
                .collect(toSet()));
    }

}
