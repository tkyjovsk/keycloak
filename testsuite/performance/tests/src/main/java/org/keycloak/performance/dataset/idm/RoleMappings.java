package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.IOException;
import java.util.List;
import org.keycloak.performance.dataset.NestedEntity;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 */
public abstract class RoleMappings<R extends Role> extends NestedEntity<RoleMapper> {
    
    private List<R> roles;

    public RoleMappings(RoleMapper roleMapper) {
        super(roleMapper);
    }

    @Override
    public String toJSON() throws IOException {
        return writeValueAsString(getRoles());
    }

    @JsonIgnore
    public RoleMapper getRoleMapper() {
        return getParentEntity();
    }
    
    public List<R> getRoles() {
        return roles;
    }

    public void setRoles(List<R> roles) {
        this.roles = roles;
    }

}
