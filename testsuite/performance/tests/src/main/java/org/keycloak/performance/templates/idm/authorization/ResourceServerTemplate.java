package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.templates.idm.*;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.templates.NestedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.representations.idm.authorization.PolicyEnforcementMode;

/**
 *
 * @author tkyjovsk
 */
public class ResourceServerTemplate extends NestedEntityTemplate<Client, ResourceServer> {

    private final ScopeTemplate scopeTemplate;
    private final ResourceTemplate resourceTemplate;
    private final RolePolicyTemplate rolePolicyTemplate;

    public ResourceServerTemplate(ClientTemplate clientTemplate) {
        super(clientTemplate);
        registerAttributeTemplate("allowRemoteResourceManagement");
        registerAttributeTemplate("policyEnforcementMode");
        this.scopeTemplate = new ScopeTemplate(this);
        this.resourceTemplate = new ResourceTemplate(this);
        this.rolePolicyTemplate = new RolePolicyTemplate(this);
    }

    public ResourceTemplate getResourceTemplate() {
        return resourceTemplate;
    }

    public ScopeTemplate getScopeTemplate() {
        return scopeTemplate;
    }

    public RolePolicyTemplate getRolePolicyTemplate() {
        return rolePolicyTemplate;
    }

    @Override
    public ResourceServer produce(Client client) {
        ResourceServer resourceServer = new ResourceServer(client);

        resourceServer.setAllowRemoteResourceManagement(Boolean.parseBoolean(
                processAttribute("allowRemoteResourceManagement", resourceServer)));
        resourceServer.setPolicyEnforcementMode(PolicyEnforcementMode.valueOf(
                processAttribute("policyEnforcementMode", resourceServer).toUpperCase()));

        resourceServer.setResources(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, resourceTemplate));
        resourceServer.setScopes(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, scopeTemplate));
        resourceServer.setRolePolicies(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, rolePolicyTemplate));

        return resourceServer;
    }

    @Override
    public void validateSizeConfiguration() {
        scopeTemplate.validateSizeConfiguration();
        resourceTemplate.validateSizeConfiguration();
        rolePolicyTemplate.validateSizeConfiguration();
    }

}
