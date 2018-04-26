package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.IndexedEntity;
import org.keycloak.performance.dataset.StringAttribute;

/**
 *
 * @author tkyjovsk
 * @param <O> owner entity
 */
public abstract class StringAttributeTemplate<O extends IndexedEntity> extends NestedEntityTemplate<O, StringAttribute> {

    public StringAttributeTemplate(EntityTemplate<O> parentEntityTemplate) {
        super(parentEntityTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("value");
    }

    @Override
    public StringAttribute produce(O attributeOwner, int index) {
        StringAttribute<O> attribute = new StringAttribute<>(attributeOwner, index);
        attribute.setName(processAttribute("name", attribute));
        attribute.setValue(processAttribute("value", attribute));
        return attribute;
    }

}
