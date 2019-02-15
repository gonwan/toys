package com.gonwan.snippet.json.demo.converter;

import java.io.IOException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class PropsConverter implements AttributeConverter<Props, String> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Props attribute) {
        try {
            String s = objectMapper.writeValueAsString(attribute);
            return s;
        } catch (IOException ioe) {
            return null;
        }
    }

    @Override
    public Props convertToEntityAttribute(String dbData) {
        try {
            Props p = objectMapper.readValue(dbData, Props.class);
            return p;
        } catch (IOException ioe) {
            return null;
        }
    }

}
