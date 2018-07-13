package org.keycloak.performance.dataset.idm;

import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleByIdResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 * @param <PE>
 */
public abstract class Role<PE extends Entity> extends NestedIndexedEntity<PE, RoleRepresentation>
        implements ResourceFacade<RoleRepresentation> {
    
    public Role(PE parentEntity, int index, RoleRepresentation representation) {
        super(parentEntity, index, representation);
    }
    
    @Override
    public String toString() {
        return getRepresentation().getName();
    }
    
    public abstract RolesResource rolesResource(Keycloak adminClient);
    
    public abstract RoleByIdResource roleByIdResource(Keycloak adminClient);
    
    public RoleResource roleResource(Keycloak adminClient) {
        return rolesResource(adminClient).get(getRepresentation().getName());
    }
    
    @Override
    public RoleRepresentation read(Keycloak adminClient) {
        return roleResource(adminClient).toRepresentation();
    }
    
    @Override
    public Response create(Keycloak adminClient) { // FIXME
        rolesResource(adminClient).create(getRepresentation());
        return null;
    }
    
    @Override
    public void update(Keycloak adminClient) {
        roleByIdResource(adminClient).updateRole(getId(), getRepresentation());
    }
    
    @Override
    public void delete(Keycloak adminClient) {
        roleByIdResource(adminClient).deleteRole(getId());
    }
}
