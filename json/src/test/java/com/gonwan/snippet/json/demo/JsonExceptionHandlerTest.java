package com.gonwan.snippet.json.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class MyObject {

    private String username;

    @JsonDeserialize(using = MyDateDeserializer.class)
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}

class MyDateDeserializer extends DateDeserializers.DateDeserializer {

    private static final List<String> PATTERNS = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    );

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String str = p.getText().trim();
            if (str.length() == 0) {
                return (Date) getEmptyValue(ctxt);
            }
            for (String pattern : PATTERNS) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    sdf.setLenient(false);
                    return sdf.parse(str);
                } catch (ParseException e) {
                    /* ignore */
                }
            }
            return (java.util.Date) ctxt.handleWeirdStringValue(handledType(), str,
                            "expected format \"%s\"", PATTERNS.toString());
        }
        return super.deserialize(p, ctxt);
    }

}

public class JsonExceptionHandlerTest {

    @Test
    public void test() throws IOException {
        String s = "{ \"username\": \"aaa\", \"createTime\": \"2020-12-30\", \"updateTime\": \"2020-12-20 12:30:40\" }";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.addHandler(new DeserializationProblemHandler() {
            /* to work with @JsonFormat */
            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
                System.out.printf("%s: %s\n", valueToConvert, failureMsg);
                return null;
            }
        });
        MyObject mo = objectMapper.readValue(s, MyObject.class);
        System.out.println(mo.getCreateTime());
        System.out.println(mo.getUpdateTime());
    }

}
