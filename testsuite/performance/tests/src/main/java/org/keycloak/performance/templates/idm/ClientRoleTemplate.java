package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.representations.idm.RoleRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ClientRoleTemplate extends NestedIndexedEntityTemplate<Client, ClientRole, RoleRepresentation> {

    private final int clientRolesPerClient;

    public ClientRoleTemplate(ClientTemplate clientTemplate) {
        super(clientTemplate);
        this.clientRolesPerClient = getConfiguration().getInt("clientRolesPerClient", 0);
    }

    @Override
    public int getEntityCountPerParent() {
        return getClientRolesPerClient();
    }

    public int getClientRolesPerClient() {
        return clientRolesPerClient;
    }

    @Override
    public void validateConfiguration() {
        // sizing
        logger().info(String.format("clientRolesPerClient: %s", clientRolesPerClient));
        validateInt().minValue(clientRolesPerClient, 0);
    }

    @Override
    public ClientRole newEntity(Client parentEntity, int index) {
        return new ClientRole(parentEntity, index, new RoleRepresentation());
    }

    @Override
    public void processMappings(ClientRole entity) {
    }

}
