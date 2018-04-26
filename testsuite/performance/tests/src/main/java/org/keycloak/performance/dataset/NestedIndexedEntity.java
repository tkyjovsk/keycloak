package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static org.keycloak.performance.iteration.RandomBooleans.getRandomBooleans;
import static org.keycloak.performance.iteration.RandomIntegers.getRandomIntegers;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity
 */
public abstract class NestedIndexedEntity<P extends Entity> extends NestedEntity<P> {

    @JsonIgnore
    private final int index;

    public NestedIndexedEntity(P parentEntity, int index) {
        super(parentEntity);
        validateInt().minValue(index, 0);
        this.index = index;
    }

    public final int getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return simpleClassNameHashCode() * getIndex() + getParentEntity().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    public int indexBasedSeed() { // for seed of random sequence use parent's hashcode + class name hashcode
        return getParentEntity().hashCode() + simpleClassNameHashCode();
    }

    public int indexBasedRandomInt(int bound) {
        return getRandomIntegers(indexBasedSeed(), bound).get(getIndex());
    }

    public boolean indexBasedRandomBool(int truePercentage) {
        return getRandomBooleans(indexBasedSeed(), truePercentage).get(getIndex());
    }

    public boolean indexBasedRandomBool() {
        return indexBasedRandomBool(50);
    }

}
