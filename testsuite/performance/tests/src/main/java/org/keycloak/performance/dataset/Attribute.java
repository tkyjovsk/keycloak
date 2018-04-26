package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author tkyjovsk
 * @param <P> attribute parent entity
 * @param <AV> attribute value (either String or List&lt;String&gt;)
 */
public abstract class Attribute<P extends IndexedEntity, AV> extends NestedEntity<P> {

    private String name;
    private AV value;

    public Attribute(P parentEntity, int index) {
        super(parentEntity, index);
    }
    
    @JsonIgnore
    public P getAttributeOwner() {
        return getParentEntity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AV getValue() {
        return value;
    }

    public void setValue(AV value) {
        this.value = value;
    }
    
}
