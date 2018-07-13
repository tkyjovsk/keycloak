package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedEntity;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;

/**
 *
 * @author tkyjovsk
 */
public abstract class ResourceOwner<PE extends Entity> extends NestedEntity<PE, ResourceOwnerRepresentation> {

    public ResourceOwner(PE resourceOwner) {
        super(resourceOwner, new ResourceOwnerRepresentation());
    }

    public PE getResourceOwner() {
        return getParentEntity();
    }

    @Override
    public String toString() {
        return getRepresentation().getName();
    }

}
