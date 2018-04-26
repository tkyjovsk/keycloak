package org.keycloak.performance.dataset;

/**
 *
 * @author tkyjovsk
 * @param <P> parent entity
 * @param <M> mapped entity
 */
public abstract class Mapping<P extends IndexedEntity, M extends IndexedEntity> extends NestedIndexedEntity<P> {

    private M mappedEntity;
    
    public Mapping(P parentEntity, int index) {
        super(parentEntity, index);
    }

    public M getMappedEntity() {
        return mappedEntity;
    }

    public void setMappedEntity(M mappedEntity) {
        this.mappedEntity = mappedEntity;
    }

}
