package org.keycloak.performance.dataset.idm;

import org.keycloak.performance.dataset.IndexedEntity;
import org.keycloak.performance.dataset.NestedEntity;

/**
 *
 * @author tkyjovsk
 * @param <P>
 */
public abstract class Role<P extends IndexedEntity> extends NestedEntity<P> {

    private String name;
    private String description;

    public Role(P parentEntity, int index) {
        super(parentEntity, index);
    }

    @Override
    public String toString() {
        return getName();
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
