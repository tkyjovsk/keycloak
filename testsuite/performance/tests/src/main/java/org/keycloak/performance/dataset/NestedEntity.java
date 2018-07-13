package org.keycloak.performance.dataset;

import org.apache.commons.lang.Validate;

/**
 *
 * @author tkyjovsk
 * @param <PE> parent entity type
 */
public abstract class NestedEntity<PE extends Entity, R> extends Entity<R> {

    private final PE parentEntity;

    public NestedEntity(PE parentEntity, R representation) {
        super(representation);
        Validate.notNull(parentEntity);
        this.parentEntity = parentEntity;
    }

    public PE getParentEntity() {
        return parentEntity;
    }

    @Override
    public int hashCode() {
        return simpleClassName().hashCode() + getParentEntity().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

}
