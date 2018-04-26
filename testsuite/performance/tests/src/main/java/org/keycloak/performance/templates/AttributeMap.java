package org.keycloak.performance.templates;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.keycloak.performance.dataset.IndexedEntity;
import org.keycloak.performance.dataset.Attribute;

/**
 *
 * @author tkyjovsk
 * @param <O> owner entity
 * @param <A> attribute type
 * @param <AV> attribute value type
 */
public class AttributeMap<O extends IndexedEntity, AV, A extends Attribute<O, AV>>
        extends AbstractMap<String, AV> {

    Set<Entry<String, AV>> entrySet;

    public AttributeMap(List<A> attributes) {

        this.entrySet = new AbstractSet<Entry<String, AV>>() {

            List<A> attrList = attributes;

            @Override
            public Iterator<Entry<String, AV>> iterator() {
                Iterator<A> attributeIterator = attrList.iterator();
                return new Iterator<Entry<String, AV>>() {
                    @Override
                    public boolean hasNext() {
                        return attributeIterator.hasNext();
                    }

                    @Override
                    public Entry<String, AV> next() {
                        A attr = attributeIterator.next();
                        return new SimpleEntry<>(attr.getName(), attr.getValue());
                    }
                };
            }

            @Override
            public int size() {
                return attrList.size();
            }
        };
    }

    @Override
    public Set<Entry<String, AV>> entrySet() {
        return entrySet;
    }

}
