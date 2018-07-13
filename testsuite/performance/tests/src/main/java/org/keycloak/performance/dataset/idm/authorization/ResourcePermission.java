package org.keycloak.performance.dataset.idm.authorization;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ResourcePermission extends Policy<ResourcePermissionRepresentation> {

    private List<Resource> resources;
    private List<Policy> policies;

    public ResourcePermission(ResourceServer resourceServer, int index, ResourcePermissionRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public ResourcePermissionRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).permissions().resource().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).permissions().resource().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).permissions().resource().findById(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).permissions().resource().findById(getId()).remove();
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

}
