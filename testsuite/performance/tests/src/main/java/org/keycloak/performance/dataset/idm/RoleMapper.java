package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public abstract class RoleMapper extends NestedIndexedEntity<Realm> {
    
    public RoleMapper(Realm realm, int index) {
        super(realm, index);
    }
    
    @JsonIgnore
    public Realm getRealm() {
        return getParentEntity();
    }
    
}
