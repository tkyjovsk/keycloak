package org.keycloak.performance.dataset;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.lang.Validate;
import org.keycloak.performance.util.Loggable;

/**
 *
 * @author tkyjovsk
 */
public interface Representable<R> extends Loggable {

    public R getRepresentation();

    public void setRepresentation(R representation);

    public default void setId(String uuid) {
        if (uuid == null) {
            logger().debug(this.getClass().getSimpleName() + " " + this + " " + " setId " + uuid);
            throw new IllegalArgumentException();
        }
        try {
            Class<R> c = (Class<R>) getRepresentation().getClass();
            Method setId = c.getMethod("setId", String.class);
            setId.invoke(getRepresentation(), uuid);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public default String getIdFromRepresentation(R representation) {
        Validate.notNull(representation);
        try {
            Class<R> c = (Class<R>) representation.getClass();
            Method getId = c.getMethod("getId");
            Validate.notNull(getId);
            return (String) getId.invoke(representation);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    public default String getId() {
        return getIdFromRepresentation(getRepresentation());
    }

}
