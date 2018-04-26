package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.IndexedEntityM;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicyM extends IndexedEntityM<RolePolicyRepresentation> {
    
    public RolePolicyM(int index, RolePolicyRepresentation representation) {
        super(index, representation);
    }
    
}
