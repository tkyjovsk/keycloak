package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author tkyjovsk
 */
public class RealmRole extends Role<Realm> {

    public RealmRole(Realm realm, int index) {
        super(realm, index);
    }

    @JsonIgnore
    public Realm getRealm() {
        return getParentEntity();
    }

}
