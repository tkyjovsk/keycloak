package org.keycloak.performance.dataset.idm.authorization;

/**
 *
 * @author tkyjovsk
 */
public class JsPolicy extends Policy {

    public JsPolicy(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public String getType() {
        return "js";
    }

}
