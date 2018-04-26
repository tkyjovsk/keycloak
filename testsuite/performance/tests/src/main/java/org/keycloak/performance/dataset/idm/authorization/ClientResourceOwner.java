package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.idm.Client;

/**
 *
 * @author tkyjovsk
 */
public class ClientResourceOwner extends ResourceOwner<Client> {

    public ClientResourceOwner(Client client) {
        super(client);
    }

    @JsonBackReference
    public Client getClient() {
        return getParentEntity();
    }

    public String getName() {
        return getClient().getName();
    }

}
