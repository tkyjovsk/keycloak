package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.keycloak.performance.dataset.idm.Realm;
import java.util.List;
import java.util.stream.Stream;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.dataset.idm.ClientRoleMappings;
import org.keycloak.performance.dataset.idm.Credential;
import org.keycloak.performance.dataset.idm.RealmRole;
import org.keycloak.performance.dataset.idm.RealmRoleMappings;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.performance.dataset.idm.authorization.ResourceType;

/**
 *
 * @author tkyjovsk
 */
public class Dataset extends IndexedEntity {

    @JsonIgnore
    private List<Realm> realms;

    @JsonIgnore
    private List<ResourceType> resourceTypes;

    @JsonIgnore
    private List<User> users; // all realms' users

    public Dataset(int index) {
        super(index);
    }

    @Override
    public String toString() {
        return String.format("dataset_%s", getIndex());
    }

    public List<Realm> getRealms() {
        return realms;
    }

    public void setRealms(List<Realm> realms) {
        this.realms = realms;
    }

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

//    public Stream<? extends Entity> entities() {
//        return Stream.of(
//                realms(),
//                resourceTypes(),
//                realmRoles(),
//                clients(),
//                clientRoles(),
//                users(),
//                userRealmRoleMappings(),
//                userClientRoleMappings()
//        )
//                .reduce(Stream::concat)
//                .orElseGet(Stream::empty);
//    }

    public Stream<Realm> realms() {
        return realms.stream();
    }

    public Stream<ResourceType> resourceTypes() {
        return resourceTypes.stream();
    }

    public Stream<RealmRole> realmRoles() {
        return realms.stream().map(Realm::getRealmRoles).flatMap(List::stream);
    }

    public Stream<Client> clients() {
        return realms.stream().map(Realm::getClients).flatMap(List::stream);
    }

    public Stream<ClientRole> clientRoles() {
        return clients().map(Client::getClientRoles).flatMap(List::stream);
    }

    public Stream<User> users() {
        return realms.stream().map(Realm::getUsers).flatMap(List::stream);
    }

    public Stream<Credential> credentials() {
        return users().map(User::getCredentials).flatMap(List::stream);
    }

    public Stream<RealmRoleMappings> userRealmRoleMappings() {
        return users().map(User::getRealmRoleMappings);
    }

    public Stream<ClientRoleMappings> userClientRoleMappings() {
        return users().map(User::getClientRoleMappingsList).flatMap(List::stream);
    }

}
