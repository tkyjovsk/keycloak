package org.keycloak.performance.templates;

import java.io.File;
import java.util.List;
import org.apache.commons.configuration.CombinedConfiguration;
import org.keycloak.performance.templates.idm.RealmTemplate;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.keycloak.performance.dataset.Dataset;
import org.keycloak.performance.dataset.DatasetRepresentation;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.performance.iteration.Flattened2DList;
import org.keycloak.performance.util.CombinedConfigurationNoInterpolation;
import static org.keycloak.performance.util.ConfigurationUtil.loadFromFile;

/**
 *
 * @author tkyjovsk
 */
public class DatasetTemplate extends EntityTemplate<Dataset, DatasetRepresentation> {

    private final RealmTemplate realmTemplate;

    public DatasetTemplate(Configuration configuration) {
        super(configuration);
        this.realmTemplate = new RealmTemplate(this);
    }

    public DatasetTemplate() {
        this(loadConfiguration());
    }

    protected static Configuration loadConfiguration() {
        try {
            CombinedConfiguration configuration = new CombinedConfigurationNoInterpolation();
            String sizePropertiesFile = System.getProperty("dataset.size.properties.file", "dataset/size/default.properties");//CONFIG.getString("dataset.size.properties.file", "dataset/size/default.properties");
            String templatingPropertiesFile = System.getProperty("dataset.templating.properties.file", "dataset/templating/default.properties");//CONFIG.getString("dataset.templating.properties.file", "dataset/templating/default.properties");
            configuration.addConfiguration(loadFromFile(new File(sizePropertiesFile)));
            configuration.addConfiguration(loadFromFile(new File(templatingPropertiesFile)));
            return configuration;
        } catch (ConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Dataset newEntity() {
        return new Dataset(new DatasetRepresentation());
    }

    @Override
    public void processMappings(Dataset dataset) {
        dataset.setRealms(new NestedIndexedEntityTemplateWrapperList<>(dataset, realmTemplate));

        dataset.setUsers(new Flattened2DList<Realm, User>() {
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
    }

    @Override
    public void validateConfiguration() {
        // sizing
        realmTemplate.validateConfiguration();
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
