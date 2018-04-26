package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang.Validate;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity type
 */
public class NestedEntity<P extends Entity> extends Entity {
    
    @JsonBackReference
    private final P parentEntity;

    public NestedEntity(P parentEntity) {
        Validate.notNull(parentEntity);
        this.parentEntity = parentEntity;
    }

    public P getParentEntity() {
        return parentEntity;
    }
    
    @Override
    public int hashCode() {
        return simpleClassNameHashCode() + getParentEntity().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

}
