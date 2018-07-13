package org.keycloak.performance.dataset.idm;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.performance.dataset.NestedEntity;
import org.keycloak.performance.dataset.UpdateOnlyResourceFacade;

/**
 *
 * @author tkyjovsk
 * @param <RM> role-mapper parent entity (user or group)
 */
public class RoleMappings<RM extends RoleMapper> extends NestedEntity<RM, RoleMappingsRepresentation>
        implements UpdateOnlyResourceFacade<RoleMappingsRepresentation> {

    public RoleMappings(RM roleMapper, RoleMappingsRepresentation representation) {
        super(roleMapper, representation);
    }

    public RoleMapper getRoleMapper() {
        return getParentEntity();
    }

    @Override
    public String toString() {
        return String.format("%s/role-mappings/realm", getRoleMapper());
    }

    @Override
    public void update(Keycloak adminClient) {
//        logger().warn("UPDATING ROLE MAPPINGS");
        getRoleMapper().roleMappingResource(adminClient).realmLevel().add(getRepresentation());
    }

}
