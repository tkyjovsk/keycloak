package org.keycloak.performance.dataset.idm.authorization;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.performance.dataset.idm.Role;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicy extends Policy<RolePolicyRepresentation> {

    private List<Role> roles;

    public RolePolicy(ResourceServer resourceServer, int index, RolePolicyRepresentation representation) {
        super(resourceServer, index, representation);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public RolePolicyRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().role().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().role().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().role().findById(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().role().findById(getId()).remove();
    }

}
