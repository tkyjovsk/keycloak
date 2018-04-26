package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.dataset.idm.authorization.Policy;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;

/**
 *
 * @author tkyjovsk
 */
public abstract class PolicyTemplate<P extends Policy> extends NestedIndexedEntityTemplate<ResourceServer, P> {

    public PolicyTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("description");
        registerAttributeTemplate("logic");
        registerAttributeTemplate("decisionStrategy");
    }

}
