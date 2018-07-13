package org.keycloak.performance.templates.idm.authorization;

import static java.util.stream.Collectors.toSet;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.ClientPolicy;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ClientPolicyTemplate extends PolicyTemplate<ClientPolicy, ClientPolicyRepresentation> {

    private final int clientPoliciesPerResourceServer;
    private final int clientsPerClientPolicy;

    public ClientPolicyTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.clientPoliciesPerResourceServer = getConfiguration().getInt("clientPoliciesPerResourceServer", 0);
        this.clientsPerClientPolicy = getConfiguration().getInt("clientsPerClientPolicy", 0);
    }

    public int getClientPoliciesPerResourceServer() {
        return clientPoliciesPerResourceServer;
    }

    public int getClientsPerClientPolicy() {
        return clientsPerClientPolicy;
    }

    @Override
    public int getEntityCountPerParent() {
        return clientPoliciesPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("clientPoliciesPerResourceServer: %s", clientPoliciesPerResourceServer));
        validateInt().minValue(clientPoliciesPerResourceServer, 0);
        // TODO add range check for usersPerRealm
        validateInt().minValue(clientsPerClientPolicy, 0);
    }

    @Override
    public ClientPolicy newEntity(ResourceServer parentEntity, int index) {
        return new ClientPolicy(parentEntity, index, new ClientPolicyRepresentation());
    }

    @Override
    public void processMappings(ClientPolicy policy) {
        policy.setClients(new RandomSublist<>(
                policy.getResourceServer().getClient().getRealm().getClients(), // original list
                policy.hashCode(), // random seed
                clientsPerClientPolicy, // sublist size
                false // unique randoms?
        ));
        policy.getRepresentation().setClients(policy.getClients()
                .stream().map(u -> u.getId())
                .filter(id -> id != null) // need non-null policy IDs
                .collect(toSet()));
    }

}
