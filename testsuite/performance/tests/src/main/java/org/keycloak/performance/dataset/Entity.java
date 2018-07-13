package org.keycloak.performance.dataset;

import java.io.IOException;
import org.apache.commons.lang.Validate;
import org.keycloak.performance.util.Loggable;
import org.keycloak.performance.util.Validating;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 * @param <R> representation type
 */
public abstract class Entity<R> implements Loggable, Validating {
    
    private R representation;
    
    public Entity(R representation) {
        setRepresentation(representation);
    }
    
    public R getRepresentation() {
        return representation;
    }
    
    public void setRepresentation(R representation) {
        Validate.notNull(representation);
        this.representation = representation;
    }
    
    public String toJSON() throws IOException {
        return writeValueAsString(getRepresentation());
    }
    
    public String simpleClassName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public int hashCode() {
        return simpleClassName().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        return this == other || (other != null
                && this.getClass() == other.getClass()
                && this.hashCode() == ((Entity) other).hashCode());
    }
    
}
