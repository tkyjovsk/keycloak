package org.keycloak.performance.dataset;

import static org.keycloak.performance.iteration.RandomBooleans.getRandomBooleans;
import static org.keycloak.performance.iteration.RandomIntegers.getRandomIntegers;

/**
 *
 * @author tkyjovsk
 * @param <PE> parent entity
 */
public abstract class NestedIndexedEntity<PE extends Entity, R> extends NestedEntity<PE, R> {

    private final int index;
    private final int seed;

    public NestedIndexedEntity(PE parentEntity, int index, R representation) {
        super(parentEntity, representation);
        validateInt().minValue(index, 0);
        this.index = index;
        this.seed = getParentEntity().hashCode() + simpleClassName().hashCode();
    }

    public synchronized final int getIndex() {
        validateInt().minValue(index, 0);
        return index;
    }

    public synchronized int getSeed() {
        return seed;
    }

    @Override
    public synchronized int hashCode() {
        return simpleClassName().hashCode() * getIndex() + getParentEntity().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    public synchronized int indexBasedRandomInt(int bound) {
        return getRandomIntegers(getSeed(), bound).get(getIndex());
    }

    public synchronized boolean indexBasedRandomBool(int truePercentage) {
        return getRandomBooleans(getSeed(), truePercentage).get(getIndex());
    }

    public synchronized boolean indexBasedRandomBool() {
        return indexBasedRandomBool(50);
    }

}
