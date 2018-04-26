package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.idm.User;

/**
 *
 * @author tkyjovsk
 */
public class UserResourceOwner extends ResourceOwner<User> {

    public UserResourceOwner(User user) {
        super(user);
    }
    
    @JsonBackReference
    public User getUser() {
        return getParentEntity();
    }

    public String getName() {
        return getUser().getUsername();
    }
    
}
