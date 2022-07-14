package com.gonwan.toys.consistentcache.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static String obj2Str(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> T str2Obj(ObjectMapper objectMapper, String content, Class<T> clazz) {
        if (content == null || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) content : objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
