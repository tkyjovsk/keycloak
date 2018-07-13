package org.keycloak.performance.templates.idm.authorization;

import java.util.List;
import static java.util.stream.Collectors.toSet;
import org.keycloak.performance.dataset.idm.authorization.Policy;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.ResourcePermission;
import org.keycloak.performance.iteration.ListOfLists;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ResourcePermissionTemplate extends PolicyTemplate<ResourcePermission, ResourcePermissionRepresentation> {

    private final int resourcePermissionsPerResourceServer;
    private final int resourcesPerResourcePermission;
    private final int policiesPerResourcePermission;

    public ResourcePermissionTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.resourcePermissionsPerResourceServer = getConfiguration().getInt("resourcePermissionsPerResourceServer", 0);
        this.resourcesPerResourcePermission = getConfiguration().getInt("resourcesPerResourcePermission", 1);
        this.policiesPerResourcePermission = getConfiguration().getInt("policiesPerResourcePermission", 0);
    }

    public int getResourcePermissionsPerResourceServer() {
        return resourcePermissionsPerResourceServer;
    }

    public int getResourcesPerResourcePermission() {
        return resourcesPerResourcePermission;
    }

    public int getPoliciesPerResourcePermission() {
        return policiesPerResourcePermission;
    }

    @Override
    public int getEntityCountPerParent() {
        return resourcePermissionsPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("resourcePermissionsPerResourceServer: %s", resourcePermissionsPerResourceServer));
        validateInt().minValue(resourcePermissionsPerResourceServer, 0);
        // TODO add range check for usersPerRealm
        validateInt().minValue(resourcesPerResourcePermission, 1);
        validateInt().minValue(policiesPerResourcePermission, 0);
    }

    @Override
    public ResourcePermission newEntity(ResourceServer parentEntity, int index) {
        return new ResourcePermission(parentEntity, index, new ResourcePermissionRepresentation());
    }

    @Override
    public void processMappings(ResourcePermission permission) {
        String resourceType = permission.getRepresentation().getResourceType();
        if (resourceType == null || "".equals(resourceType)) {
            permission.setResources(new RandomSublist<>(
                    permission.getResourceServer().getResources(), // original list
                    permission.hashCode(), // random seed
                    resourcesPerResourcePermission, // sublist size
                    false // unique randoms?
            ));
            permission.getRepresentation().setResources(
                    permission.getResources().stream()
                            .map(r -> r.getId()).filter(id -> id != null).collect(toSet())
            );
        }

        permission.setPolicies(new RandomSublist<>(
                permission.getResourceServer().getAllPolicies(), // original list
                permission.hashCode(), // random seed
                policiesPerResourcePermission, // sublist size
                false // unique randoms?
        ));
        permission.getRepresentation().setPolicies(permission.getPolicies()
                .stream().map(p -> p.getId())
                .filter(id -> id != null) // need non-null policy IDs
                .collect(toSet()));
    }

}
