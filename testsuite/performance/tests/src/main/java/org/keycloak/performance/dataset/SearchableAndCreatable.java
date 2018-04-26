package org.keycloak.performance.dataset;

/**
 *
 * @author tkyjovsk
 */
public interface SearchableAndCreatable {

    public String searchEndpoint();
    
    public default String createEndpoint() {
        return searchEndpoint();
    }
    
}
