package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.idm.authorization.ResourceType;
import org.keycloak.performance.templates.NestedEntityTemplate;
import org.keycloak.performance.templates.DatasetTemplate;
import static org.keycloak.performance.util.ValidationUtil.VALIDATE_INT;

/**
 *
 * @author tkyjovsk
 */
public class ResourceTypeTemplate extends NestedEntityTemplate<Dataset, ResourceType> {

    private final int resourceTypes;

    public ResourceTypeTemplate(DatasetTemplate datasetTemplate) {
        super(datasetTemplate);
        registerAttributeTemplate("uri");
        this.resourceTypes = configuration.getInt("resourceTypes");
    }
    
    @Override
    public ResourceType produce(Dataset dataset, int index) {
        ResourceType resourceType = new ResourceType(dataset, index);
        resourceType.setUri(processAttribute("uri", resourceType));
        return resourceType;
    }

    public int getResourceTypes() {
        return resourceTypes;
    }

    @Override
    public void validateSizeConfiguration() {
        logger.info(String.format("resourceTypes: %s", resourceTypes));
        VALIDATE_INT.minValue(resourceTypes, 0);
    }

    @Override
    public int getEntityCountPerParent() {
        return resourceTypes;
    }

}
