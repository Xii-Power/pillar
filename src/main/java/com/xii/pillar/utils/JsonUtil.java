package com.xii.pillar.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.net.URL;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String write(Object object) throws Exception {
        return mapper.writeValueAsString(object);
    }

    public static <T> T read(String content, Class<T> classType) throws Exception {
        return mapper.readValue(content, classType);
    }

    public static <T> T read(String content, TypeReference<T> typeReference) throws Exception {
        return mapper.readValue(content, typeReference);
    }

    public static void writeToFile(File dst, Object object) throws Exception {
        mapper.writeValue(dst, object);
    }

    public static JsonNode readTree(String content) throws Exception {
        return mapper.readTree(content);
    }

    public static JsonNode readTree(File file) throws Exception {
        return mapper.readTree(file);
    }

    public static JsonNode readRemote(String url) throws Exception {
        return mapper.readTree(new URL(url));
    }

    public static <T> T read(String content, TypeReference<T> typeReference, String... keys) throws Exception {
        JsonNode jsonNode = readTree(content);
        for (String key : keys) {
            if (jsonNode != null && jsonNode.has(key)) {
                jsonNode = jsonNode.get(key);
            } else {
                return null;
            }
        }

        return mapper.convertValue(jsonNode, typeReference);
    }

    public static <T> T read(File file, TypeReference<T> typeReference, String... keys) throws Exception {
        JsonNode jsonNode = mapper.readTree(file);
        for (String key : keys) {
            if (jsonNode != null && jsonNode.has(key)) {
                jsonNode = jsonNode.get(key);
            } else {
                return null;
            }
        }

        return mapper.convertValue(jsonNode, typeReference);
    }

    public static ObjectNode toObjectNode(Object object) throws Exception {
        return mapper.valueToTree(object);
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }

    public static String getString(ObjectNode objectNode, String key, String defaultValue) {
        return objectNode.has(key) ? objectNode.get(key).asText() : defaultValue;
    }

    public static Integer getInt(ObjectNode objectNode, String key, Integer defaultValue) {
        return objectNode.has(key) ? objectNode.get(key).asInt() : defaultValue;
    }

    public static Long getLong(ObjectNode node, String key, Long defaultValue) {
        return node.has(key) ? node.get(key).asLong() : defaultValue;
    }
}
