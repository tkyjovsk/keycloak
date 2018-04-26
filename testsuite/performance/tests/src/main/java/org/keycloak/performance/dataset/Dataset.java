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
import org.keycloak.performance.dataset.idm.authorization.Resource;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.RolePolicy;
import org.keycloak.performance.dataset.idm.authorization.Scope;

/**
 *
 * @author tkyjovsk
 */
public class Dataset extends Entity {

    @JsonIgnore
    private List<Realm> realms;

    @JsonIgnore
    private List<User> users; // all realms' users

    @Override
    public int hashCode() {
        return simpleClassNameHashCode();
    }

    public boolean equals(Object other) { // TODO decide if Dataset should be indexed or singleton
        return other instanceof Dataset;
    }

    public List<Realm> getRealms() {
        return realms;
    }

    public void setRealms(List<Realm> realms) {
        this.realms = realms;
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

    public Stream<ResourceServer> resourceServers() {
        return clients().filter(c -> c.isAuthorizationServicesEnabled())
                .map(c -> c.getResourceServer());
    }

    public Stream<Scope> scopes() {
        return resourceServers().map(rs -> rs.getScopes()).flatMap(List::stream);
    }

    public Stream<Resource> resources() {
        return resourceServers().map(rs -> rs.getResources()).flatMap(List::stream);
    }

    public Stream<RolePolicy> rolePolicies() {
        return resourceServers().map(rs -> rs.getRolePolicies()).flatMap(List::stream);
    }

}
