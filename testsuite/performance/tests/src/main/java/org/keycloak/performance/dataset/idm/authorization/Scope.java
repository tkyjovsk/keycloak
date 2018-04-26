package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.IndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class Scope extends IndexedEntity {

    private Client client;

    private String name;
    private String displayName;

    public Scope(Client client, int index) {
        super(index);
        if (client == null) {
            throw new IllegalArgumentException();
        }
        this.client = client;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    @Override
    public int hashCode() {
        return 59 * super.hashCode() + client.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.getClient().equals(((Scope) obj).getClient());
    }

}
