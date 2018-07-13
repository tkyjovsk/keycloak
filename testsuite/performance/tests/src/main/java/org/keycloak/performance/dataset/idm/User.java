package org.keycloak.performance.dataset.idm;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class User extends RoleMapper<UserRepresentation> implements ResourceFacade<UserRepresentation> {

    private List<Credential> credentials;
    private RoleMappings<User> realmRoleMappings;
    private List<ClientRoleMappings<User>> clientRoleMappingsList;

    public User(Realm realm, int index, UserRepresentation representation) {
        super(realm, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getUsername();
    }

    public void setRealmRoleMappings(RoleMappings<User> realmRoleMappings) {
        this.realmRoleMappings = realmRoleMappings;
    }

    public void setClientRoleMappingsList(List<ClientRoleMappings<User>> clientRoleMappingsList) {
        this.clientRoleMappingsList = clientRoleMappingsList;
    }

    public RoleMappings<User> getRealmRoleMappings() {
        return realmRoleMappings;
    }

    public List<ClientRoleMappings<User>> getClientRoleMappingsList() {
        return clientRoleMappingsList;
    }

    public List<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<Credential> credentials) {
        this.credentials = credentials;
    }

    public UserResource userResource(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).users().get(getId());
    }

    @Override
    public UserRepresentation read(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).users().search(getRepresentation().getUsername()).get(0);
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).users().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        userResource(adminClient).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        userResource(adminClient).remove();
    }

    @Override
    public RoleMappingResource roleMappingResource(Keycloak adminClient) {
        return userResource(adminClient).roles();
    }

}
