package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author tkyjovsk
 */
public class User extends RoleMapper {

    private String username;
    private boolean enabled;
    @JsonIgnore
    private String password;
    
    @JsonIgnore
    private List<Credential> credentials;
    
    private String email;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes;

    @JsonIgnore
    private List<RealmRole> realmRoles;
    @JsonIgnore
    private RealmRoleMappings realmRoleMappings;
    
    @JsonIgnore
    private List<ClientRole> clientRoles;
    @JsonIgnore
    private List<ClientRoleMappings> clientRoleMappingsList;

    public User(Realm realm, int index) {
        super(realm, index);
    }

    @Override
    public String toString() {
        return getUsername();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public List<RealmRole> getRealmRoles() {
        return realmRoles;
    }

    public void setRealmRoles(List<RealmRole> realmRoles) {
        this.realmRoles = realmRoles;
        // re-create realm role mappings
        realmRoleMappings = new RealmRoleMappings(this);
        realmRoleMappings.setRoles(realmRoles);
    }

    public List<ClientRole> getClientRoles() {
        return clientRoles;
    }

    public void setClientRoles(List<ClientRole> clientRoles) {
        this.clientRoles = clientRoles;
        // re-create client role mappings list
        clientRoleMappingsList = new LinkedList<>();
        List<Client> clients = clientRoles.stream().map(ClientRole::getClient).distinct().collect(toList());
        clients.forEach((client) -> {
            ClientRoleMappings crm = new ClientRoleMappings(this, client);
            List<ClientRole> roles = clientRoles.stream().filter(clientRole
                    -> client.equals(clientRole.getClient()))
                    .collect(toList());
            crm.setRoles(roles);
            clientRoleMappingsList.add(crm);
        });
    }

    public RealmRoleMappings getRealmRoleMappings() {
        return realmRoleMappings;
    }

    public List<ClientRoleMappings> getClientRoleMappingsList() {
        return clientRoleMappingsList;
    }

    public List<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<Credential> credentials) {
        this.credentials = credentials;
    }

}
