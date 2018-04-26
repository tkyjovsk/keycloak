package org.keycloak.performance.dataset;

/**
 *
 * @author tkyjovsk
 */
public interface UpdatableAndDeletable {

    public String updateEndpoint();
    
    public default String deletEndpoint() {
        return updateEndpoint();
    }
    
}
