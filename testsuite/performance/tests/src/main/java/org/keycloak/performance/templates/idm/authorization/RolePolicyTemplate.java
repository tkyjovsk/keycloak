package org.keycloak.performance.templates.idm.authorization;

import java.util.List;
import org.keycloak.performance.dataset.idm.Role;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.RolePolicyRoleDefinition;
import org.keycloak.performance.dataset.idm.authorization.RolePolicy;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.idm.ClientRoleTemplate;
import org.keycloak.performance.templates.idm.ClientTemplate;
import org.keycloak.performance.templates.idm.RealmTemplate;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.Logic;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicyTemplate extends PolicyTemplate<RolePolicy> {

    private final int rolePoliciesPerResourceServer;
    private final int realmRolesPerRolePolicy;
    private final int clientRolesPerRolePolicy;

    private final RolePolicyRoleDefinitionTemplate roleDefinitionTemplate;

    public RolePolicyTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.roleDefinitionTemplate = new RolePolicyRoleDefinitionTemplate();
        this.rolePoliciesPerResourceServer = configuration.getInt("scopesPerResourceServer", 0);
        this.realmRolesPerRolePolicy = configuration.getInt("realmRolesPerRolePolicy", 0);
        this.clientRolesPerRolePolicy = configuration.getInt("clientRolesPerRolePolicy", 0);
    }

    public int getRolePoliciesPerResourceServer() {
        return rolePoliciesPerResourceServer;
    }

    public int getRealmRolesPerRolePolicy() {
        return realmRolesPerRolePolicy;
    }

    public int getClientRolesPerRolePolicy() {
        return clientRolesPerRolePolicy;
    }

    @Override
    public RolePolicy produce(ResourceServer resourceServer, int index) {
        RolePolicy rolePolicy = new RolePolicy(resourceServer, index);

        rolePolicy.setName(
                processAttribute("name", rolePolicy));
        rolePolicy.setDescription(
                processAttribute("description", rolePolicy));
        rolePolicy.setLogic(Logic.valueOf(
                processAttribute("logic", rolePolicy).toUpperCase()));
        rolePolicy.setDecisionStrategy(DecisionStrategy.valueOf(
                processAttribute("decisionStrategy", rolePolicy).toUpperCase()));

        List<Role> mappedRoles = new RandomSublist(
                rolePolicy.getResourceServer().getClient().getRealm().getRealmRoles(), // original list
                rolePolicy.hashCode(), // random seed
                getRealmRolesPerRolePolicy(), // sublist size
                false // unique randoms?
        );
        mappedRoles.addAll( // client roles
                new RandomSublist(
                        rolePolicy.getResourceServer().getClient().getRealm().getClientRoles(), // original list
                        rolePolicy.hashCode(), // random seed
                        getClientRolesPerRolePolicy(), // sublist size
                        false // unique randoms?
                )
        );
        rolePolicy.setMappedRoles(mappedRoles);
        rolePolicy.setRoles(new NestedIndexedEntityTemplateWrapperList<>(rolePolicy, roleDefinitionTemplate));

        return rolePolicy;
    }

    @Override
    public int getEntityCountPerParent() {
        return rolePoliciesPerResourceServer;
    }

    @Override
    public void validateSizeConfiguration() {
        logger().info(String.format("scopesPerResourceServer: %s", rolePoliciesPerResourceServer));
        validateInt().minValue(rolePoliciesPerResourceServer, 0);

        ResourceServerTemplate rst = (ResourceServerTemplate) getParentEntityTemplate();
        ClientTemplate ct = (ClientTemplate) rst.getParentEntityTemplate();
        ClientRoleTemplate crt = ct.getClientRoleTemplate();
        RealmTemplate rt = (RealmTemplate) ct.getParentEntityTemplate();
        int maxRealmRoles = rt.getRealmRoleTemplate().getRealmRolesPerRealm();
        int maxClientRoles = ct.getClientsPerRealm() * crt.getClientRolesPerClient();

        validateInt().isInRange(getRealmRolesPerRolePolicy(), 0, maxRealmRoles);
        validateInt().isInRange(getClientRolesPerRolePolicy(), 0, maxClientRoles);
    }

    public class RolePolicyRoleDefinitionTemplate extends NestedIndexedEntityTemplate<RolePolicy, RolePolicyRoleDefinition> {

        public RolePolicyRoleDefinitionTemplate() {
            super(RolePolicyTemplate.this);
            registerAttributeTemplate("required");
        }

        @Override
        public RolePolicyRoleDefinition produce(RolePolicy rolePolicy, int index) {
            RolePolicyRoleDefinition roleDefinition = new RolePolicyRoleDefinition(rolePolicy, index);
            roleDefinition.setRole(rolePolicy.getMappedRoles().get(index));
            roleDefinition.setRequired(Boolean.parseBoolean(
                    processAttribute("required", roleDefinition)));
            return roleDefinition;
        }

        @Override
        public int getEntityCountPerParent() {
            return getRealmRolesPerRolePolicy() + getClientRolesPerRolePolicy();
        }

        @Override
        public void validateSizeConfiguration() {
        }

    }

}
