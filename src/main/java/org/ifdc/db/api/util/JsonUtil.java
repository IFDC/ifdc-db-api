package org.ifdc.db.api.util;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.ifdc.db.api.me.ActivityAPI;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class JsonUtil {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonFactory FACTORY = new JsonFactory();
    public static final String EMPTY_ARRAY = "[]";
    public static final String EMPTY_DOC = "{}";
    public static final String NULL = "null";

//    public static String toJsonStr(Document data) throws JsonProcessingException {
//        return data.toJson();
//    }

//    public static String toJsonStr(ObjectId data) throws JsonProcessingException {
//        StringBuilder sb = new StringBuilder();
//        sb.append("{ \"$oid\" : \"").append(data.toString()).append("\" }");
//        return sb.toString();
//    }
    
    public static String toJsonStr(Object data){
        try {
        return new String(toJsonByteArray(data));
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return null;
        }
    }

    public static byte[] toJsonByteArray(Object data) throws JsonProcessingException, IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JsonGenerator generator = FACTORY.createGenerator(baos);) {

            if (data instanceof Map) {
                toJsonByteArray((Map) data, generator);
            } else if (data instanceof Object[]) {
                toJsonByteArray(Arrays.asList((Object[]) data), generator);
            } else if (data instanceof List) {
                toJsonByteArray((List) data, generator);
            } else if (data instanceof ObjectId) {
                toJsonByteArray((ObjectId) data, generator);
            } else if (data instanceof Document) {
                generator.writeRawValue(((Document) data).toJson());
            } else {
                generator.writeObject(data);
            }
            generator.flush();
            return baos.toByteArray();
        }
    }

    private static void toJsonByteArray(Map<String, Object> data, JsonGenerator generator) throws JsonProcessingException, IOException {
        generator.writeStartObject();
        for (String key : data.keySet()) {
            generator.writeFieldName(key);
            Object value = data.get(key);
            if (value instanceof Map) {
                toJsonByteArray((Map) value, generator);
            } else if (value instanceof Object[]) {
                toJsonByteArray(Arrays.asList((Object[]) value), generator);
            } else if (value instanceof List) {
                toJsonByteArray((List) value, generator);
            } else if (value instanceof ObjectId) {
                toJsonByteArray((ObjectId) value, generator);
            } else if (value instanceof Document) {
                generator.writeRawValue(((Document) value).toJson());
            } else {
                generator.writeRawValue(MAPPER.writeValueAsString(value));
            }
        }
        generator.writeEndObject();
    }

    private static void toJsonByteArray(List<Object> data, JsonGenerator generator) throws JsonProcessingException, IOException {
        generator.writeStartArray();
        for (Object value : data) {
            if (value instanceof Map) {
                toJsonByteArray((Map) value, generator);
            } else if (value instanceof Object[]) {
                toJsonByteArray(Arrays.asList((Object[]) value), generator);
            } else if (value instanceof List) {
                toJsonByteArray((List) value, generator);
            } else if (value instanceof ObjectId) {
                toJsonByteArray((ObjectId) value, generator);
            } else if (value instanceof Document) {
                generator.writeRawValue(((Document) value).toJson());
            } else {
                generator.writeRawValue(MAPPER.writeValueAsString(value));
            }
        }
        generator.writeEndArray();
    }

    private static void toJsonByteArray(ObjectId data, JsonGenerator generator) throws JsonProcessingException, IOException {
        generator.writeStartObject();
        generator.writeStringField("$oid", data.toString());
        generator.writeEndObject();
    }

    public static <T> T toObject(String json, Class<T> type) throws IOException {
        return MAPPER.readValue(json, type);
    }

    public static LinkedHashMap toOrderedMap(String json) throws IOException {
        return toObject(json, LinkedHashMap.class);
    }

    public static HashMap toMap(String json) throws IOException {
        return toObject(json, HashMap.class);
    }
    

    public static ArrayList<Document> toDocList(String json) {
        try {
            ArrayList<Document> jsons = MAPPER.readValue(json, new TypeReference<ArrayList<Document>>() {});
            return jsons;
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return null;
        }
    }
}
