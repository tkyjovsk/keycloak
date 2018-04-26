package org.keycloak.performance.dataset.idm.authorization;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.IndexedEntity;
import org.keycloak.representations.idm.authorization.Logic;

/**
 *
 * @author tkyjovsk
 */
public abstract class Policy extends IndexedEntity {

    private Client client;

    private String name;
    private String description;
    private Logic logic;

    public Policy(Client client, int index) {
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

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public int hashCode() {
        return 29 * super.hashCode() + getClient().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.getClient().equals(((Policy) obj).getClient());
    }

}
