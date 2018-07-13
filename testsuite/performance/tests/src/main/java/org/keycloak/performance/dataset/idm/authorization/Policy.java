package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.authorization.AbstractPolicyRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public abstract class Policy<PR extends AbstractPolicyRepresentation> 
        extends NestedIndexedEntity<ResourceServer, PR> 
        implements ResourceFacade<PR>
{

    public Policy(ResourceServer resourceServer, int index, PR representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getName();
    }

    public ResourceServer getResourceServer() {
        return getParentEntity();
    }

}
