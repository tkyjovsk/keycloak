package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.performance.dataset.idm.authorization.Scope;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;

/**
 *
 * @author tkyjovsk
 */
public class ScopeTemplate extends NestedIndexedEntityTemplate<ResourceServer, Scope> {

    private final int scopesPerResourceServer;

    public ScopeTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        registerAttributeTemplate("name");
        registerAttributeTemplate("displayName");
        this.scopesPerResourceServer = configuration.getInt("scopesPerResourceServer", 0);
    }

    public int getScopesPerResourceServer() {
        return scopesPerResourceServer;
    }

    @Override
    public Scope produce(ResourceServer resourceServer, int index) {
        Scope scope = new Scope(resourceServer, index);
        scope.setName(
                processAttribute("name", scope));
        scope.setDisplayName(
                processAttribute("displayName", scope));
        
        return scope;
    }

    @Override
    public int getEntityCountPerParent() {
        return scopesPerResourceServer;
    }

    @Override
    public void validateSizeConfiguration() {
        logger().info(String.format("scopesPerResourceServer: %s", scopesPerResourceServer));
        validateInt().minValue(scopesPerResourceServer, 0);
    }

}
