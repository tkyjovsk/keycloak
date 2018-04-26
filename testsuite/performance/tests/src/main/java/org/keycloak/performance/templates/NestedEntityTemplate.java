package org.keycloak.performance.templates;

import org.apache.commons.configuration.Configuration;
import org.keycloak.performance.dataset.IndexedEntity;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity
 * @param <N> nested entity
 */
public abstract class NestedEntityTemplate<P extends IndexedEntity, N extends IndexedEntity> extends EntityTemplate<N> {

    private final EntityTemplate<P> parentEntityTemplate;

    public NestedEntityTemplate(EntityTemplate<P> parentEntityTemplate) {
        super(parentEntityTemplate.getConfiguration(), parentEntityTemplate.getFreemarkerConfiguration());
        this.parentEntityTemplate = parentEntityTemplate;
    }

    public EntityTemplate<P> getParentEntityTemplate() {
        return parentEntityTemplate;
    }

    @Override
    public Configuration getConfiguration() {
        return parentEntityTemplate.getConfiguration();
    }

    @Override
    public freemarker.template.Configuration getFreemarkerConfiguration() {
        return parentEntityTemplate.getFreemarkerConfiguration();
    }

    @Override
    public N produce(int index) {
        throw new UnsupportedOperationException();
    }

    public abstract N produce(P parentEntity, int index);

    public abstract int getEntityCountPerParent();

}
