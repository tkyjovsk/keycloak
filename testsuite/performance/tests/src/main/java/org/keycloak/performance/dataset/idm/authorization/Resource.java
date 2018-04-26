package org.keycloak.performance.dataset.idm.authorization;

import java.util.Map;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.IndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class Resource extends IndexedEntity {

    private Client resourceServer;
    private String resourceOwner;

    private String name;
    private String displayName;
    private String type;
    private String uri;
    private boolean ownerManagedAccess;
    private Map<String, String> attributes;

    public Resource(Client client, int index) {
        super(index);
        if (client == null) {
            throw new IllegalArgumentException();
        }
        this.resourceServer = client;
    }

    public Client getResourceServer() {
        return resourceServer;
    }

    public void setResourceServer(Client resourceServer) {
        this.resourceServer = resourceServer;
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

    @Override
    public int hashCode() {
        return 73 * super.hashCode() + getResourceServer().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.getResourceServer().equals(((Resource) obj).getResourceServer());
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getResourceOwner() {
        return resourceOwner;
    }

    public void setResourceOwner(String resourceOwner) {
        this.resourceOwner = resourceOwner;
    }

}
