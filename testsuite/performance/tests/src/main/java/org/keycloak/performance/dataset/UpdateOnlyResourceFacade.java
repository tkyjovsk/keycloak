package org.keycloak.performance.dataset;

import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;

/**
 * For entities with no id.
 *
 * @author tkyjovsk
 */
public interface UpdateOnlyResourceFacade<R> extends ResourceFacade<R> {

    @Override
    public default R read(Keycloak adminClient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public default void readAndSetId(Keycloak adminClient) {
    }

    @Override
    public default Response create(Keycloak adminClient) {
        return null;
    }

    @Override
    public default boolean createCheckingForConflict(Keycloak adminClient) {
        return true;
    }

    @Override
    public default void delete(Keycloak adminClient) {
        throw new UnsupportedOperationException();
    }

}
