package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedEntity;

/**
 *
 * @author tkyjovsk
 * @param <PE>
 * @param <NE>
 * @param <R>
 */
public abstract class NestedEntityTemplate<PE extends Entity, NE extends NestedEntity<PE, R>, R>
        extends EntityTemplate<NE, R> {

    private final EntityTemplate parentEntityTemplate;

    public NestedEntityTemplate(EntityTemplate parentEntityTemplate) {
        super(parentEntityTemplate.getConfiguration());
        this.parentEntityTemplate = parentEntityTemplate;
    }

    public EntityTemplate getParentEntityTemplate() {
        return parentEntityTemplate;
    }

    @Override
    public NE newEntity() {
        throw new UnsupportedOperationException("Nested entity requires a parent entity.");
    }

    @Override
    public NE produce() {
        throw new UnsupportedOperationException("Nested entity requires a parent entity.");
    }

    public abstract NE newEntity(PE parentEntity);

    public NE produce(PE parentEntity) {
        return processEntity(newEntity(parentEntity));
    }


}
