package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static org.keycloak.performance.iteration.RandomIntegers.getRandomIntegers;
import static org.keycloak.performance.iteration.RandomBooleans.getRandomBooleans;

/**
 *
 * @author tkyjovsk
 */
public abstract class IndexedEntity extends Entity {

    @JsonIgnore
    private final int index;

    public IndexedEntity(int index) {
        this.index = index;
    }

    public final int getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return this.getClass().getSimpleName().hashCode() * 3 + this.getIndex();
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other != null
                && this.getClass() == other.getClass()
                && this.getIndex() == ((IndexedEntity) other).getIndex());
    }

    public int indexBasedRandomInt(int bound) {
        return getRandomIntegers(hashCode(), bound).get(index);
    }

    public boolean indexBasedRandomBool() {
        return indexBasedRandomBool(50);
    }

    public boolean indexBasedRandomBool(int truePercentage) {
        return getRandomBooleans(hashCode(), truePercentage).get(index);
    }

}
