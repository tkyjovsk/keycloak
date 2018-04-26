package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class Resource extends NestedIndexedEntity<ResourceServer> {

//    @JsonBackReference
    private ResourceOwner owner;

    private String name;
    private String displayName;
    private String type;
    private String uri;
    private boolean ownerManagedAccess;
    private Map<String, String> attributes;

    public Resource(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    @JsonProperty("_id")
    public synchronized String getId() {
        return super.getId();
    }
    
    @JsonBackReference
    public ResourceServer getResourceServer() {
        return getParentEntity();
    }

    public ResourceOwner getOwner() {
        return owner;
    }

    public void setOwner(ResourceOwner owner) {
        this.owner = owner;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isOwnerManagedAccess() {
        return ownerManagedAccess;
    }

    public void setOwnerManagedAccess(boolean ownerManagedAccess) {
        this.ownerManagedAccess = ownerManagedAccess;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

}
