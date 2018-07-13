package org.keycloak.performance.dataset.idm.authorization;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ClientPolicy extends Policy<ClientPolicyRepresentation> {

    private List<Client> clients;

    public ClientPolicy(ResourceServer resourceServer, int index, ClientPolicyRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public ClientPolicyRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().client().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().client().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().client().findById(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().client().findById(getId()).remove();
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

}
