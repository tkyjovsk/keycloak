package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import org.keycloak.performance.dataset.NestedIndexedEntity;

/**
 *
 * @author tkyjovsk
 */
public class Client extends NestedIndexedEntity<Realm> {

    @JsonIgnore
    private List<ClientRole> clientRoles;

    private String clientId;
    private String name;
    private String description;
    private String rootUrl;
    private String adminUrl;
    private String baseUrl;
    private boolean enabled;
    private String secret;
    private List<String> redirectUris;
    private List<String> webOrigins;
    private String protocol;
    boolean bearerOnly;
    boolean publicClient;

    private boolean serviceAccountsEnabled;
    private boolean authorizationServicesEnabled;

    public Client(Realm realm, int index) {
        super(realm, index);
    }

    @Override
    public String toString() {
        return getClientId();
    }

    public boolean isBearerOnly() {
        return bearerOnly;
    }

    public void setBearerOnly(boolean bearerOnly) {
        if (bearerOnly && isPublicClient()) {
            throw new IllegalArgumentException("Client cannot be bearer-only and public at the same time.");
        }
        this.bearerOnly = bearerOnly;
    }

    public boolean isPublicClient() {
        return publicClient;
    }

    public void setPublicClient(boolean publicClient) {
        if (publicClient && isBearerOnly()) {
            throw new IllegalArgumentException("Client cannot be public and bearer-only at the same time.");
        }
        this.publicClient = publicClient;
    }

    @JsonIgnore
    public Realm getRealm() {
        return getParentEntity();
    }

    public List<ClientRole> getClientRoles() {
        return clientRoles;
    }

    public void setClientRoles(List<ClientRole> clientRoles) {
        this.clientRoles = clientRoles;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public List<String> getWebOrigins() {
        return webOrigins;
    }

    public void setWebOrigins(List<String> webOrigins) {
        this.webOrigins = webOrigins;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @JsonIgnore
    public String getType() {
        return isBearerOnly() ? "bearer-only"
                : isPublicClient() ? "public"
                        : "confidential";
    }

    @JsonIgnore
    public void setType(String type) {
        switch (type) {
            case "bearer-only":
                setBearerOnly(true);
                setPublicClient(false);
                break;
            case "public":
                setBearerOnly(false);
                setPublicClient(true);
                break;
            case "confidential":
                setBearerOnly(false);
                setPublicClient(false);
                break;
            default:
                throw new IllegalArgumentException("Unknown client type: " + type);
        }
    }

    public boolean isServiceAccountsEnabled() {
        return serviceAccountsEnabled;
    }

    public void setServiceAccountsEnabled(boolean serviceAccountsEnabled) {
        this.serviceAccountsEnabled = serviceAccountsEnabled;
    }

    public boolean isAuthorizationServicesEnabled() {
        return authorizationServicesEnabled;
    }

    public void setAuthorizationServicesEnabled(boolean authorizationServicesEnabled) {
        this.authorizationServicesEnabled = authorizationServicesEnabled;
    }

}
