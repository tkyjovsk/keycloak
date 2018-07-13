package org.keycloak.performance.templates.idm;

import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.keycloak.performance.dataset.attr.AttributeMap;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.dataset.idm.ClientRoleMappings;
import org.keycloak.performance.dataset.idm.Credential;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.RealmRole;
import org.keycloak.performance.dataset.idm.RoleMappings;
import org.keycloak.performance.dataset.idm.RoleMappingsRepresentation;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.attr.StringListAttributeTemplate;
import org.keycloak.performance.iteration.RandomSublist;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class UserTemplate extends NestedIndexedEntityTemplate<Realm, User, UserRepresentation> {

    UserAttributeTemplate attributeTemplate;
    CredentialTemplate credentialTemplate;

    private final int usersPerRealm;
    private final int realmRolesPerUser;
    private final int clientRolesPerUser;

    public UserTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        this.attributeTemplate = new UserAttributeTemplate();
        this.credentialTemplate = new CredentialTemplate();
        this.usersPerRealm = getConfiguration().getInt("usersPerRealm", 0);
        this.realmRolesPerUser = getConfiguration().getInt("realmRolesPerUser", 0);
        this.clientRolesPerUser = getConfiguration().getInt("clientRolesPerUser", 0);
    }

    @Override
    public User newEntity(Realm parentEntity, int index) {
        return new User(parentEntity, index, new UserRepresentation());
    }

    @Override
    public void processMappings(User user) {

        user.setCredentials(new NestedIndexedEntityTemplateWrapperList<>(user, credentialTemplate));

        // note: attributes are embedded in user rep.
        user.getRepresentation().setAttributes(new AttributeMap(new NestedIndexedEntityTemplateWrapperList<>(user, attributeTemplate)));

        // REALM ROLE MAPPINGS
        List<RealmRole> realmRoles = new RandomSublist(
                user.getRealm().getRealmRoles(), // original list
                user.hashCode(), // random seed
                getRealmRolesPerUser(), // sublist size
                false // unique randoms?
        );
        RoleMappingsRepresentation rmr = new RoleMappingsRepresentation();
        realmRoles.forEach(rr -> rmr.add(rr.getRepresentation()));
        user.setRealmRoleMappings(new RoleMappings<>(user, rmr));

        // CLIENT ROLE MAPPINGS
        List<ClientRole> clientRoles = new RandomSublist(
                user.getRealm().getClientRoles(), // original list
                user.hashCode(), // random seed
                getClientRolesPerUser(), // sublist size
                false // unique randoms?
        );

        List<ClientRoleMappings<User>> clientRoleMappingsList = new LinkedList<>();
        List<Client> clients = clientRoles.stream().map(ClientRole::getClient).distinct().collect(toList());
        clients.forEach(client -> {
            List<ClientRole> clientClientRoles = clientRoles.stream().filter(clientRole
                    -> client.equals(clientRole.getClient()))
                    .collect(toList());

            RoleMappingsRepresentation cmr = new RoleMappingsRepresentation();
            clientClientRoles.forEach(cr -> cmr.add(cr.getRepresentation()));

            ClientRoleMappings<User> crm = new ClientRoleMappings(user, client, cmr);
            clientRoleMappingsList.add(crm);
        });
        user.setClientRoleMappingsList(clientRoleMappingsList);
    }

    @Override
    public int getEntityCountPerParent() {
        return getUsersPerRealm();
    }

    public int getUsersPerRealm() {
        return usersPerRealm;
    }

    public int getRealmRolesPerUser() {
        return realmRolesPerUser;
    }

    public int getClientRolesPerUser() {
        return clientRolesPerUser;
    }

    @Override
    public void validateConfiguration() {

        // sizing
        logger().info(String.format("usersPerRealm: %s", usersPerRealm));
        validateInt().minValue(usersPerRealm, 0);

        // mappings
        attributeTemplate.validateConfiguration();

        logger().info(String.format("realmRolesPerUser: %s", realmRolesPerUser));
        RealmTemplate rt = (RealmTemplate) getParentEntityTemplate();
        RealmRoleTemplate rrt = rt.getRealmRoleTemplate();
        validateInt().isInRange(realmRolesPerUser, 0, rrt.getRealmRolesPerRealm());

        logger().info(String.format("clientRolesPerUser: %s", clientRolesPerUser));
        ClientTemplate ct = rt.getClientTemplate();
        ClientRoleTemplate crt = ct.getClientRoleTemplate();
        validateInt().isInRange(clientRolesPerUser, 0, ct.getClientsPerRealm() * crt.getClientRolesPerClient());

    }

    public class CredentialTemplate extends NestedIndexedEntityTemplate<User, Credential, CredentialRepresentation> {

        public CredentialTemplate() {
            super(UserTemplate.this);
        }

        @Override
        public int getEntityCountPerParent() {
            return 1;
        }

        @Override
        public void validateConfiguration() {
        }

        @Override
        public Credential newEntity(User parentEntity, int index) {
            return new Credential(parentEntity, index, new CredentialRepresentation());
        }

        @Override
        public void processMappings(Credential entity) {
        }

    }

    public class UserAttributeTemplate extends StringListAttributeTemplate<User> {

        private final int attributesPerUser;

        public UserAttributeTemplate() {
            super(UserTemplate.this);
            this.attributesPerUser = getConfiguration().getInt("attributesPerUser", 0);
        }

        @Override
        public int getEntityCountPerParent() {
            return attributesPerUser;
        }

        @Override
        public void validateConfiguration() {
            logger().info(String.format("attributesPerUser: %s", attributesPerUser));
            validateInt().minValue(attributesPerUser, 0);
        }

    }

}
