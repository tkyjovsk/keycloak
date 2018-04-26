package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.performance.dataset.idm.Role;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicyRoleDefinition extends NestedIndexedEntity<RolePolicy> {
    
    private Role role;
    private boolean required;

    public RolePolicyRoleDefinition(RolePolicy parentEntity, int index) {
        super(parentEntity, index);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
}
