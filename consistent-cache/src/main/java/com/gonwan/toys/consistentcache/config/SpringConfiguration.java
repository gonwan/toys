package com.gonwan.toys.consistentcache.config;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.gonwan.toys.consistentcache.ConsistentCacheApplication;
import com.gonwan.toys.consistentcache.model.UserDO;
import com.gonwan.toys.consistentcache.repository.UserRepository;
import io.github.dtm.cache.Cache;
import io.github.dtm.cache.CacheClient;
import io.github.dtm.cache.spring.RocksCacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

@Configuration
@Import(RocksCacheConfiguration.class) /* support spring boot 2.6 */
public class SpringConfiguration {

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

    @Bean("userCache")
    public Cache<Long, UserDO> userCache(CacheClient cacheClient, UserRepository userRepository) {
        return cacheClient
                .newCacheBuilder(
                        "cachetest2:u:",
                        Long.class,
                        UserDO.class
                )
                .setJavaLoader(
                        UserDO::getId,
                        userRepository::findAllById
                )
                .build();
    }

}
