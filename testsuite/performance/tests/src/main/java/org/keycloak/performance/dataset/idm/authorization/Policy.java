package org.keycloak.performance.dataset.idm.authorization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.keycloak.performance.dataset.NestedIndexedEntity;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.Logic;

/**
 *
 * @author tkyjovsk
 */
public abstract class Policy extends NestedIndexedEntity<ResourceServer> {

    private String name;
    private String description;
    private Logic logic;
    private DecisionStrategy decisionStrategy;

    public Policy(ResourceServer resourceServer, int index) {
        super(resourceServer, index);
    }

    @Override
    public String toString() {
        return getName();
    }
    
    @JsonBackReference
    public ResourceServer getResourceServer() {
        return getParentEntity();
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

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public abstract String getType();

    public DecisionStrategy getDecisionStrategy() {
        return decisionStrategy;
    }

    public void setDecisionStrategy(DecisionStrategy decisionStrategy) {
        this.decisionStrategy = decisionStrategy;
    }

}
