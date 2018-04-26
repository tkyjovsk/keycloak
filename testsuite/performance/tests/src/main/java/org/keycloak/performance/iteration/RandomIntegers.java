package org.keycloak.performance.iteration;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.keycloak.performance.util.Validating;

/**
 *
 * @author tkyjovsk
 */
public class RandomIntegers extends AbstractList<Integer> implements Validating {

    protected final List<Integer> randoms;
    protected final int seed;
    protected final int bound;

    private final Random random;

    private static final Map<Integer, Map<Integer, RandomIntegers>> RANDOM_INTS_CACHE = new HashMap<>();

    public RandomIntegers(int seed, int bound) {
        this.randoms = new ArrayList<>();
        this.seed = seed;
        validateInt().minValue(bound, 1);
        this.bound = bound;
        this.random = new Random(seed);
    }

    protected int nextInt() {
        return bound == 0 ? random.nextInt() : random.nextInt(bound);
    }

    private void generateRandomsUpTo(int index) {
        int mIndex = randoms.size() - 1;
        for (int i = mIndex; i < index; i++) {
            randoms.add(nextInt());
        }
    }

    @Override
    public Integer get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        generateRandomsUpTo(index);
        return randoms.get(index);
    }

    @Override
    public int size() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.seed;
        hash = 41 * hash + this.bound;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RandomIntegers other = (RandomIntegers) obj;
        if (this.seed != other.seed) {
            return false;
        }
        return this.bound == other.bound;
    }

    public static synchronized RandomIntegers getRandomIntegers(int seed, int bound) {
        return RANDOM_INTS_CACHE
                .computeIfAbsent(seed, (s) -> new HashMap<>())
                .computeIfAbsent(bound, (b) -> new RandomIntegers(seed, b));
    }

}
