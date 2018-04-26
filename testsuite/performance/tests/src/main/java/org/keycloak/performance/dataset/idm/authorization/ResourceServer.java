package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import org.keycloak.performance.dataset.NestedEntity;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.representations.idm.authorization.PolicyEnforcementMode;

/**
 *
 * @author tkyjovsk
 */
public class ResourceServer extends NestedEntity<Client> {

    private boolean allowRemoteResourceManagement;
    private PolicyEnforcementMode policyEnforcementMode;

    @JsonIgnore
    private List<Scope> scopes;

    @JsonIgnore
    private List<Resource> resources;

    @JsonIgnore
    private List<RolePolicy> rolePolicies;

    public ResourceServer(Client client) {
        super(client);
    }

    @Override
    public String toString() {
        return getClient().toString();
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
        return getClient().getName();
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

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

    public List<RolePolicy> getRolePolicies() {
        return rolePolicies;
    }

    public void setRolePolicies(List<RolePolicy> rolePolicies) {
        this.rolePolicies = rolePolicies;
    }

}
