package org.keycloak.performance.dataset.idm;

import java.util.stream.Stream;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.EntityTest;

/**
 *
 * @author tkyjovsk
 */
public class ClientRoleMappingsTest extends EntityTest<ClientRoleMappings> {

    @Override
    public Stream<ClientRoleMappings> entityStream(Dataset dataset) {
        return dataset.userClientRoleMappings();
    }

}
