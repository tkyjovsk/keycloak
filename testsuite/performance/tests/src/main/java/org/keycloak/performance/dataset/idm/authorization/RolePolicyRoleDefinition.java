package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicyRoleDefinition extends NestedIndexedEntity<RolePolicy, RolePolicyRepresentation.RoleDefinition> {

    public RolePolicyRoleDefinition(RolePolicy parentEntity, int index, RolePolicyRepresentation.RoleDefinition representation) {
        super(parentEntity, index, representation);
    }

}
