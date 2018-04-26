package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.RealmRole;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;

/**
 *
 * @author tkyjovsk
 */
public class RealmRoleTemplate extends NestedIndexedEntityTemplate<Realm, RealmRole> {

    private final int realmRolesPerRealm;

    public RealmRoleTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("description");
        this.realmRolesPerRealm = configuration.getInt("realmRolesPerRealm", 0);
    }

    @Override
    public synchronized RealmRole produce(Realm realm, int index) {
        RealmRole realmRole = new RealmRole(realm, index);
        realmRole.setName(processAttribute("name", realmRole));
        realmRole.setDescription(processAttribute("description", realmRole));
        return realmRole;
    }

    @Override
    public int getEntityCountPerParent() {
        return getRealmRolesPerRealm();
    }

    public int getRealmRolesPerRealm() {
        return realmRolesPerRealm;
    }

    @Override
    public void validateSizeConfiguration() {
        // sizing
        logger().info(String.format("realmRolesPerRealm: %s", getRealmRolesPerRealm()));
        validateInt().minValue(getRealmRolesPerRealm(), 0);
    }

}
