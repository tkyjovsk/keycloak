package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.RealmRole;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.representations.idm.RoleRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RealmRoleTemplate extends NestedIndexedEntityTemplate<Realm, RealmRole, RoleRepresentation> {

    private final int realmRolesPerRealm;

    public RealmRoleTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        this.realmRolesPerRealm = getConfiguration().getInt("realmRolesPerRealm", 0);
    }

    @Override
    public int getEntityCountPerParent() {
        return getRealmRolesPerRealm();
    }

    public int getRealmRolesPerRealm() {
        return realmRolesPerRealm;
    }

    @Override
    public void validateConfiguration() {
        // sizing
        logger().info(String.format("realmRolesPerRealm: %s", getRealmRolesPerRealm()));
        validateInt().minValue(getRealmRolesPerRealm(), 0);
    }

    @Override
    public RealmRole newEntity(Realm parentEntity, int index) {
        return new RealmRole(parentEntity, index, new RoleRepresentation());
    }

    @Override
    public void processMappings(RealmRole role) {
    }

}
