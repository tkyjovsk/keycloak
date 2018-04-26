package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang.Validate;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity
 */
public abstract class NestedEntity<P extends IndexedEntity> extends IndexedEntity {

    @JsonBackReference
    private P parentEntity;

    public NestedEntity(P parentEntity, int index) {
        super(index);
        Validate.notNull(parentEntity);
        this.parentEntity = parentEntity;
    }

    public P getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(P parentEntity) {
        this.parentEntity = parentEntity;
    }

    @Override
    public int hashCode() {
        return this.getClass().getSimpleName().hashCode() * super.hashCode() + getParentEntity().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.getParentEntity().equals(((NestedEntity) obj).getParentEntity());
    }

}
