package com.gonwan.snippet.bloomfilter.util;

import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reactive lettuce implementation uses single thread, that is not always the fastest.
 * - The pool implementation is not suitable, since close() is async and caused pool exhausted.
 * - The default lettuce client also uses one-thread for one-connection, currently not knowing how to customize it.
 * Here the class simply implement a fixed size connection of redis clients.
 */
public class ReactiveRedisTemplateGroup {

    /* multi-threading is faster, but pooling support in spring boot seems to be problematic. */
    private List<ReactiveRedisTemplate> reactiveRedisTemplateList = new ArrayList<>(4);

    private AtomicInteger index = new AtomicInteger(0);

    public ReactiveRedisTemplateGroup(LettuceConnectionFactory factory, int threads) {
        for (int i = 0; i < threads; i++) {
            LettuceConnectionFactory factory2 = new LettuceConnectionFactory();
            BeanUtils.copyProperties(factory, factory2);
            factory2.afterPropertiesSet();
            RedisSerializationContext<String, Object> context = RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                    .value(new GenericJackson2JsonRedisSerializer())
                    .build();
            ReactiveRedisTemplate<String, Object> reactiveRedisTemplate = new ReactiveRedisTemplate<>(factory2, context);
            reactiveRedisTemplateList.add(reactiveRedisTemplate);
        }
    }

    public ReactiveRedisTemplate get() {
        int i = Math.abs(index.getAndIncrement() % reactiveRedisTemplateList.size());
        return reactiveRedisTemplateList.get(i);
    }

    public ReactiveRedisTemplate get(int i) {
        return reactiveRedisTemplateList.get(i);
    }

}
