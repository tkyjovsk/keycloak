package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.apache.commons.lang.Validate;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.IndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class ResourceType extends IndexedEntity {

    @JsonBackReference
    private Dataset dataset;

    private String uri;

    public ResourceType(Dataset dataset, int index) {
        super(index);
        Validate.notNull(dataset);
        this.dataset = dataset;
    }

    @Override
    public String toString() {
        return uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

}
