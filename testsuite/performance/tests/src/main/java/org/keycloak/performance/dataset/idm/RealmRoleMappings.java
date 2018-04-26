package org.keycloak.performance.dataset.idm;

/**
 *
 * @author tkyjovsk
 */
public class RealmRoleMappings extends RoleMappings<RealmRole> {

    public RealmRoleMappings(RoleMapper roleMapper) {
        super(roleMapper);
    }

    @Override
    public String toString() {
        return String.format("%s/role-mappings/realm", getRoleMapper());
    }

}
