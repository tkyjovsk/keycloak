package org.keycloak.performance.templates.idm;

import java.util.List;
import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.iteration.Flattened2DList;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.authorization.ResourceServerList;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.DatasetTemplate;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class RealmTemplate extends NestedIndexedEntityTemplate<Dataset, Realm, RealmRepresentation> {

    private final int realms;

    private final ClientTemplate clientTemplate;
    private final RealmRoleTemplate realmRoleTemplate;
    private final UserTemplate userTemplate;
    private final GroupTemplate groupTemplate;

    public RealmTemplate(DatasetTemplate datasetTemplate) {
        super(datasetTemplate);
        this.realms = getConfiguration().getInt("realms", 0);
        this.clientTemplate = new ClientTemplate(this);
        this.realmRoleTemplate = new RealmRoleTemplate(this);
        this.userTemplate = new UserTemplate(this);
        this.groupTemplate = new GroupTemplate(this);
    }

    public ClientTemplate getClientTemplate() {
        return clientTemplate;
    }

    public RealmRoleTemplate getRealmRoleTemplate() {
        return realmRoleTemplate;
    }

    public UserTemplate getUserTemplate() {
        return userTemplate;
    }

    public GroupTemplate getGroupTemplate() {
        return groupTemplate;
    }

    @Override
    public int getEntityCountPerParent() {
        return getRealms();
    }

    public int getRealms() {
        return realms;
    }

    @Override
    public void validateConfiguration() {
        // sizing
        logger().info(String.format("realms: %s", realms));
        validateInt().minValue(realms, 0);

        clientTemplate.validateConfiguration();
        realmRoleTemplate.validateConfiguration();
        userTemplate.validateConfiguration();
        groupTemplate.validateConfiguration();
    }

    @Override
    public Realm newEntity(Dataset parentEntity, int index) {
        return new Realm(parentEntity, index, new RealmRepresentation());
    }

    @Override
    public void processMappings(Realm realm) {
        realm.setClients(new NestedIndexedEntityTemplateWrapperList<>(realm, clientTemplate));
        realm.setResourceServers(new ResourceServerList(realm.getClients()));
        realm.setClientRoles(new Flattened2DList<Client, ClientRole>() {
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
        realm.setGroups(new NestedIndexedEntityTemplateWrapperList<>(realm, groupTemplate));
    }

}
