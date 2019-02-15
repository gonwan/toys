package com.gonwan.snippet.json.demo.converter;

import java.io.IOException;
import java.util.HashMap;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class JsonToMapConverter implements AttributeConverter<HashMap<String, Object>, String> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(HashMap<String, Object> attribute) {
        try {
            String s = objectMapper.writeValueAsString(attribute);
            return s;
        } catch (IOException ioe) {
            return null;
        }
    }

    @Override
    public HashMap<String, Object> convertToEntityAttribute(String dbData) {
        try {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> m = objectMapper.readValue(dbData, HashMap.class);
            return m;
        } catch (IOException ioe) {
            return null;
        }
    }

}
