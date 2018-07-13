package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.idm.User;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class UserResourceOwner extends ResourceOwner<User> {

    public UserResourceOwner(User user) {
        super(user);
    }

    public User getUser() {
        return getParentEntity();
    }

    @Override
    public ResourceOwnerRepresentation getRepresentation() {
        ResourceOwnerRepresentation r = super.getRepresentation();
        r.setId(getUser().getRepresentation().getId());
        r.setName(getUser().getRepresentation().getUsername());
        return r;
    }

}
