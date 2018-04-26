package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.performance.iteration.FlattenedListOfLists;

/**
 *
 * @author tkyjovsk
 */
public class Realm extends NestedIndexedEntity<Dataset> {

    @JsonIgnore
    private List<User> users;
    @JsonIgnore
    private List<Client> clients;
    @JsonIgnore
    private List<RealmRole> realmRoles;

    @JsonIgnore
    private List<ClientRole> clientRoles; // all clients' roles

    private String realm;
    private String displayName;
    private boolean enabled;
    private boolean registrationAllowed;
    private int accessTokenLifespan;
    private String passwordPolicy;

    public Realm(Dataset dataset, int index) {
        super(dataset, index);
    }

    @Override
    public String toString() {
        return getRealm();
    }

    @JsonIgnore
    public Dataset getDataset() {
        return getParentEntity();
    }

    public String getRealm() {
        return realm;
    }
    
    public void setRealm(String realm) {
        this.realm = realm;
    }

    public boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public void setRegistrationAllowed(boolean registrationAllowed) {
        this.registrationAllowed = registrationAllowed;
    }

    public int getAccessTokenLifespan() {
        return accessTokenLifespan;
    }

    public void setAccessTokenLifespan(int accessTokenLifespan) {
        this.accessTokenLifespan = accessTokenLifespan;
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

    public void setClientRoles(FlattenedListOfLists<Client, ClientRole> clientRoles) {
        this.clientRoles = clientRoles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(String passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
