package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class Credential extends NestedIndexedEntity<User> {
    
    private String type;
    private String value;
    private boolean temporary;

    public Credential(User user, int index) {
        super(user, index);
    }
    
    @JsonIgnore
    public User getUser() {
        return getParentEntity();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
    
}
