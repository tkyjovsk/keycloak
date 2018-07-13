package org.keycloak.performance.dataset.idm.authorization;

import java.util.List;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ScopePermission extends Policy<ScopePermissionRepresentation> {

    private List<Scope> scopes;
    private List<Policy> policies;

    public ScopePermission(ResourceServer resourceServer, int index, ScopePermissionRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public ScopePermissionRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).permissions().scope().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).permissions().scope().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).permissions().scope().findById(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).permissions().scope().findById(getId()).remove();
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

}
