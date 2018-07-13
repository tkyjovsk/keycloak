package org.keycloak.performance.dataset.idm.authorization;

import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class Scope extends NestedIndexedEntity<ResourceServer, ScopeRepresentation>
        implements ResourceFacade<ScopeRepresentation> {

    public Scope(ResourceServer resourceServer, int index, ScopeRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getName();
    }

    public ResourceServer getResourceServer() {
        return getParentEntity();
    }

    @Override
    public ScopeRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).scopes().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).scopes().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).scopes().scope(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).scopes().scope(getId()).remove();
    }
}
