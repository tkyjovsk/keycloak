package org.keycloak.performance.dataset.idm;

import org.keycloak.performance.dataset.IndexedEntityM;
import org.keycloak.performance.dataset.SearchableAndCreatable;
import org.keycloak.performance.dataset.UpdatableAndDeletable;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class UserM extends IndexedEntityM<UserRepresentation>
        implements SearchableAndCreatable, UpdatableAndDeletable {

    public UserM(int index) {
        super(index, new UserRepresentation());
    }

    @Override
    public String searchEndpoint() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String updateEndpoint() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
