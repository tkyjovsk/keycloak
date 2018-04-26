package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.NestedEntity;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.representations.idm.authorization.PolicyEnforcementMode;

/**
 *
 * @author tkyjovsk
 */
public class ResourceServer extends NestedEntity<Client> {

    private String name;
    private boolean allowRemoteResourceManagement;
    private PolicyEnforcementMode policyEnforcementMode;

    public ResourceServer(Client client) {
        super(client);
    }

    @JsonBackReference
    public Client getClient() {
        return getParentEntity();
    }

    @Override
    public synchronized String getId() {
        return getClient().getId();
    }

    public String getClientId() {
        return getClient().getClientId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowRemoteResourceManagement() {
        return allowRemoteResourceManagement;
    }

    public void setAllowRemoteResourceManagement(boolean allowRemoteResourceManagement) {
        this.allowRemoteResourceManagement = allowRemoteResourceManagement;
    }

    public PolicyEnforcementMode getPolicyEnforcementMode() {
        return policyEnforcementMode;
    }

    public void setPolicyEnforcementMode(PolicyEnforcementMode policyEnforcementMode) {
        this.policyEnforcementMode = policyEnforcementMode;
    }

}
