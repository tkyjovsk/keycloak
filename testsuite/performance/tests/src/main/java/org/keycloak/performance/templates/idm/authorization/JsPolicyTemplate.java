package org.keycloak.performance.templates.idm.authorization;

import org.keycloak.performance.dataset.idm.authorization.JsPolicy;
import org.keycloak.performance.dataset.idm.authorization.ResourceServer;
import org.keycloak.representations.idm.authorization.JSPolicyRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class JsPolicyTemplate extends PolicyTemplate<JsPolicy, JSPolicyRepresentation> {

    private final int jsPoliciesPerResourceServer;

    public JsPolicyTemplate(ResourceServerTemplate resourceServerTemplate) {
        super(resourceServerTemplate);
        this.jsPoliciesPerResourceServer = getConfiguration().getInt("jsPoliciesPerResourceServer", 0);
    }

    public int getJsPoliciesPerResourceServer() {
        return jsPoliciesPerResourceServer;
    }

    @Override
    public int getEntityCountPerParent() {
        return jsPoliciesPerResourceServer;
    }

    @Override
    public void validateConfiguration() {
        logger().info(String.format("jsPoliciesPerResourceServer: %s", jsPoliciesPerResourceServer));
        validateInt().minValue(jsPoliciesPerResourceServer, 0);
    }

    @Override
    public JsPolicy newEntity(ResourceServer parentEntity, int index) {
        return new JsPolicy(parentEntity, index, new JSPolicyRepresentation());
    }

    @Override
    public void processMappings(JsPolicy policy) {
    }

}
