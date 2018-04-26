package org.keycloak.performance.dataset;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.Validate;
import static org.keycloak.performance.util.ValidationUtil.VALIDATE_INT;
import static org.keycloak.util.JsonSerialization.writeValueAsString;

/**
 *
 * @author tkyjovsk
 * @param <ER> entity representation type
 */
public abstract class IndexedEntityM<ER> extends ConcurrentHashMap<String, Object> {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final TypeReference TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {
    };

    static {
        MAPPER.setSerializationInclusion(Include.NON_NULL);
    }

    private ER representation;

    public IndexedEntityM(int index, ER representation) {
        VALIDATE_INT.minValue(index, 0);
        setIndex(index);
        Validate.notNull(representation);
        this.representation = representation;
    }

    public synchronized final int getIndex() {
        return (int) get("index");
    }

    public synchronized final void setIndex(int index) {
        put("index", index);
    }

    public synchronized ER getRepresentation() {
        System.out.println("entity class: " + representation.getClass());
        return representation;
    }

    public synchronized void setRepresentation(ER representation) {
        this.representation = representation;
    }

    public synchronized void mapFromEntity() {
        Map<String, Object> reloadedMap = MAPPER.convertValue(representation, TYPE_REFERENCE);
        int index = getIndex();
        clear();
        putAll(reloadedMap);
        setIndex(index);
    }

    public synchronized void entityFromMap() throws IOException {
        ER e = MAPPER.readValue(mapJSON(), (Class<ER>) getRepresentation().getClass());
        setRepresentation(e);
    }

    public synchronized String mapJSON() throws IOException {
        int index = getIndex();
        remove("index");
        String mapJSON = writeValueAsString(this);
        setIndex(index);
        return mapJSON;
    }

    public synchronized String entityJSON() throws IOException {
        return writeValueAsString(representation);
    }

}
