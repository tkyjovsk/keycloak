package org.keycloak.performance.dataset.idm;

import org.keycloak.performance.dataset.IndexedEntityM;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.performance.dataset.SearchableAndCreatable;
import org.keycloak.performance.dataset.UpdatableAndDeletable;

/**
 *
 * @author tkyjovsk
 */
public class RealmM extends IndexedEntityM<RealmRepresentation>
        implements SearchableAndCreatable, UpdatableAndDeletable {

    public RealmM(int index) {
        super(index, new RealmRepresentation());
    }

    @Override
    public String searchEndpoint() {
        return "/realms";
    }

    @Override
    public String updateEndpoint() {
        return String.format("%s/%s", searchEndpoint(), getRepresentation().getId());
    }

}
