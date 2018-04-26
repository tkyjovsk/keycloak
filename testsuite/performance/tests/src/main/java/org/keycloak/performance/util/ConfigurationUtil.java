package org.keycloak.performance.util;

import java.util.Iterator;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import static org.jboss.logging.Logger.Level.INFO;

/**
 *
 * @author tkyjovsk
 */
public class ConfigurationUtil {

    public static void logConfigurationState(Configuration c, Logger logger) {
        logConfigurationState(c, logger, INFO);
    }

    public static void logConfigurationState(Configuration c, Logger logger, Level logLevel) {
        Iterator<String> configKeys = c.getKeys();
        while (configKeys.hasNext()) {
            String k = configKeys.next();
            logger.log(logLevel, String.format("Configuration: %s: %s", k, c.getProperty(k)));
        }
    }

    public static PropertiesConfiguration newPropertiesConfiguration() {
        return newPropertiesConfiguration(false);
    }

    public static PropertiesConfiguration newPropertiesConfiguration(boolean listParsing) {
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        configuration.setDelimiterParsingDisabled(!listParsing);
        return configuration;
    }

    public static PropertiesConfiguration loadFromFile(String filename) throws ConfigurationException {
        // this is needed to disable interpreting comma-delimited string properties as lists
        return loadFromFile(filename, false);
    }

    public static PropertiesConfiguration loadFromFile(String filename, boolean listParsing) throws ConfigurationException {
        PropertiesConfiguration configuration = newPropertiesConfiguration(listParsing);
        configuration.load(filename);
        return configuration;
    }

}
