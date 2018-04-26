package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.dataset.idm.authorization.ClientResourceOwner;
import org.keycloak.performance.dataset.idm.authorization.Resource;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.UserResourceOwner;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;

/**
 *
 * @author tkyjovsk
 */
public class ResourceTemplate extends NestedIndexedEntityTemplate<ResourceServer, Resource> {

    private final int resourcesPerResourceServer;

    public ResourceTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("displayName");
        registerAttributeTemplate("uri");
        registerAttributeTemplate("type");
        registerAttributeTemplate("ownerManagedAccess");
        this.resourcesPerResourceServer = configuration.getInt("resourcesPerResourceServer", 0);
    }

    public int getResourcesPerResourceServer() {
        return resourcesPerResourceServer;
    }

    @Override
    public Resource produce(ResourceServer resourceServer, int index) {
        Resource resource = new Resource(resourceServer, index);
        resource.setName(
                processAttribute("name", resource));
        resource.setDisplayName(
                processAttribute("displayName", resource));
        resource.setUri(
                processAttribute("uri", resource));
        resource.setType(
                processAttribute("type", resource));
        resource.setOwnerManagedAccess(Boolean.parseBoolean(
                processAttribute("ownerManagedAccess", resource)));

        resource.setOwner(
                resource.isOwnerManagedAccess()
                ? new UserResourceOwner( // map to random user from the same realm
                        new RandomSublist<>(
                                resource.getResourceServer().getClient().getRealm().getUsers(), // original list
                                resource.hashCode(), // random seed
                                1, // sublist size
                                false // unique randoms?
                        ).get(0)
                )
                : new ClientResourceOwner(resourceServer.getClient())
        );

        return resource;
    }

    @Override
    public int getEntityCountPerParent() {
        return resourcesPerResourceServer;
    }

    @Override
    public void validateSizeConfiguration() {
        logger().info(String.format("resourcesPerResourceServer: %s", resourcesPerResourceServer));
        validateInt().minValue(resourcesPerResourceServer, 0);
    }

}
