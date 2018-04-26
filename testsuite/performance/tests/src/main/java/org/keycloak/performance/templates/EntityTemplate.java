package org.keycloak.performance.templates;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.jboss.logging.Logger;
import org.keycloak.performance.dataset.IndexedEntity;
import static org.keycloak.performance.util.StringUtil.firstLetterToLowerCase;

/**
 *
 * @author tkyjovsk
 * @param <E> entity
 */
public abstract class EntityTemplate<E extends IndexedEntity> {

    protected Logger logger = org.jboss.logging.Logger.getLogger(this.getClass());

    private final Map<String, Template> attributeTemplates = new HashMap<>();
    protected final Configuration configuration;
    protected final freemarker.template.Configuration freemarkerConfiguration;

    public EntityTemplate(Configuration configuration, freemarker.template.Configuration freemarkerConfiguration) {
        this.configuration = configuration;
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    public String getEntityPrefix() {
        return firstLetterToLowerCase(this.getClass().getSimpleName().replaceFirst("Template$", ""));
    }

    protected String getAttributeKey(String attributeName) {
        return getEntityPrefix() + "." + attributeName;
    }

    protected final void registerAttributeTemplate(String attributeName) {
        String configKey = getAttributeKey(attributeName);
        String configValue = configuration.getString(configKey);
        logger.debug(String.format("Template: %s: %s", configKey, configValue));
        try {
            attributeTemplates.put(configKey, new Template(configKey, configValue, getFreemarkerConfiguration()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String processAttribute(String attribute, Object model) {
        String value;
        try (StringWriter stringWriter = new StringWriter()) {
            attributeTemplates.get(getAttributeKey(attribute)).process(model, stringWriter);
            value = stringWriter.toString();
        } catch (IOException | TemplateException ex) {
            throw new RuntimeException(ex);
        }
        return value;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public freemarker.template.Configuration getFreemarkerConfiguration() {
        return freemarkerConfiguration;
    }

    public abstract void validateSizeConfiguration();

    public abstract E produce(int index);

}
