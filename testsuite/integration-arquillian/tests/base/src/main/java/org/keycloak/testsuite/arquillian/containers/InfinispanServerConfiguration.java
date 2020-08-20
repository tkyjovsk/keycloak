package org.keycloak.testsuite.arquillian.containers;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

/**
 *
 * @author tkyjovsk
 */
public class InfinispanServerConfiguration implements ContainerConfiguration {

    private String infinispanHome;
    private String serverConfig;
    private String site;
    private Integer portOffset;

    @Override
    public void validate() throws ConfigurationException {
    }
    
    public String getInfinispanHome() {
        return infinispanHome;
    }

    public void setInfinispanHome(String infinispanHome) {
        this.infinispanHome = infinispanHome;
    }

    public String getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(String serverConfig) {
        this.serverConfig = serverConfig;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
    
    public Integer getPortOffset() {
        return portOffset;
    }

    public void setPortOffset(Integer portOffset) {
        this.portOffset = portOffset;
    }

}
