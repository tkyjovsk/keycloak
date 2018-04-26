package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.keycloak.performance.dataset.idm.Realm;
import java.util.List;
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

}
