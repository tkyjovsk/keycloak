package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang.Validate;
import static org.keycloak.performance.iteration.RandomBooleans.getRandomBooleans;
import static org.keycloak.performance.iteration.RandomIntegers.getRandomIntegers;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity
 */
public abstract class NestedIndexedEntity<P extends Entity> extends IndexedEntity {

    @JsonBackReference
    private P parentEntity;

    public NestedIndexedEntity(P parentEntity, int index) {
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
        return simpleClassNameHashCode() * super.hashCode() + getParentEntity().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.getParentEntity().equals(((NestedIndexedEntity) obj).getParentEntity());
    }

    public int indexBasedRandomInt(int bound) {
        return getRandomIntegers( // for seed of random sequence use parent's hashcode + class name hashcode
                getParentEntity().hashCode() + simpleClassNameHashCode(), bound).get(getIndex());
    }

    public boolean indexBasedRandomBool(int truePercentage) {
        return getRandomBooleans( // for seed of random sequence use parent's hashcode + class name hashcode
                getParentEntity().hashCode() + simpleClassNameHashCode(), truePercentage).get(getIndex());
    }

    public boolean indexBasedRandomBool() {
        return indexBasedRandomBool(50);
    }

}
