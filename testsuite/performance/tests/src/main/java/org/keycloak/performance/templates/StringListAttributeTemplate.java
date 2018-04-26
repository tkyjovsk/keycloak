package org.keycloak.performance.templates;

import org.keycloak.performance.dataset.IndexedEntity;
import org.keycloak.performance.dataset.StringListAttribute;
import static org.keycloak.performance.util.StringUtil.parseStringList;

/**
 *
 * @author tkyjovsk
 * @param <O> owner entity
 */
public abstract class StringListAttributeTemplate<O extends IndexedEntity> extends NestedEntityTemplate<O, StringListAttribute> {

    public StringListAttributeTemplate(EntityTemplate<O> parentEntityTemplate) {
        super(parentEntityTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("value");
    }

    @Override
    public StringListAttribute produce(O attributeOwner, int index) {
        StringListAttribute<O> attribute = new StringListAttribute<>(attributeOwner, index);
        attribute.setName(processAttribute("name", attribute));
        attribute.setValue(parseStringList(processAttribute("value", attribute)));
        return attribute;
    }

}
