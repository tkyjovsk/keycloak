package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public abstract class NestedIndexedEntityTemplate<PE extends Entity, NIE extends NestedIndexedEntity<PE, R>, R>
        extends NestedEntityTemplate<PE, NIE, R> {

    public NestedIndexedEntityTemplate(EntityTemplate parentEntityTemplate) {
        super(parentEntityTemplate);
    }

    public abstract int getEntityCountPerParent();

    @Override
    public NIE newEntity(PE parentEntity) {
        throw new UnsupportedOperationException("Nested indexed entity requires a parent entity and index.");
    }

    @Override
    public NIE produce(PE parentEntity) {
        throw new UnsupportedOperationException("Nested indexed entity requires a parent entity and index.");
    }

    public abstract NIE newEntity(PE parentEntity, int index);

    public NIE produce(PE parentEntity, int index) {
        return processEntity(newEntity(parentEntity, index));
    }
}
