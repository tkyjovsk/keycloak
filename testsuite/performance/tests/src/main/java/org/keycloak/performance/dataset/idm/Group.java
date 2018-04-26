package org.keycloak.performance.dataset.idm;

/**
 *
 * @author tkyjovsk
 */
public class Group extends RoleMapper {
    
    private String name;
    
    public Group(Realm realm, int index) {
        super(realm, index);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
