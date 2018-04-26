package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.idm.authorization.ResourceServerTemplate;
import static org.keycloak.performance.util.StringUtil.parseStringList;

/**
 *
 * @author tkyjovsk
 */
public class ClientTemplate extends NestedIndexedEntityTemplate<Realm, Client> {

    private final int clientsPerRealm;

    private final ClientRoleTemplate clientRoleTemplate;
    private final ResourceServerTemplate resourceServerTemplate;

    public ClientTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        this.clientsPerRealm = configuration.getInt("clientsPerRealm", 0);
        this.clientRoleTemplate = new ClientRoleTemplate(this);
        this.resourceServerTemplate = new ResourceServerTemplate(this);
        registerAttributeTemplate("clientId");
        registerAttributeTemplate("name");
        registerAttributeTemplate("description");
        registerAttributeTemplate("rootUrl");
        registerAttributeTemplate("adminUrl");
        registerAttributeTemplate("baseUrl");
        registerAttributeTemplate("enabled");
        registerAttributeTemplate("secret");
        registerAttributeTemplate("redirectUris");
        registerAttributeTemplate("webOrigins");
        registerAttributeTemplate("protocol");
        registerAttributeTemplate("type");
        registerAttributeTemplate("serviceAccountsEnabled");
        registerAttributeTemplate("authorizationServicesEnabled");
    }

    @Override
    public synchronized Client produce(Realm realm, int index) {
        Client client = new Client(realm, index);

        client.setClientId(
                processAttribute("clientId", client));
        client.setName(
                processAttribute("name", client));
        client.setDescription(
                processAttribute("description", client));
        client.setRootUrl(
                processAttribute("rootUrl", client));
        client.setAdminUrl(
                processAttribute("adminUrl", client));
        client.setBaseUrl(
                processAttribute("baseUrl", client));
        client.setEnabled(Boolean.parseBoolean(
                processAttribute("enabled", client)));
        client.setSecret(
                processAttribute("secret", client));
        client.setRedirectUris(parseStringList(
                processAttribute("redirectUris", client)));
        client.setWebOrigins(parseStringList(
                processAttribute("webOrigins", client)));
        client.setProtocol(
                processAttribute("protocol", client));
        client.setType(
                processAttribute("type", client));
        client.setAuthorizationServicesEnabled(Boolean.parseBoolean(
                processAttribute("authorizationServicesEnabled", client)));
        client.setServiceAccountsEnabled(Boolean.parseBoolean(
                processAttribute("serviceAccountsEnabled", client)));
        
        client.setClientRoles(new NestedIndexedEntityTemplateWrapperList<>(client, clientRoleTemplate));
        
        if (client.isAuthorizationServicesEnabled()) {
            client.setResourceServer(resourceServerTemplate.produce(client));
        }

        return client;
    }

    public ClientRoleTemplate getClientRoleTemplate() {
        return clientRoleTemplate;
    }

    public ResourceServerTemplate getResourceServerTemplate() {
        return resourceServerTemplate;
    }

    @Override
    public int getEntityCountPerParent() {
        return getClientsPerRealm();
    }

    public int getClientsPerRealm() {
        return clientsPerRealm;
    }

    @Override
    public void validateSizeConfiguration() {
        // sizing
        logger().info(String.format("clientsPerRealm: %s", clientsPerRealm));
        validateInt().minValue(clientsPerRealm, 0);

        clientRoleTemplate.validateSizeConfiguration();
    }
    
}
