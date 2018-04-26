package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Credential;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.AttributeMap;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.StringListAttributeTemplate;
import org.keycloak.performance.iteration.RandomSublist;

/**
 *
 * @author tkyjovsk
 */
public class UserTemplate extends NestedIndexedEntityTemplate<Realm, User> {

    UserAttributeTemplate attributeTemplate;
    CredentialTemplate credentialTemplate;

    private final int usersPerRealm;
    private final int realmRolesPerUser;
    private final int clientRolesPerUser;

    public UserTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        registerAttributeTemplate("username");
        registerAttributeTemplate("enabled");
        registerAttributeTemplate("password");
        registerAttributeTemplate("email");
        registerAttributeTemplate("emailVerified");
        registerAttributeTemplate("firstName");
        registerAttributeTemplate("lastName");
        this.attributeTemplate = new UserAttributeTemplate();
        this.credentialTemplate = new CredentialTemplate();
        this.usersPerRealm = configuration.getInt("usersPerRealm", 0);
        this.realmRolesPerUser = configuration.getInt("realmRolesPerUser", 0);
        this.clientRolesPerUser = configuration.getInt("clientRolesPerUser", 0);
    }

    @Override
    public synchronized User produce(Realm realm, int index) {
        User user = new User(realm, index);
        user.setUsername(processAttribute("username", user));
        user.setEnabled(Boolean.parseBoolean(processAttribute("enabled", user)));
        user.setPassword(processAttribute("password", user));
        user.setEmail(processAttribute("email", user));
        user.setEmailVerified(Boolean.parseBoolean(processAttribute("emailVerified", user)));
        user.setFirstName(processAttribute("firstName", user));
        user.setLastName(processAttribute("lastName", user));

        user.setAttributes(new AttributeMap<>(
                new NestedIndexedEntityTemplateWrapperList<>(user, attributeTemplate)));
        
        user.setCredentials(new NestedIndexedEntityTemplateWrapperList<>(user, credentialTemplate));

        user.setRealmRoles(
                new RandomSublist(
                        user.getRealm().getRealmRoles(), // original list
                        user.hashCode(), // random seed
                        getRealmRolesPerUser(), // sublist size
                        false // unique randoms?
                )
        );

        user.setClientRoles(
                new RandomSublist(
                        user.getRealm().getClientRoles(), // original list
                        user.hashCode(), // random seed
                        getClientRolesPerUser(), // sublist size
                        false // unique randoms?
                )
        );

        return user;
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
    public void validateSizeConfiguration() {

        // sizing
        logger().info(String.format("usersPerRealm: %s", usersPerRealm));
        validateInt().minValue(usersPerRealm, 0);

        // mappings
        attributeTemplate.validateSizeConfiguration();

        logger().info(String.format("realmRolesPerUser: %s", realmRolesPerUser));
        RealmTemplate rt = (RealmTemplate) getParentEntityTemplate();
        RealmRoleTemplate rrt = rt.getRealmRoleTemplate();
        validateInt().isInRange(realmRolesPerUser, 0, rrt.getRealmRolesPerRealm());

        logger().info(String.format("clientRolesPerUser: %s", clientRolesPerUser));
        ClientTemplate ct = rt.getClientTemplate();
        ClientRoleTemplate crt = ct.getClientRoleTemplate();
        validateInt().isInRange(clientRolesPerUser, 0, ct.getClientsPerRealm() * crt.getClientRolesPerClient());

    }

    public class UserAttributeTemplate extends StringListAttributeTemplate<User> {

        private final int attributesPerUser;

        public UserAttributeTemplate() {
            super(UserTemplate.this);
            this.attributesPerUser = configuration.getInt("attributesPerUser", 0);
        }

        @Override
        public int getEntityCountPerParent() {
            return attributesPerUser;
        }

        @Override
        public void validateSizeConfiguration() {
            logger().info(String.format("attributesPerUser: %s", attributesPerUser));
            validateInt().minValue(attributesPerUser, 0);
        }

    }

    public class CredentialTemplate extends NestedIndexedEntityTemplate<User, Credential> {

        public CredentialTemplate() {
            super(UserTemplate.this);
        }

        @Override
        public Credential produce(User user, int index) {
            validateInt().isInRange(index, 1, 1);
            Credential credential = new Credential(user, index);
            credential.setType("password");
            credential.setValue(user.getPassword());
            credential.setTemporary(false);
            return credential;
        }

        @Override
        public int getEntityCountPerParent() {
            return 1;
        }

        @Override
        public void validateSizeConfiguration() {
        }

    }

}
