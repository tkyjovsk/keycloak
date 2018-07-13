package org.keycloak.performance.dataset.idm;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class Realm extends NestedIndexedEntity<Dataset, RealmRepresentation>
        implements ResourceFacade<RealmRepresentation> {

    private List<Client> clients;
    private List<RealmRole> realmRoles;
    private List<User> users;
    private List<Group> groups;

    private List<ClientRole> clientRoles; // all clients' roles
    private List<ResourceServer> resourceServers;

    public Realm(Dataset dataset, int index, RealmRepresentation representation) {
        super(dataset, index, representation);
    }

    @Override
    public String toString() {
        return getRepresentation().getRealm();
    }

    public Dataset getDataset() {
        return getParentEntity();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<RealmRole> getRealmRoles() {
        return realmRoles;
    }

    public void setRealmRoles(List<RealmRole> realmRoles) {
        this.realmRoles = realmRoles;
    }

    public List<ClientRole> getClientRoles() {
        return clientRoles;
    }

    public void setClientRoles(List<ClientRole> clientRoles) {
        this.clientRoles = clientRoles;
    }

    public List<ResourceServer> getResourceServers() {
        return resourceServers;
    }

    public void setResourceServers(List<ResourceServer> resourceServers) {
        this.resourceServers = resourceServers;
    }

    @Override
    public RealmRepresentation read(Keycloak adminClient) {
        return adminClient.realms().realm(getRepresentation().getRealm()).toRepresentation();
    }

    @Override
    public Response create(Keycloak adminClient) {
        adminClient.realms().create(getRepresentation());
        return null;
    }

    public RealmResource realmResource(Keycloak adminClient) {
        return adminClient.realm(getRepresentation().getRealm());
    }

    @Override
    public void update(Keycloak adminClient) {
        realmResource(adminClient).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        realmResource(adminClient).remove();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}
