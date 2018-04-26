package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.templates.NestedEntityTemplate;
import static org.keycloak.performance.util.ValidationUtil.VALIDATE_INT;

/**
 *
 * @author tkyjovsk
 */
public class ClientRoleTemplate extends NestedEntityTemplate<Client, ClientRole> {

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
        logger.info(String.format("clientRolesPerClient: %s", clientRolesPerClient));
        VALIDATE_INT.minValue(clientRolesPerClient, 0);
    }

}
