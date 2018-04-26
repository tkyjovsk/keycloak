package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author tkyjovsk
 */
public class ClientRole extends Role<Client> {

    public ClientRole(Client client, int index) {
        super(client, index);
    }

    @JsonIgnore
    public Client getClient() {
        return getParentEntity();
    }

}
