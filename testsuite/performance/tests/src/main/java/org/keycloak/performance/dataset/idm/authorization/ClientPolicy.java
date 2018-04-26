package org.keycloak.performance.dataset.idm.authorization;

/**
 *
 * @author tkyjovsk
 */
public class ClientPolicy extends Policy {

    public ClientPolicy(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public String getType() {
        return "client";
    }

}
