package org.keycloak.performance.iteration;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author tkyjovsk
 * @param <T>
 */
public abstract class CachingList<T> extends AbstractList<T> {

    private final Map<Integer, T> cache = new HashMap<>(); // TODO implement LRU caching, parametrized capacity

    @Override
    public T get(int index) {
        return cache.computeIfAbsent(index, (i) -> compute(i));
    }

    public abstract T compute(int index);

}
