package com.gonwan.snippet.bloomfilter;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class BloomFilterApplication {

    //@Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(LettuceConnectionFactory factory) {
        RedisSerializationContext<String, Object> context = RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    //@Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate2(LettuceConnectionFactory factory) {
        LettuceConnectionFactory factory2 = new LettuceConnectionFactory();
        BeanUtils.copyProperties(factory, factory2);
        factory2.afterPropertiesSet();
        RedisSerializationContext<String, Object> context = RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory2, context);
    }

    public static void main(String[] args) {
        SpringApplication.run(BloomFilterApplication.class, args);
    }

}
