package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.Scope;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class ScopeTemplate extends NestedIndexedEntityTemplate<ResourceServer, Scope, ScopeRepresentation> {

    private final int scopesPerResourceServer;

    public ScopeTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.scopesPerResourceServer = getConfiguration().getInt("scopesPerResourceServer", 0);
    }

    public int getScopesPerResourceServer() {
        return scopesPerResourceServer;
    }

    @Override
    public int getEntityCountPerParent() {
        return scopesPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("scopesPerResourceServer: %s", scopesPerResourceServer));
        validateInt().minValue(scopesPerResourceServer, 0);
    }

    @Override
    public Scope newEntity(ResourceServer parentEntity, int index) {
        return new Scope(parentEntity, index, new ScopeRepresentation());
    }

    @Override
    public void processMappings(Scope entity) {
    }

}
