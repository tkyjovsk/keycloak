package org.keycloak.performance.dataset.idm;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleByIdResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.performance.dataset.ResourceFacade;

/**
 *
 * @author tkyjovsk
 */
public class ClientRole extends Role<Client> implements ResourceFacade<RoleRepresentation> {

    public ClientRole(Client client, int index, RoleRepresentation representation) {
        super(client, index, representation);
    }

    public Client getClient() {
        return getParentEntity();
    }

    @Override
    public RolesResource rolesResource(Keycloak adminClient) {
        return getClient().clientResource(adminClient).roles();
    }

    @Override
    public RoleByIdResource roleByIdResource(Keycloak adminClient) {
        return getClient().getRealm().realmResource(adminClient).rolesById();
    }

}
