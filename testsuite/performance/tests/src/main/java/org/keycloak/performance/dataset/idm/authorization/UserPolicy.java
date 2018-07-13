package org.keycloak.performance.dataset.idm.authorization;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class UserPolicy extends Policy<UserPolicyRepresentation> {

    private List<User> users;

    public UserPolicy(ResourceServer resourceServer, int index, UserPolicyRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public UserPolicyRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().user().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().user().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().user().findById(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().user().findById(getId()).remove();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
