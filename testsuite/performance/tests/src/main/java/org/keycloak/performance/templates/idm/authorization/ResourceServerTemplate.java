package org.keycloak.performance.templates.idm.authorization;

import org.apache.commons.lang.Validate;
import org.keycloak.performance.templates.idm.*;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.iteration.ListOfLists;
import org.keycloak.performance.templates.NestedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ResourceServerTemplate extends NestedEntityTemplate<Client, ResourceServer, ResourceServerRepresentation> {

    private final ScopeTemplate scopeTemplate;
    private final ResourceTemplate resourceTemplate;
    private final RolePolicyTemplate rolePolicyTemplate;
    private final JsPolicyTemplate jsPolicyTemplate;
    private final UserPolicyTemplate userPolicyTemplate;
    private final ClientPolicyTemplate clientPolicyTemplate;
    private final ResourcePermissionTemplate resourcePermissionTemplate;
    private final ScopePermissionTemplate scopePermissionTemplate;

    public ResourceServerTemplate(ClientTemplate clientTemplate) {
        super(clientTemplate);
        this.scopeTemplate = new ScopeTemplate(this);
        this.resourceTemplate = new ResourceTemplate(this);
        this.rolePolicyTemplate = new RolePolicyTemplate(this);
        this.jsPolicyTemplate = new JsPolicyTemplate(this);
        this.userPolicyTemplate = new UserPolicyTemplate(this);
        this.clientPolicyTemplate = new ClientPolicyTemplate(this);
        this.resourcePermissionTemplate = new ResourcePermissionTemplate(this);
        this.scopePermissionTemplate = new ScopePermissionTemplate(this);
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

    public JsPolicyTemplate getJsPolicyTemplate() {
        return jsPolicyTemplate;
    }

    public UserPolicyTemplate getUserPolicyTemplate() {
        return userPolicyTemplate;
    }

    public ClientPolicyTemplate getClientPolicyTemplate() {
        return clientPolicyTemplate;
    }

    public ResourcePermissionTemplate getResourcePermissionTemplate() {
        return resourcePermissionTemplate;
    }

    public ScopePermissionTemplate getScopePermissionTemplate() {
        return scopePermissionTemplate;
    }

    @Override
    public void validateConfiguration() {
        scopeTemplate.validateConfiguration();
        resourceTemplate.validateConfiguration();
        rolePolicyTemplate.validateConfiguration();
        jsPolicyTemplate.validateConfiguration();
        userPolicyTemplate.validateConfiguration();
        clientPolicyTemplate.validateConfiguration();
        resourcePermissionTemplate.validateConfiguration();
        scopePermissionTemplate.validateConfiguration();
    }

    @Override
    public ResourceServer newEntity(Client client) {
        Validate.notNull(client);
        Validate.notNull(client.getRepresentation());
        Validate.notNull(client.getRepresentation().getBaseUrl());
        return new ResourceServer(client, new ResourceServerRepresentation());
    }

    @Override
    public void processMappings(ResourceServer resourceServer) {
        resourceServer.setScopes(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, scopeTemplate));
        resourceServer.setResources(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, resourceTemplate));

        resourceServer.setRolePolicies(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, rolePolicyTemplate));
        resourceServer.setJsPolicies(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, jsPolicyTemplate));
        resourceServer.setUserPolicies(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, userPolicyTemplate));
        resourceServer.setClientPolicies(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, clientPolicyTemplate));
        resourceServer.setAllPolicies(new ListOfLists( // proxy list
                resourceServer.getRolePolicies(),
                resourceServer.getJsPolicies(),
                resourceServer.getUserPolicies(),
                resourceServer.getClientPolicies()
        ));

        resourceServer.setResourcePermissions(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, resourcePermissionTemplate));
        resourceServer.setScopePermissions(new NestedIndexedEntityTemplateWrapperList<>(resourceServer, scopePermissionTemplate));

    }

}
