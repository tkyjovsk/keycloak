package org.keycloak.testsuite.arquillian.containers;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.management.remote.JMXServiceURL;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 *
 * @author tkyjovsk
 */
public class InfinispanServerDeployableContainer implements DeployableContainer<InfinispanServerConfiguration> {

    protected static final Logger log = Logger.getLogger(InfinispanServerDeployableContainer.class);

    InfinispanServerConfiguration configuration;
    private Process infinispanServerProcess;

    private File pidFile;
    private JMXServiceURL jmxServiceURL;
    private static final String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";

    @Override
    public Class<InfinispanServerConfiguration> getConfigurationClass() {
        return InfinispanServerConfiguration.class;
    }

    @Override
    public void setup(InfinispanServerConfiguration configuration) {
        this.configuration = configuration;
        pidFile = new File(configuration.getInfinispanHome(), "bin/server.pid");
    }

    @Override
    public void start() throws LifecycleException {
        List<String> commands = new ArrayList<>();
        commands.add("./server.sh");
        commands.add("-c");
        commands.add(configuration.getServerConfig());
        commands.add("-o");
        commands.add(configuration.getPortOffset().toString());
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb = pb.directory(new File(configuration.getInfinispanHome(), "/bin")).inheritIO().redirectErrorStream(true);
        pb.environment().put("LAUNCH_ISPN_IN_BACKGROUND", "false");
        pb.environment().put("ISPN_PIDFILE", pidFile.getAbsolutePath());
        try {
            log.info("Starting Infinispan server");
            log.info(configuration.getInfinispanHome());
            log.info(commands);
            infinispanServerProcess = pb.start();

            long startTimeMillis = System.currentTimeMillis();
            long startupTimeoutMillis = 30 * 1000;
            URL consoleURL = new URL(String.format("http://localhost:%s/console/", 11222 + configuration.getPortOffset()));

            while (true) {
                Thread.sleep(1000);
                if (System.currentTimeMillis() > startTimeMillis + startupTimeoutMillis) {
                    stop();
                    throw new LifecycleException("Infinispan server startup timed out.");
                }

                HttpURLConnection connection = (HttpURLConnection) consoleURL.openConnection();
                connection.setReadTimeout(1000);
                connection.setConnectTimeout(1000);
                try {
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        break;
                    }
                    connection.disconnect();
                } catch (ConnectException ex) {
                    // ignoring
                }
            }

            log.info("Infinispan server started.");

        } catch (IOException ex) {
            throw new LifecycleException("Unable to start Infinispan server.", ex);
        } catch (InterruptedException ex) {
            log.error("Infinispan server startup process interupted.", ex);
            stop();
        }
    }

    @Override
    public void stop() throws LifecycleException {
        log.info("Stopping Infinispan server");
        infinispanServerProcess.destroy();
        try {
            infinispanServerProcess.waitFor(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("Unable to stop Infinispan server within timeout. Stopping forcibly.");
            infinispanServerProcess.destroyForcibly();
        }
        log.info("Infinispan server stopped");
    }

    private long getPID() throws IOException {
        if (pidFile == null) {
            throw new IllegalStateException(String.format("Unable to find PID file '%s'", pidFile));
        }
        return Long.parseLong(Files.readString(pidFile.toPath()).trim());
    }

    /**
     * Attach to a local Infinispan JVM, launch a management-agent, and return
     * its JMXServiceURL.
     *
     * @return
     */
    public JMXServiceURL getJMXServiceURL() {
        if (jmxServiceURL == null) {
            VirtualMachine vm;
            try {
                vm = VirtualMachine.attach(String.valueOf(getPID()));
            } catch (AttachNotSupportedException | IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                jmxServiceURL = new JMXServiceURL(vm.startLocalManagementAgent());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                try {
                    vm.detach();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return jmxServiceURL;
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return ProtocolDescription.DEFAULT;
    }

    @Override
    public ProtocolMetaData deploy(Archive<?> archv) throws DeploymentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void undeploy(Archive<?> archv) throws DeploymentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deploy(Descriptor d) throws DeploymentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void undeploy(Descriptor d) throws DeploymentException {
        throw new UnsupportedOperationException();
    }

}
