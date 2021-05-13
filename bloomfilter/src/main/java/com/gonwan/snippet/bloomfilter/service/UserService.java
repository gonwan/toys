package com.gonwan.snippet.bloomfilter.service;

import com.gonwan.snippet.bloomfilter.model.UserRouteInfo;
import com.gonwan.snippet.bloomfilter.util.ReactiveRedisTemplateGroup;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

import static com.googlecode.cqengine.query.QueryFactory.equal;

@SuppressWarnings("UnstableApiUsage")
@Component
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final long FILTER_SIZE = 1 * 10 * 1000;

    private static final String REDIS_KEY = "redis";

    private IndexedCollection<UserRouteInfo> userRouteInfoData;

    private BloomFilter<String> userIdFilter;

    private BloomFilter<String> userTokenFilter;

    private BloomFilter<String> mobileMd5Filter;

    private ReactiveRedisTemplateGroup reactiveRedisTemplateGroup;

    private ReactiveRedisTemplate reactiveRedisTemplate;

    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    private static final Attribute<UserRouteInfo, String> USERID_ATTRIBUTE = new SimpleAttribute<UserRouteInfo, String>("userId") {
        @Override
        public String getValue(UserRouteInfo o, QueryOptions queryOptions) {
            return o.getUserId();
        }
    };

    private static final Attribute<UserRouteInfo, String> USERTOKEN_ATTRIBUTE = new SimpleNullableAttribute<UserRouteInfo, String>("userToken") {
        @Override
        public String getValue(UserRouteInfo o, QueryOptions queryOptions) {
            return o.getUserToken();
        }
    };

    private static final Attribute<UserRouteInfo, String> MOBILE_MD5_ATTRIBUTE = new SimpleNullableAttribute<UserRouteInfo, String>("mobileMd5") {
        @Override
        public String getValue(UserRouteInfo o, QueryOptions queryOptions) {
            return o.getMobileMd5();
        }
    };

    @Autowired
    public UserService(ReactiveRedisTemplateGroup reactiveRedisTemplateGroup, ReactiveRedisTemplate reactiveRedisTemplate, ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.reactiveRedisTemplateGroup = reactiveRedisTemplateGroup;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        initLocalIndex(FILTER_SIZE);
    }

    private void initLocalIndex(long size) {
        /* index */
        IndexedCollection<UserRouteInfo> userRouteInfoData2 = new ConcurrentIndexedCollection<>();
        userRouteInfoData2.addIndex(HashIndex.onAttribute(USERID_ATTRIBUTE));
        userRouteInfoData2.addIndex(HashIndex.onAttribute(USERTOKEN_ATTRIBUTE));
        userRouteInfoData2.addIndex(HashIndex.onAttribute(MOBILE_MD5_ATTRIBUTE));
        /* filters */
        BloomFilter<String> userIdFilter2 = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), size, 0.01);
        BloomFilter<String> userTokenFilter2 = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), size, 0.01);
        BloomFilter<String> mobileMd5Filter2 = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), size, 0.01);
        /* generation */
        for (long i = 0; i < size; i++) {
            UserRouteInfo uri = new UserRouteInfo(RandomStringUtils.randomAlphanumeric(32), RandomStringUtils.randomAlphanumeric(64));
            uri.setMobileMd5(RandomStringUtils.randomAlphabetic(32));
            uri.setZoneId((int) i % 16);
            userRouteInfoData2.add(uri);
            userIdFilter2.put(uri.getUserId());
            userTokenFilter2.put(uri.getUserToken());
            mobileMd5Filter2.put(uri.getMobileMd5());
            if (i < 3) {
                logger.info("For testing: {}", uri.toString());
                reactiveRedisTemplate.opsForValue().set(REDIS_KEY, uri).block();
            }
            if (i % (100 * 1000) == 0) {
                logger.info("Running {}...", i);
            }
        }
        userRouteInfoData = userRouteInfoData2;
        userIdFilter = userIdFilter2;
        userTokenFilter = userTokenFilter2;
        mobileMd5Filter = mobileMd5Filter2;
        logger.info("Finished loading all data: size={}", userRouteInfoData.size());
    }

    public Mono<UserRouteInfo> query(String userId, boolean multi) {
        /* bloom filter */
        if (!userIdFilter.mightContain(userId) && !StringUtils.equals(userId, REDIS_KEY)) {
            return Mono.empty();
        }
        /* local cache */
        Query<UserRouteInfo> query = equal(USERID_ATTRIBUTE, userId);
        try (ResultSet<UserRouteInfo> rs = userRouteInfoData.retrieve(query)) {
            UserRouteInfo result = rs.stream().findFirst().orElse(null);
            if (result != null) {
                return Mono.just(result);
            }
        }
        /* simulate redis fetch */
        if (multi) {
            return reactiveRedisTemplateGroup.get().opsForValue().get(REDIS_KEY).cast(UserRouteInfo.class);
        } else {
            return reactiveRedisTemplate.opsForValue().get(REDIS_KEY).cast(UserRouteInfo.class);
        }
    }

    public Mono<UserRouteInfo> query2() {
        return reactiveRedisTemplate.opsForValue().get(REDIS_KEY).cast(UserRouteInfo.class);
    }

    public Mono<String> query3() {
        return reactiveStringRedisTemplate.opsForValue().get(REDIS_KEY);
    }

}
