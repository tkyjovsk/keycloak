package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 *
 * @author tkyjovsk
 */
public class NestedEntity<P extends Entity> extends Entity {
    
    @JsonBackReference
    private P parentEntity;

    public NestedEntity(P parentEntity) {
        this.parentEntity = parentEntity;
    }

    public P getParentEntity() {
        return parentEntity;
    }
    
}
