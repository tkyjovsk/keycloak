package org.keycloak.performance.templates.idm;

import java.util.List;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.iteration.FlattenedListOfLists;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.authorization.ResourceServerList;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.DatasetTemplate;

/**
 *
 * @author tkyjovsk
 */
public class RealmTemplate extends NestedIndexedEntityTemplate<Dataset, Realm> {

    private final int realms;

    private final ClientTemplate clientTemplate;
    private final RealmRoleTemplate realmRoleTemplate;
    private final UserTemplate userTemplate;

    public RealmTemplate(DatasetTemplate datasetTemplate) {
        super(datasetTemplate);
        registerAttributeTemplate("realm");
        registerAttributeTemplate("displayName");
        registerAttributeTemplate("enabled");
        registerAttributeTemplate("registrationAllowed");
        registerAttributeTemplate("accessTokenLifeSpan");
        registerAttributeTemplate("passwordPolicy");
        this.realms = configuration.getInt("realms", 0);
        clientTemplate = new ClientTemplate(this);
        realmRoleTemplate = new RealmRoleTemplate(this);
        userTemplate = new UserTemplate(this);
    }

    @Override
    public synchronized Realm produce(Dataset dataset, int index) {
        Realm realm = new Realm(dataset, index);
        realm.setRealm(processAttribute("realm", realm));
        realm.setDisplayName(processAttribute("displayName", realm));
        realm.setEnabled(Boolean.parseBoolean(processAttribute("enabled", realm)));
        realm.setRegistrationAllowed(Boolean.parseBoolean(processAttribute("registrationAllowed", realm)));
        realm.setAccessTokenLifespan(Integer.parseInt(processAttribute("accessTokenLifeSpan", realm)));
        realm.setPasswordPolicy(processAttribute("passwordPolicy", realm));
        realm.setClients(new NestedIndexedEntityTemplateWrapperList<>(realm, clientTemplate));
        realm.setResourceServers(new ResourceServerList(realm.getClients()));
        realm.setClientRoles(new FlattenedListOfLists<Client, ClientRole>() { // only for mapping, no CRUD
            @Override
            public List<Client> getXList() {
                return realm.getClients();
            }

            @Override
            public List<ClientRole> getYList(Client client) {
                return client.getClientRoles();
            }

            @Override
            public int getYListSize() {
                return clientTemplate.getClientRoleTemplate().getClientRolesPerClient();
            }
        });
        realm.setRealmRoles(new NestedIndexedEntityTemplateWrapperList<>(realm, realmRoleTemplate));
        realm.setUsers(new NestedIndexedEntityTemplateWrapperList<>(realm, userTemplate));
        return realm;
    }

    public UserTemplate getUserTemplate() {
        return userTemplate;
    }

    public ClientTemplate getClientTemplate() {
        return clientTemplate;
    }

    public RealmRoleTemplate getRealmRoleTemplate() {
        return realmRoleTemplate;
    }

    @Override
    public int getEntityCountPerParent() {
        return getRealms();
    }

    public int getRealms() {
        return realms;
    }

    @Override
    public void validateSizeConfiguration() {
        // sizing
        logger().info(String.format("realms: %s", realms));
        validateInt().minValue(realms, 0);

        clientTemplate.validateSizeConfiguration();
        realmRoleTemplate.validateSizeConfiguration();
        userTemplate.validateSizeConfiguration();
    }

}
