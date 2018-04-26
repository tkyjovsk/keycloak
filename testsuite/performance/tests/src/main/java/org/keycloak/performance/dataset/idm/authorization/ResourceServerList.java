package org.keycloak.performance.dataset.idm.authorization;

import java.util.AbstractList;
import java.util.List;
import org.keycloak.performance.dataset.idm.Client;

/**
 *
 * @author tkyjovsk
 */
public class ResourceServerList extends AbstractList<ResourceServer> {

    List<Client> clients;
    List<ResourceServer> resourceServers;

    public ResourceServerList(List<Client> clients) {
        this.clients = clients;
        refreshFromClients();
    }

    public void refreshFromClients() {
        
    }

    @Override
    public ResourceServer get(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
