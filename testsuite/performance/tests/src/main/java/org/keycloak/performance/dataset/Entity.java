package org.keycloak.performance.dataset;

import java.io.IOException;
import org.keycloak.performance.util.Loggable;
import org.keycloak.performance.util.Validating;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 */
public abstract class Entity implements Loggable, Validating {

    private String id;

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public String toJSON() throws IOException {
        return writeValueAsString(this);
    }

    public int simpleClassNameHashCode() {
        return this.getClass().getSimpleName().hashCode();
    }

    @Override
    public int hashCode() {
        return simpleClassNameHashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other != null
                && this.getClass() == other.getClass()
                && this.hashCode() == ((Entity) other).hashCode());
    }

}
