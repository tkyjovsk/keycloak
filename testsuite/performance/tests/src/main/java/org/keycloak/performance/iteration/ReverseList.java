package org.keycloak.performance.iteration;

import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author tkyjovsk
 */
public class ReverseList<T> extends AbstractList<T> {
    
    private final List<T> orig;
    
    public ReverseList(List<T> orig) {
        this.orig = orig;
    }
    
    @Override
    public T get(int index) {
        return orig.get(orig.size() - 1 - index);
    }
    
    @Override
    public int size() {
        return orig.size();
    }
    
}
