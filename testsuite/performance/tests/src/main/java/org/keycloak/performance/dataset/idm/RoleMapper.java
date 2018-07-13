package org.keycloak.performance.dataset.idm;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public abstract class RoleMapper<R> extends NestedIndexedEntity<Realm, R> {

    public RoleMapper(Realm realm, int index, R representation) {
        super(realm, index, representation);
    }

    public Realm getRealm() {
        return getParentEntity();
    }

    public abstract RoleMappingResource roleMappingResource(Keycloak adminClient);

}
