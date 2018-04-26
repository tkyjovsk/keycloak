package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.performance.iteration.CachingList;

/**
 * A wrapper list for NestedIndexedEntityTemplate which delegates to the template if requested element is absent in cache.
 * 
 * @author tkyjovsk
 * @param <P> parent entity type
 * @param <N> child entity type
 */
public class NestedIndexedEntityTemplateWrapperList<P extends Entity, N extends NestedIndexedEntity> extends CachingList<N> {

    P parentEntity;
    NestedIndexedEntityTemplate<P, N> nestedEntityTemplate;

    public NestedIndexedEntityTemplateWrapperList(P parentEntity, NestedIndexedEntityTemplate<P, N> nestedEntityTemplate) {
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
