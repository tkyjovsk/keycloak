package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ClientResourceOwner extends ResourceOwner<Client> {

    public ClientResourceOwner(Client client) {
        super(client);
    }

    public Client getClient() {
        return getParentEntity();
    }

    @Override
    public ResourceOwnerRepresentation getRepresentation() {
        ResourceOwnerRepresentation r = super.getRepresentation();
        r.setId(getClient().getRepresentation().getId());
        r.setName(getClient().getRepresentation().getName());
        return r;
    }

}
