package org.keycloak.performance.dataset;

import java.io.IOException;
import org.jboss.logging.Logger;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 */
public abstract class Entity {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private String id;

    public synchronized String getId() {
        return id;
    }

    public synchronized void setId(String id) {
        this.id = id;
//        logger.info("setting id for '" + this + "': " + id);
    }

    public String toJSON() throws IOException {
        return writeValueAsString(this);
    }
    
    public int simpleClassNameHashCode() {
        return this.getClass().getSimpleName().hashCode();
    }

}
