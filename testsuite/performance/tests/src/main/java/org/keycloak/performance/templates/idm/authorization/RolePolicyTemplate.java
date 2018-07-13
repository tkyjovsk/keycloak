package org.keycloak.performance.templates.idm.authorization;

import org.apache.commons.lang.Validate;
import org.keycloak.performance.dataset.Entity;
import org.keycloak.performance.dataset.idm.Role;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.RolePolicyRoleDefinition;
import org.keycloak.performance.dataset.idm.authorization.RolePolicy;
import org.keycloak.performance.dataset.idm.authorization.RolePolicyRoleDefinitionSet;
import org.keycloak.performance.iteration.ListOfLists;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.idm.ClientRoleTemplate;
import org.keycloak.performance.templates.idm.ClientTemplate;
import org.keycloak.performance.templates.idm.RealmTemplate;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RolePolicyTemplate extends PolicyTemplate<RolePolicy, RolePolicyRepresentation> {

    private final int rolePoliciesPerResourceServer;

    private final RolePolicyRoleDefinitionTemplate roleDefinitionTemplate;

    public RolePolicyTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.roleDefinitionTemplate = new RolePolicyRoleDefinitionTemplate();
        this.rolePoliciesPerResourceServer = getConfiguration().getInt("rolePoliciesPerResourceServer", 0);
    }

    public int getRolePoliciesPerResourceServer() {
        return rolePoliciesPerResourceServer;
    }

    @Override
    public int getEntityCountPerParent() {
        return rolePoliciesPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("rolePoliciesPerResourceServer: %s", rolePoliciesPerResourceServer));
        validateInt().minValue(rolePoliciesPerResourceServer, 0);

        roleDefinitionTemplate.validateConfiguration();
    }

    @Override
    public RolePolicy newEntity(ResourceServer parentEntity, int index) {
        return new RolePolicy(parentEntity, index, new RolePolicyRepresentation());
    }

    @Override
    public void processMappings(RolePolicy rolePolicy) {
        rolePolicy.setRoles(new ListOfLists<>(
                new RandomSublist(
                        rolePolicy.getResourceServer().getClient().getRealm().getRealmRoles(), // original list
                        rolePolicy.hashCode(), // random seed
                        roleDefinitionTemplate.realmRolesPerRolePolicy, // sublist size
                        false // unique randoms?
                ),
                new RandomSublist(
                        rolePolicy.getResourceServer().getClient().getRealm().getClientRoles(), // original list
                        rolePolicy.hashCode(), // random seed
                        roleDefinitionTemplate.clientRolesPerRolePolicy, // sublist size
                        false // unique randoms?
                )
        ));

        rolePolicy.getRepresentation().setRoles(new RolePolicyRoleDefinitionSet(
                new NestedIndexedEntityTemplateWrapperList<>(rolePolicy, roleDefinitionTemplate)
        ));
    }

    public class RolePolicyRoleDefinitionTemplate
            extends NestedIndexedEntityTemplate<RolePolicy, RolePolicyRoleDefinition, RolePolicyRepresentation.RoleDefinition> {

        private final int realmRolesPerRolePolicy;
        private final int clientRolesPerRolePolicy;

        public RolePolicyRoleDefinitionTemplate() {
            super(RolePolicyTemplate.this);
            this.realmRolesPerRolePolicy = getConfiguration().getInt("realmRolesPerRolePolicy", 0);
            this.clientRolesPerRolePolicy = getConfiguration().getInt("clientRolesPerRolePolicy", 0);
        }

        public int getRealmRolesPerRolePolicy() {
            return realmRolesPerRolePolicy;
        }

        public int getClientRolesPerRolePolicy() {
            return clientRolesPerRolePolicy;
        }

        @Override
        public int getEntityCountPerParent() {
            return getRealmRolesPerRolePolicy() + getClientRolesPerRolePolicy();
        }

        @Override
        public void validateConfiguration() {//FIXME
            RolePolicyTemplate rpt = (RolePolicyTemplate) getParentEntityTemplate();
            ResourceServerTemplate rst = (ResourceServerTemplate) rpt.getParentEntityTemplate();
            ClientTemplate ct = (ClientTemplate) rst.getParentEntityTemplate();
            ClientRoleTemplate crt = ct.getClientRoleTemplate();
            RealmTemplate rt = (RealmTemplate) ct.getParentEntityTemplate();
            int maxRealmRoles = rt.getRealmRoleTemplate().getRealmRolesPerRealm();
            int maxClientRoles = ct.getClientsPerRealm() * crt.getClientRolesPerClient();

            validateInt().isInRange(getRealmRolesPerRolePolicy(), 0, maxRealmRoles);
            validateInt().isInRange(getClientRolesPerRolePolicy(), 0, maxClientRoles);

        }

        @Override
        public RolePolicyRoleDefinition newEntity(RolePolicy rolePolicy, int index) {
            Validate.isTrue(rolePolicy.getRoles().size() == getEntityCountPerParent());
            String roleUUID = ((Role<Entity>) rolePolicy.getRoles().get(index)).getRepresentation().getId();
            return new RolePolicyRoleDefinition(rolePolicy, index, new RolePolicyRepresentation.RoleDefinition(roleUUID, false));
        }

        @Override
        public void processMappings(RolePolicyRoleDefinition entity) {
        }

    }

}
