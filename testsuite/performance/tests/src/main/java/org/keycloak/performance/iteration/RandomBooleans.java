package org.keycloak.performance.iteration;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;
import static org.keycloak.performance.iteration.RandomIntegers.getRandomIntegers;
import static org.keycloak.performance.util.ValidationUtil.VALIDATE_INT;

/**
 *
 * @author tkyjovsk
 */
public class RandomBooleans extends AbstractList<Boolean> {

    private final RandomIntegers randomIntegers;
    private final int truePercentage;
    
    private static final Map<Integer, Map<Integer, RandomBooleans>> RANDOM_BOOLS_CACHE = new HashMap<>();
    
    /**
     * 
     * @param seed Random sequence seed.
     * @param truePercentage Percentage of the sequence values which should be true. Valid range is 0-100.
     */
    public RandomBooleans(int seed, int truePercentage) {
        randomIntegers = getRandomIntegers(seed, 100);
        VALIDATE_INT.isInRange(truePercentage, 0, 100);
        this.truePercentage = truePercentage;
    }

    public RandomBooleans(int seed) {
        this(seed, 50);
    }

    @Override
    public Boolean get(int index) {
        return randomIntegers.get(index) < truePercentage;
    }

    @Override
    public int size() {
        return Integer.MAX_VALUE;
    }
    
    public static synchronized RandomBooleans getRandomBooleans(int seed, int percent) {
        return RANDOM_BOOLS_CACHE
                .computeIfAbsent(seed, (s) -> new HashMap<>())
                .computeIfAbsent(percent, (p) -> new RandomBooleans(seed, p));
    }
        
}
