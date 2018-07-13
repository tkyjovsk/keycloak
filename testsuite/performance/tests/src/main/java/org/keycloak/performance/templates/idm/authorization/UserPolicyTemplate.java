package org.keycloak.performance.templates.idm.authorization;

import static java.util.stream.Collectors.toSet;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.UserPolicy;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class UserPolicyTemplate extends PolicyTemplate<UserPolicy, UserPolicyRepresentation> {

    private final int userPoliciesPerResourceServer;
    private final int usersPerUserPolicy;

    public UserPolicyTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.userPoliciesPerResourceServer = getConfiguration().getInt("userPoliciesPerResourceServer", 0);
        this.usersPerUserPolicy = getConfiguration().getInt("usersPerUserPolicy", 0);
    }

    public int getUserPoliciesPerResourceServer() {
        return userPoliciesPerResourceServer;
    }

    public int getUsersPerUserPolicy() {
        return usersPerUserPolicy;
    }

    @Override
    public int getEntityCountPerParent() {
        return userPoliciesPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("userPoliciesPerResourceServer: %s", userPoliciesPerResourceServer));
        validateInt().minValue(userPoliciesPerResourceServer, 0);
        // TODO add range check for usersPerRealm
        validateInt().minValue(usersPerUserPolicy, 0);
    }

    @Override
    public UserPolicy newEntity(ResourceServer parentEntity, int index) {
        return new UserPolicy(parentEntity, index, new UserPolicyRepresentation());
    }

    @Override
    public void processMappings(UserPolicy policy) {
        policy.setUsers(new RandomSublist<>(
                policy.getResourceServer().getClient().getRealm().getUsers(), // original list
                policy.hashCode(), // random seed
                usersPerUserPolicy, // sublist size
                false // unique randoms?
        ));
        policy.getRepresentation().setUsers(policy.getUsers()
                .stream().map(u -> u.getId())
                .filter(id -> id != null) // need non-null policy IDs
                .collect(toSet()));
    }

}
