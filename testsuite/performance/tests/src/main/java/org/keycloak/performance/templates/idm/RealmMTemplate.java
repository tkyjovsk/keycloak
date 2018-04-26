package org.keycloak.performance.templates.idm;

import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.keycloak.performance.dataset.idm.RealmM;
import org.keycloak.performance.templates.IndexedEntityMTemplate;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RealmMTemplate extends IndexedEntityMTemplate<RealmRepresentation, RealmM> {

    public RealmMTemplate(Configuration configuration, freemarker.template.Configuration freemarkerConfiguration) throws IOException {
        super(configuration, freemarkerConfiguration);
    }

    @Override
    public RealmM newEM(int index) {
        return new RealmM(index);
    }

}
