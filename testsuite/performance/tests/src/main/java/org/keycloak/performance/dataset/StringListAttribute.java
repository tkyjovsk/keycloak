package org.keycloak.performance.dataset;

import java.util.List;

/**
 *
 * @author tkyjovsk
 * @param <O> owner entity
 */
public class StringListAttribute<O extends Entity> extends Attribute<O, List<String>> {

    public StringListAttribute(O owner, int index) {
        super(owner, index);
    }

}
