package org.keycloak.performance.templates;

import java.util.List;
import org.keycloak.performance.templates.idm.RealmTemplate;
import org.apache.commons.configuration.Configuration;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.performance.iteration.FlattenedListOfLists;
import org.keycloak.performance.templates.idm.authorization.ResourceTypeTemplate;

/**
 *
 * @author tkyjovsk
 */
public class DatasetTemplate extends EntityTemplate<Dataset> {

    private final RealmTemplate realmTemplate;
    private final ResourceTypeTemplate resourceTypeTemplate;

    public DatasetTemplate(Configuration configuration, freemarker.template.Configuration freemarkerConfiguration) {
        super(configuration, freemarkerConfiguration);
        this.realmTemplate = new RealmTemplate(this);
        this.resourceTypeTemplate = new ResourceTypeTemplate(this);
    }

    @Override
    public Dataset produce(int index) {
        Dataset dataset = new Dataset(index);
        dataset.setRealms(new NestedEntityTemplateWrapperList<>(dataset, realmTemplate));
        dataset.setResourceTypes(new NestedEntityTemplateWrapperList<>(dataset, resourceTypeTemplate));

        dataset.setUsers(new FlattenedListOfLists<Realm, User>() {
            @Override
            public List<Realm> getXList() {
                return dataset.getRealms();
            }

            @Override
            public List<User> getYList(Realm realm) {
                return realm.getUsers();
            }

            @Override
            public int getYListSize() {
                return realmTemplate.getUserTemplate().getUsersPerRealm();
            }
        });

        return dataset;
    }

    @Override
    public void validateSizeConfiguration() {
        // sizing
        realmTemplate.validateSizeConfiguration();
        resourceTypeTemplate.validateSizeConfiguration();
    }

    public int getTotalRealmRolesCount() {
        return realmTemplate.getRealms()
                * realmTemplate.getRealmRoleTemplate().getEntityCountPerParent();
    }

    public int getTotalClientsCount() {
        return realmTemplate.getRealms()
                * realmTemplate.getClientTemplate().getClientsPerRealm();
    }

    public int getTotalClientRolesCount() {
        return realmTemplate.getRealms()
                * realmTemplate.getClientTemplate().getClientsPerRealm()
                * realmTemplate.getClientTemplate().getClientRoleTemplate().getClientRolesPerClient();
    }

    public int getTotalUserCount() {
        return realmTemplate.getRealms()
                * realmTemplate.getUserTemplate().getUsersPerRealm();
    }

    public String toStringTotalCount() {
        return String.format("Total dataset size:\n"
                + "total realms: %s\n"
                + "total realm roles: %s\n"
                + "total clients: %s\n"
                + "total client roles: %s\n"
                + "total users: %s",
                realmTemplate.getRealms(),
                getTotalRealmRolesCount(),
                getTotalClientsCount(),
                getTotalClientRolesCount(),
                getTotalUserCount()
        );
    }

}
