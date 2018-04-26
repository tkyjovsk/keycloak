package org.keycloak.performance.templates;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.keycloak.performance.dataset.IndexedEntityM;
import static org.keycloak.performance.util.StringUtil.firstLetterToLowerCase;

/**
 *
 * @author tkyjovsk
 */
public abstract class IndexedEntityMTemplate<ER, EM extends IndexedEntityM<ER>> {

    protected Configuration configuration;
    protected freemarker.template.Configuration freemarkerConfiguration;

    Map<String, Template> attributeTemplates = new LinkedHashMap<>();

    public IndexedEntityMTemplate(Configuration configuration, freemarker.template.Configuration freemarkerConfiguration) throws IOException {
        this.configuration = configuration;
        this.freemarkerConfiguration = freemarkerConfiguration;
        registerAttributeTemplates();
    }

    public String configPrefix() {
        return firstLetterToLowerCase(this.getClass().getSimpleName().replaceFirst("Template$", ""));
    }

    private void registerAttributeTemplates() throws IOException {
        Iterator<String> configKeys = configuration.getKeys(configPrefix());
        while (configKeys.hasNext()) {
            String configKey = configKeys.next();
            String attributeName = configKey.replaceFirst(configPrefix() + ".", "");
            String attributeTemplateDefinition = configuration.getString(configKey);
            System.out.println("template: " + attributeName + " = " + attributeTemplateDefinition);
            attributeTemplates.put(attributeName, new Template(configKey, attributeTemplateDefinition, freemarkerConfiguration));
        }
    }

    public abstract EM newEM(int index);

    public EM produce(int index) {
        EM em = newEM(index);
        attributeTemplates.keySet().forEach((attributeName) -> {
            try (StringWriter stringWriter = new StringWriter()) {
                System.out.println("processing " + attributeName);
                attributeTemplates.get(attributeName).process(em, stringWriter);
                em.put(attributeName, stringWriter.toString());
            } catch (IOException | TemplateException ex) {
                throw new RuntimeException(ex);
            }
        });
        return em;
    }

}
