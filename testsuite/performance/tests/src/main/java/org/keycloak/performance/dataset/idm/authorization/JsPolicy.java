package org.keycloak.performance.dataset.idm.authorization;

import javax.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.authorization.JSPolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class JsPolicy extends Policy<JSPolicyRepresentation> {

    public JsPolicy(ResourceServer resourceServer, int index, JSPolicyRepresentation representation) {
        super(resourceServer, index, representation);
    }

    @Override
    public JSPolicyRepresentation read(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().js().findByName(getRepresentation().getName());
    }

    @Override
    public Response create(Keycloak adminClient) {
        return getResourceServer().authorization(adminClient).policies().js().create(getRepresentation());
    }

    @Override
    public void update(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().js().findById(getId()).update(getRepresentation());
    }

    @Override
    public void delete(Keycloak adminClient) {
        getResourceServer().authorization(adminClient).policies().js().findById(getId()).remove();
    }

}
