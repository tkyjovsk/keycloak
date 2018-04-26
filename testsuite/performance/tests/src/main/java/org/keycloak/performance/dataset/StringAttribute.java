package org.keycloak.performance.dataset;

/**
 *
 * @author tkyjovsk
 * @param <O> owner entity
 */
public class StringAttribute<O extends Entity> extends Attribute<O, String> {

    public StringAttribute(O owner, int index) {
        super(owner, index);
    }

}
