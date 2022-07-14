package com.gonwan.toys.consistentcache;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class ConsistentCacheApplication {

    private static final Logger logger = LoggerFactory.getLogger(ConsistentCacheApplication.class);

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addHandler(new DeserializationProblemHandler() {
                    /* to work with @JsonFormat */
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
                        logger.warn("Failed to parse {}={}: {}", ctxt.getParser().getCurrentName(), valueToConvert, failureMsg);
                        return null;
                    }
                })
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsistentCacheApplication.class, args);
    }

}
