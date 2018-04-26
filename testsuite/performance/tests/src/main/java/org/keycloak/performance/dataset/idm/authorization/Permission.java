package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public abstract class Permission extends NestedIndexedEntity<ResourceServer> {

    private String name;
    private String description;

    public Permission(ResourceServer resourceServer, int index) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
