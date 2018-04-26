package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class Scope extends NestedIndexedEntity<ResourceServer> {

    private String name;
    private String displayName;

    public Scope(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public String toString() {
        return getName();
    }
    
    @JsonBackReference
    public ResourceServer getResourceServer() {
        return getParentEntity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
