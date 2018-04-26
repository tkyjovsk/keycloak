package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedEntity;

/**
 *
 * @author tkyjovsk
 */
public abstract class ResourceOwner<T extends Entity> extends NestedEntity<T> {
    
    public ResourceOwner(T resourceOwner) {
        super(resourceOwner);
    }

    @JsonBackReference
    public T getResourceOwner() {
        return getParentEntity();
    }
    
    @Override
    public synchronized String getId() {
        return getResourceOwner().getId();
    }
    
}
