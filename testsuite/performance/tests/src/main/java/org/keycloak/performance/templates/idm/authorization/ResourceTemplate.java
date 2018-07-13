package org.keycloak.performance.templates.idm.authorization;

import static java.util.stream.Collectors.toSet;
import org.keycloak.performance.dataset.idm.authorization.Resource;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ResourceTemplate extends NestedIndexedEntityTemplate<ResourceServer, Resource, ResourceRepresentation> {

    private final int resourcesPerResourceServer;
    private final int scopesPerResource;

    public ResourceTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.resourcesPerResourceServer = getConfiguration().getInt("resourcesPerResourceServer", 0);
        this.scopesPerResource = getConfiguration().getInt("scopesPerResource", 0);
    }

    public int getResourcesPerResourceServer() {
        return resourcesPerResourceServer;
    }

    @Override
    public int getEntityCountPerParent() {
        return resourcesPerResourceServer;
    }

    public int getScopesPerResource() {
        return scopesPerResource;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("resourcesPerResourceServer: %s", resourcesPerResourceServer));
        validateInt().minValue(resourcesPerResourceServer, 0);
        validateInt().minValue(scopesPerResource, 0); // fixme range check
    }

    @Override
    public Resource newEntity(ResourceServer parentEntity, int index) {
        return new Resource(parentEntity, index, new ResourceRepresentation());
    }

    @Override
    public void processMappings(Resource resource) {
        if (resource.getRepresentation().getOwnerManagedAccess()) {
            String ownerId = new RandomSublist<>(
                    resource.getResourceServer().getClient().getRealm().getUsers(), // original list
                    resource.hashCode(), // random seed
                    1, // sublist size
                    false // unique randoms?
            ).get(0).getRepresentation().getId();
            resource.getRepresentation().setOwner(ownerId);
        }
        
        resource.setScopes(new RandomSublist<>(
                resource.getResourceServer().getScopes(), // original list
                resource.hashCode(), // random seed
                scopesPerResource, // sublist size
                false // unique randoms?
        ));
        resource.getRepresentation().setScopes(
                resource.getScopes().stream().map(s -> s.getRepresentation()).collect(toSet()));
    }

}
