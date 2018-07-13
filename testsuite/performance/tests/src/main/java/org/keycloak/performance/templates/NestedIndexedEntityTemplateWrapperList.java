package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.performance.iteration.CachingList;

/**
 * A wrapper list for NestedIndexedEntityTemplate which delegates to the
 * template if requested element is absent in cache.
 *
 * @author tkyjovsk
 * @param <PE> parent entity type
 * @param <NIE> child entity type
 */
public class NestedIndexedEntityTemplateWrapperList<PE extends Entity, NIE extends NestedIndexedEntity<PE, R>, R> extends CachingList<NIE> {

    PE parentEntity;
    NestedIndexedEntityTemplate<PE, NIE, R> nestedEntityTemplate;

    public NestedIndexedEntityTemplateWrapperList(PE parentEntity, NestedIndexedEntityTemplate<PE, NIE, R> nestedEntityTemplate) {
        this.parentEntity = parentEntity;
        this.nestedEntityTemplate = nestedEntityTemplate;
    }

    @Override
    public NIE compute(int index) {
        return nestedEntityTemplate.produce(parentEntity, index);
    }

    @Override
    public int size() {
        return nestedEntityTemplate.getEntityCountPerParent();
    }

}
