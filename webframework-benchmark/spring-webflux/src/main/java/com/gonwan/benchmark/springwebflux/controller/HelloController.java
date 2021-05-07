package com.gonwan.benchmark.springwebflux.controller;

import com.gonwan.benchmark.springwebflux.model.World;
import com.gonwan.benchmark.springwebflux.model.WorldRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class HelloController {

    private static String TEXT100 = RandomStringUtils.random(100);

    @Autowired
    private WorldRepository worldRepository;

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
    public Mono<World> db() {
        return randomWorld();
    }

    @GetMapping("/queries")
    public Flux<World> queries(@RequestParam String count) {
        Integer[] ids = new Integer[parseQueryCount(count)];
        Arrays.setAll(ids, i -> random());
        return worldRepository.findAllById(Arrays.asList(ids));
    }

    @GetMapping("/updates")
    public Flux<World> updates(@RequestParam String count) {
        Flux<World> worlds = this.queries(count)
                .map(x -> {
                    x.setRandomNumber(random());
                    x.setRandomText("哈哈哈哈");
                    x.setUpdateTime(LocalDateTime.now());
                    return x;
                });
        return worldRepository.saveAll(worlds);
    }

    private Mono<World> randomWorld() {
        /* returns Mono.empty() if none found */
        return worldRepository.findById(random());
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
