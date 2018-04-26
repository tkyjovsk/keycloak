package org.keycloak.performance.dataset.idm;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 *
 * @author tkyjovsk
 */
public class ClientRoleMappings extends RoleMappings<ClientRole> {

    @JsonBackReference
    private final Client client;

    public ClientRoleMappings(RoleMapper roleMapper, Client client) {
        super(roleMapper);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        return String.format("%s/role-mappings/%s", getRoleMapper(), getClient());
    }

}
