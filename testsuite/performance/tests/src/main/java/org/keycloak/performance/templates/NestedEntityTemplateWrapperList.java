package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.IndexedEntity;
import org.keycloak.performance.iteration.CachingList;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity type
 * @param <N> child entity type
 */
public class NestedEntityTemplateWrapperList<P extends IndexedEntity, N extends IndexedEntity> extends CachingList<N> {

    P parentEntity;
    NestedEntityTemplate<P, N> nestedEntityTemplate;

    public NestedEntityTemplateWrapperList(P parentEntity, NestedEntityTemplate<P, N> nestedEntityTemplate) {
        this.parentEntity = parentEntity;
        this.nestedEntityTemplate = nestedEntityTemplate;
    }

    @Override
    public N compute(int index) {
        return nestedEntityTemplate.produce(parentEntity, index);
    }

    @Override
    public int size() {
        return nestedEntityTemplate.getEntityCountPerParent();
    }

}
