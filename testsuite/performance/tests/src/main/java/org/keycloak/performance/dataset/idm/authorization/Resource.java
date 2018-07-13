package org.keycloak.performance.dataset.idm.authorization;

import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.Validate;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ResourcesResource;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class Resource extends NestedIndexedEntity<ResourceServer, ResourceRepresentation>
        implements ResourceFacade<ResourceRepresentation> {

    private List<Scope> scopes;

    public Resource(ResourceServer resourceServer, int index, ResourceRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getName();
    }

    public ResourceServer getResourceServer() {
        return getParentEntity();
    }

    public ResourcesResource resourcesResource(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).resources();
    }

    @Override
    public ResourceRepresentation read(Keycloak adminClient) {
        return resourcesResource(adminClient).findByName(getRepresentation().getName()).get(0);
    }

    @Override
    public Response create(Keycloak adminClient) {
        Validate.notNull(getResourceServer());
        Validate.notNull(getResourceServer().getClient());
        Validate.notNull(getResourceServer().getClient().getRepresentation().getBaseUrl());
        return resourcesResource(adminClient).create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        resourcesResource(adminClient).resource(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        resourcesResource(adminClient).resource(getId()).remove();
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

}
