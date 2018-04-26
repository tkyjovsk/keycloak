package org.keycloak.performance.dataset;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.concurrent.TimeoutException;
import static org.keycloak.performance.util.ValidationUtil.VALIDATE_LONG;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 */
public abstract class Entity {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toJSON() throws IOException {
        return writeValueAsString(this);
    }

    /**
     *
     * @param timeout How long to wait for the {@code id} to be set before
     * throwing {@code TimeoutException}.
     * @param timeUnit
     * @return This entity after its {@code id} has been set.
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public Entity waitForIdNotNullAndGet(long timeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
        VALIDATE_LONG.minValue(timeout, 0);
        long timeoutMillis = MILLISECONDS.convert(timeout, timeUnit);
        long tStart = new Date().getTime();
        while (id == null) {
            long tNow = new Date().getTime();
            if (tNow - tStart > timeoutMillis) {
                throw new TimeoutException(String.format(
                        "Waiting for 'id' of entity '%s' to be set timed out.",
                        this.toString()));
            }
            Thread.sleep(100);
        }
        return this;
    }

}
