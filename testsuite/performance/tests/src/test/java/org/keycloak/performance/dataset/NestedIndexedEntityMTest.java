package org.keycloak.performance.dataset;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.keycloak.performance.TestConfig;
import org.keycloak.performance.dataset.idm.RealmM;
import org.keycloak.performance.dataset.idm.UserM;
import org.keycloak.performance.dataset.idm.authorization.RolePolicyM;
import org.keycloak.performance.templates.idm.RealmMTemplate;
import org.keycloak.performance.templates.idm.authorization.RolePolicyMTemplate;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class NestedIndexedEntityMTest {

    @Test
    public void testRealm() throws IOException {
        RealmM realm = new RealmM(1111);
        realm.getRepresentation().setRealm("realm_0");
        realm.getRepresentation().setEnabled(true);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("a", "A");
        attributes.put("b", "B");
        realm.getRepresentation().setAttributes(attributes);

        realm.mapFromEntity();

        System.out.println("MAP");
        System.out.println(realm);
        System.out.println("MAP JSON");
        System.out.println(realm.mapJSON());
        System.out.println("REP JSON");
        System.out.println(realm.entityJSON());

        realm.put("accessCodeLifespan", 60);
        realm.put("passwordPolicy", "hashIterations(1)");

//        Map m = new HashMap(realm);
//        m.remove("index");
//        String json = writeValueAsString(m);
//        RealmRepresentation rr = MAPPER.readValue(json, realm.getEntityRepresentation().getClass());
        realm.entityFromMap();

        System.out.println("MAP");
        System.out.println(realm);
        System.out.println("MAP JSON");
        System.out.println(realm.mapJSON());
        System.out.println("REP JSON");
        System.out.println(realm.entityJSON());
        RealmRepresentation rr = realm.getRepresentation();

        System.out.println("rr: " + rr);
        assertTrue(realm.getRepresentation() instanceof RealmRepresentation);
    }

    @Test
    public void testUser() throws IOException {
        UserM userM = new UserM(1234);
        UserRepresentation u = userM.getRepresentation();
        u.setUsername("john");
        u.setEmail("johnny@email.test");
        u.setEnabled(true);

        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("a", Arrays.asList(new String[]{"a1", "a2"}));
        attrs.put("b", Arrays.asList(new String[]{"b1", "b2", "b3"}));
        u.setAttributes(attrs);

        userM.mapFromEntity();

        System.out.println("MAP");
        System.out.println(userM);
        System.out.println("MAP JSON");
        System.out.println(userM.mapJSON());
        System.out.println("REP JSON");
        System.out.println(userM.entityJSON());

        userM.put("firstName", "John");
        userM.put("lastName", "Rambo");
        userM.entityFromMap();

        System.out.println("MAP");
        System.out.println(userM);
        System.out.println("MAP JSON");
        System.out.println(userM.mapJSON());
        System.out.println("REP JSON");
        System.out.println(userM.entityJSON());

        assertTrue(userM.getRepresentation() instanceof UserRepresentation);
    }

    @Test
    public void testMTemplates() throws IOException {
        RealmMTemplate rt = new RealmMTemplate(TestConfig.CONFIG, TestConfig.FREEMARKER_CONFIG);

        RealmM rm = rt.produce(1234);
        rm.entityFromMap();
        System.out.println("realm from template: " + rm.entityJSON());

        RolePolicyMTemplate rpt = new RolePolicyMTemplate(TestConfig.CONFIG, TestConfig.FREEMARKER_CONFIG);
        RolePolicyM rpm = rpt.produce(1351435);
        rpm.entityFromMap();
        System.out.println("role policy from template: " + rpm.entityJSON());
    }

}
