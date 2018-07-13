package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.idm.authorization.ResourceServerTemplate;
import org.keycloak.representations.idm.ClientRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ClientTemplate extends NestedIndexedEntityTemplate<Realm, Client, ClientRepresentation> {

    private final int clientsPerRealm;

    private final ClientRoleTemplate clientRoleTemplate;
    private final ResourceServerTemplate resourceServerTemplate;

    public ClientTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        this.clientsPerRealm = getConfiguration().getInt("clientsPerRealm", 0);
        this.clientRoleTemplate = new ClientRoleTemplate(this);
        this.resourceServerTemplate = new ResourceServerTemplate(this);
    }

    public ClientRoleTemplate getClientRoleTemplate() {
        return clientRoleTemplate;
    }

    public ResourceServerTemplate getResourceServerTemplate() {
        return resourceServerTemplate;
    }

    @Override
    public int getEntityCountPerParent() {
        return getClientsPerRealm();
    }

    public int getClientsPerRealm() {
        return clientsPerRealm;
    }

    @Override
    public void validateConfiguration() {
        // sizing
        logger().info(String.format("clientsPerRealm: %s", clientsPerRealm));
        validateInt().minValue(clientsPerRealm, 0);

        clientRoleTemplate.validateConfiguration();
    }

    @Override
    public Client newEntity(Realm parentEntity, int index) {
        return new Client(parentEntity, index, new ClientRepresentation());
    }

    @Override
    public void processMappings(Client client) {
        client.setClientRoles(new NestedIndexedEntityTemplateWrapperList<>(client, clientRoleTemplate));

        if (client.getRepresentation().getAuthorizationServicesEnabled()) {
            client.setResourceServer(resourceServerTemplate.produce(client));
        }
    }

}
