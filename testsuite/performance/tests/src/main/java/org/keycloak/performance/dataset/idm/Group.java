package org.keycloak.performance.dataset.idm;

import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class Group extends RoleMapper<GroupRepresentation> implements ResourceFacade<GroupRepresentation> {

    public Group(Realm realm, int index, GroupRepresentation representation) {
        super(realm, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getName();
    }

    @Override
    public RoleMappingResource roleMappingResource(Keycloak adminClient) {
        throw new UnsupportedOperationException();
    }

    public GroupResource groupResource(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).groups().group(getId());
    }

    @Override
    public GroupRepresentation read(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).groups().groups(getRepresentation().getName(), 0, 1).get(0);
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).groups().add(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        groupResource(adminClient).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        groupResource(adminClient).remove();
    }

}
