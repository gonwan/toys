package com.gonwan.snippet.bloomfilter;

import com.gonwan.snippet.bloomfilter.util.ReactiveRedisTemplateGroup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class BloomFilterApplication {

    @Bean
    public ReactiveRedisTemplateGroup reactiveRedisTemplateGroup(LettuceConnectionFactory factory) {
        return new ReactiveRedisTemplateGroup(factory, 4);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(LettuceConnectionFactory factory) {
        RedisSerializationContext<String, Object> context = RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(LettuceConnectionFactory factory) {
        RedisSerializationContext<String, String> context = RedisSerializationContext.<String, String>newSerializationContext(new StringRedisSerializer())
                .value(new StringRedisSerializer())
                .build();
        return new ReactiveStringRedisTemplate(factory, context);
    }

    public static void main(String[] args) {
        SpringApplication.run(BloomFilterApplication.class, args);
    }

}
