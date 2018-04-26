package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import org.keycloak.performance.dataset.NestedEntity;

/**
 *
 * @author tkyjovsk
 */
public class User extends NestedEntity<Realm> {

    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes;
    
    @JsonIgnore
    private List<RealmRole> realmRoles;
    @JsonIgnore
    private List<ClientRole> clientRoles;

    public User(Realm realm, int index) {
        super(realm, index);
    }

    @Override
    public String toString() {
        return getUsername();
    }

    @JsonIgnore
    public Realm getRealm() {
        return getParentEntity();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
