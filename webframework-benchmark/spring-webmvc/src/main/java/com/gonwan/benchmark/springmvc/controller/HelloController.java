package com.gonwan.benchmark.springmvc.controller;

import com.gonwan.benchmark.springmvc.model.World;
import com.gonwan.benchmark.springmvc.model.WorldRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public final class HelloController {

    private static final String TEXT100 = RandomStringUtils.randomAlphabetic(100);

    private static final String REDIS_KEY = "redis";

    @Autowired
    private WorldRepository worldRepository;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public HelloController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        stringRedisTemplate.opsForValue().set(REDIS_KEY, TEXT100);
    }

    @GetMapping(value = "/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public String text() {
        return "Hello, World!";
    }

    @GetMapping(value = "/text100", produces = MediaType.TEXT_PLAIN_VALUE)
    public String text100() {
        return TEXT100;
    }

    @GetMapping("/json")
    public Map<String, String> json() {
        return Collections.singletonMap("message", "Hello, World!");
    }

    @GetMapping("/db")
    public World db() {
        return randomWorld();
    }

    @GetMapping("/queries")
    public Iterable<World> queries(@RequestParam String count) {
        Integer[] ids = new Integer[parseQueryCount(count)];
        Arrays.setAll(ids, i -> random());
        return worldRepository.findAllById(Arrays.asList(ids));
    }

    @GetMapping("/updates")
    public Iterable<World> updates(@RequestParam String count) {
        Iterable<World> worlds = this.queries(count);
        for (World w : worlds) {
            w.setRandomNumber(random());
            w.setRandomText("哈哈哈");
            w.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        }
        return worldRepository.saveAll(worlds);
    }

    @GetMapping("/redis")
    public String redis() {
        return stringRedisTemplate.opsForValue().get(REDIS_KEY);
    }

    private World randomWorld() {
        return worldRepository.findById(random()).orElse(null);
    }

    private static int random() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }

    private static int parseQueryCount(String count) {
        if (count == null) {
            return 1;
        }
        int parsedCount = 1;
        try {
            parsedCount = Integer.parseInt(count);
        } catch (NumberFormatException e) {
            /* ignore */
        }
        return Math.min(500, Math.max(1, parsedCount));
    }

}
