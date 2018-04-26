package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;

/**
 *
 * @author tkyjovsk
 */
public class ClientRoleTemplate extends NestedIndexedEntityTemplate<Client, ClientRole> {

    private final int clientRolesPerClient;

    public ClientRoleTemplate(ClientTemplate clientTemplate) {
        super(clientTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("description");
        this.clientRolesPerClient = configuration.getInt("clientRolesPerClient", 0);
    }

    @Override
    public synchronized ClientRole produce(Client client, int index) {
        ClientRole clientRole = new ClientRole(client, index);
        clientRole.setName(processAttribute("name", clientRole));
        clientRole.setDescription(processAttribute("description", clientRole));
        return clientRole;
    }

    @Override
    public int getEntityCountPerParent() {
        return getClientRolesPerClient();
    }

    public int getClientRolesPerClient() {
        return clientRolesPerClient;
    }

    @Override
    public void validateSizeConfiguration() {
        // sizing
        logger().info(String.format("clientRolesPerClient: %s", clientRolesPerClient));
        validateInt().minValue(clientRolesPerClient, 0);
    }

}
