package org.keycloak.performance.dataset.idm;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class Client extends NestedIndexedEntity<Realm, ClientRepresentation>
        implements ResourceFacade<ClientRepresentation> {

    private List<ClientRole> clientRoles;
    private ResourceServer resourceServer;

    public Client(Realm realm, int index, ClientRepresentation representation) {
        super(realm, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getClientId();
    }

    public Realm getRealm() {
        return getParentEntity();
    }

    public List<ClientRole> getClientRoles() {
        return clientRoles;
    }

    public void setClientRoles(List<ClientRole> clientRoles) {
        this.clientRoles = clientRoles;
    }

    public ResourceServer getResourceServer() {
        return resourceServer;
    }

    public void setResourceServer(ResourceServer resourceServer) {
        this.resourceServer = resourceServer;
    }

    @Override
    public ClientRepresentation read(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).clients().findByClientId(getRepresentation().getClientId()).get(0);
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).clients().create(getRepresentation());
    }

    public ClientResource clientResource(Keycloak adminClient) {
        return getRealm().realmResource(adminClient).clients().get(getRepresentation().getId());
    }

    @Override
    public void update(Keycloak adminClient) {
        clientResource(adminClient).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        clientResource(adminClient).remove();
    }

}
