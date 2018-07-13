package org.keycloak.performance.dataset.idm;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.CredentialRepresentation;
import static org.keycloak.representations.idm.CredentialRepresentation.PASSWORD;
import org.keycloak.performance.dataset.UpdateOnlyResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class Credential extends NestedIndexedEntity<User, CredentialRepresentation>
        implements UpdateOnlyResourceFacade<CredentialRepresentation> {

    public Credential(User user, int index, CredentialRepresentation representation) {
        super(user, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getType();
    }

    public User getUser() {
        return getParentEntity();
    }

    @Override
    public void update(Keycloak adminClient) {
        if (getRepresentation().getType().equals(PASSWORD)) {
            
//            logger().warn("UPDATING PASSWORD");
            
            getUser().userResource(adminClient).resetPassword(getRepresentation());
        } else {
            logger().warn("Cannot reset password. Non-password credetial type.");
        }
    }

}
