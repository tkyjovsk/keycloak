package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.IndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public abstract class Permission extends IndexedEntity {

    private Client client;

    private String name;
    private String description;

    public Permission(Client client, int index) {
        super(index);
        if (client == null) {
            throw new IllegalArgumentException();
        }
        this.client = client;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return 89 * super.hashCode() + client.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.getClient().equals(((Permission) obj).getClient());
    }

}
