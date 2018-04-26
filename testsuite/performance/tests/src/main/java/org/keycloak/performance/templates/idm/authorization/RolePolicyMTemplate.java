package org.keycloak.performance.templates.idm.authorization;

import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.keycloak.performance.dataset.idm.authorization.RolePolicyM;
import org.keycloak.performance.templates.IndexedEntityMTemplate;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicyMTemplate extends IndexedEntityMTemplate<RolePolicyRepresentation, RolePolicyM> {

    public RolePolicyMTemplate(Configuration configuration, freemarker.template.Configuration freemarkerConfiguration) throws IOException {
        super(configuration, freemarkerConfiguration);
    }

    @Override
    public RolePolicyM newEM(int index) {
        return new RolePolicyM(index, new RolePolicyRepresentation());
    }

}
