package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import org.keycloak.performance.dataset.idm.Role;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicy extends Policy {

    private List<RolePolicyRoleDefinition> roles;

    @JsonIgnore
    private List<Role> mappedRoles;

    public RolePolicy(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public final String getType() {
        return "role";
    }

    public void setRoles(List<RolePolicyRoleDefinition> roles) {
        this.roles = roles;
    }

    public List<RolePolicyRoleDefinition> getRoles() {
        return roles;
    }

    public List<Role> getMappedRoles() {
        return mappedRoles;
    }

    public void setMappedRoles(List<Role> mappedRoles) {
        this.mappedRoles = mappedRoles;
    }

}
