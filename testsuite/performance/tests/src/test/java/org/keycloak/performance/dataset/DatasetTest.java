package org.keycloak.performance.dataset;

import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.dataset.idm.ClientRole;
import org.keycloak.performance.dataset.idm.User;
import org.keycloak.performance.dataset.idm.RealmRole;
import org.keycloak.performance.dataset.idm.Client;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.keycloak.performance.AbstractTest;
import org.keycloak.performance.TestConfig;
import org.keycloak.performance.dataset.idm.ClientRoleMappings;
import org.keycloak.performance.iteration.RandomSublist;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 */
public class DatasetTest extends AbstractTest {

    Dataset dataset = TestConfig.DATASET;

    @Test
    @Ignore
    public void simplePrintout() {

        for (Realm r : dataset.getRealms()) {
            logger.info(r.getRealm());
            logger.info("--- clients");
            for (Client c : r.getClients()) {
                logger.info(c.getClientId());
                for (ClientRole cr : c.getClientRoles()) {
//                    logger.info(ToStringBuilder.reflectionToString(cr, ToStringStyle.MULTI_LINE_STYLE));
                    logger.info(String.format("%s (%s)", cr.getName(), cr.getDescription()));
                }
            }
            logger.info("--- all client roles");
            for (ClientRole cr : r.getClientRoles()) {
                logger.info(cr.getName());
            }
            logger.info("--- random client roles sublist");
            for (ClientRole cr : new RandomSublist<>(r.getClientRoles(), r.hashCode(), 15)) {
                logger.info(cr.getName());
            }
            logger.info("--- unique random client roles sublist");
            for (ClientRole cr : new RandomSublist<>(r.getClientRoles(), r.hashCode(), 15, true)) {
                logger.info(cr.getName());
            }
            logger.info("--- realm roles");
            for (RealmRole rr : r.getRealmRoles()) {
                logger.info(String.format("%s (%s)", rr.getName(), rr.getDescription()));
            }
            logger.info("--- users");
            for (User u : r.getUsers()) {
                logger.info(ToStringBuilder.reflectionToString(u, ToStringStyle.MULTI_LINE_STYLE));
//                logger.info(u.getUsername());
                for (RealmRole urr : u.getRealmRoles()) {
                    logger.info(" - " + urr.getName());
                }
                for (ClientRole ucr : u.getClientRoles()) {
                    logger.info(" - " + ucr.getName());
                }
            }
        }

    }

    @Test
    @Ignore
    public void equalsTest() {
        User u0 = dataset.getUsers().get(0);
        User u1 = dataset.getUsers().get(1);
        assertEquals(u0, u0);
        assertNotEquals(u0, u1);
    }

    @Test
    @Ignore
    public void roleMappings() {
        User u = dataset.getRealms().get(0).getUsers().get(0);
        
        logger.info("USER REALM ROLES");
        logger.info(u.getRealmRoles());
        logger.info("USER REALM ROLE MAPPINGS");
        logger.info(u.getRealmRoleMappings().getRoles());

        logger.info("USER CLIENT ROLES");
        logger.info(u.getClientRoles());
        logger.info("USER CLIENT ROLE MAPPINGS");
        for (ClientRoleMappings crm : u.getClientRoleMappingsList()) {
            logger.info(crm.getClient() + " --> " + crm.getRoles());
        }
    }

    @Test
    @Ignore
    public void toJSON() throws IOException {
        logger.info("REALM JSON: \n" + dataset.getRealms().get(0).toJSON());
        logger.info("CLIENT JSON: \n" + dataset.getRealms().get(0).getClients().get(0).toJSON());
        logger.info("USER JSON: \n" + dataset.getRealms().get(0).getUsers().get(0).toJSON());
        logger.info("CREDENTIAL JSON: \n" + dataset.getRealms().get(0).getUsers().get(0).getCredentials().get(0).toJSON());
        logger.info("REALM ROLE MAPPINGS: \n" + dataset.getRealms().get(0).getUsers().get(0).getRealmRoleMappings().toJSON());
    }

    @Test
    @Ignore
    public void pojoToMap() throws IOException {
        logger.info("POJO TO MAP");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper
                .convertValue(dataset.getRealms().get(0),
                        new TypeReference<Map<String, Object>>() {
                });
        map.put("index", 1000);

        logger.info(map);
        logger.info("JSON:");
        logger.info(writeValueAsString(map));
    }
    
    
    @Test
    @Ignore
    public void testStreams() {
        
        dataset.realms().forEach(r -> logger.info(r.toString()));
        dataset.realmRoles().forEach(rr -> logger.info(rr.toString()));
        dataset.clients().forEach(c -> logger.info(c.toString()));
        dataset.clientRoles().forEach(cr -> logger.info(cr.toString()));
        dataset.users().forEach(u -> logger.info(u.toString()));

    }

    
}