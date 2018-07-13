package org.keycloak.performance.dataset.attr;

import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 * @param <PE>
 */
public abstract class Attribute<PE extends Entity, R extends AttributeRepresentation> extends NestedIndexedEntity<PE, R> {

    public Attribute(PE attributeOwner, int index, R representation) {
        super(attributeOwner, index, representation);
    }
}
