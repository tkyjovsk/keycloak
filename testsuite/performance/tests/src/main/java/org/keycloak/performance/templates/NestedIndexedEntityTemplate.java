package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity
 * @param <N> nested entity
 */
public abstract class NestedIndexedEntityTemplate<P extends Entity, N extends NestedIndexedEntity> extends NestedEntityTemplate<P, N> {

    public NestedIndexedEntityTemplate(EntityTemplate<P> parentEntityTemplate) {
        super(parentEntityTemplate);
    }

    @Override
    public N produce(P parentEntity) {
        throw new UnsupportedOperationException();
    }

    public abstract N produce(P parentEntity, int index);

    public abstract int getEntityCountPerParent();

}
