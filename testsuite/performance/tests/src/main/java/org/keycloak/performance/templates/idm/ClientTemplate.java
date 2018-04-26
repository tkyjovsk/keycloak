package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.idm.Client;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.templates.NestedEntityTemplate;
import org.keycloak.performance.templates.NestedEntityTemplateWrapperList;
import static org.keycloak.performance.util.StringUtil.parseStringList;
import static org.keycloak.performance.util.ValidationUtil.VALIDATE_INT;

/**
 *
 * @author tkyjovsk
 */
public class ClientTemplate extends NestedEntityTemplate<Realm, Client> {

    private final int clientsPerRealm;

    private final ClientRoleTemplate clientRoleTemplate;

    public ClientTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        this.clientsPerRealm = configuration.getInt("clientsPerRealm");
        this.clientRoleTemplate = new ClientRoleTemplate(this);
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

        client.setClientRoles(new NestedEntityTemplateWrapperList<>(client, clientRoleTemplate));

        return client;
    }

    public ClientRoleTemplate getClientRoleTemplate() {
        return clientRoleTemplate;
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
        logger.info(String.format("clientsPerRealm: %s", clientsPerRealm));
        VALIDATE_INT.minValue(clientsPerRealm, 0);

        clientRoleTemplate.validateSizeConfiguration();
    }

}
