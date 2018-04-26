package org.keycloak.performance.dataset.idm.authorization;

/**
 *
 * @author tkyjovsk
 */
public class UserPolicy extends Policy {
    
    public UserPolicy(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public String getType() {
        return "user";
    }

}
