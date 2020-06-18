package com.gonwan.benchmark.springwebflux.controller;

import com.gonwan.benchmark.springwebflux.model.World;
import com.gonwan.benchmark.springwebflux.model.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Autowired
    private WorldRepository worldRepository;

    @RequestMapping(value = "/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public String text() {
        return "Hello, World!";
    }

    @RequestMapping("/json")
    public Map<String, String> json() {
        return Collections.singletonMap("message", "Hello, World!");
    }

    @RequestMapping("/db")
    public Mono<World> db() {
        return randomWorld();
    }

    @RequestMapping("/queries")
    public Flux<World> queries(@RequestParam String count) {
        Integer[] ids = new Integer[parseQueryCount(count)];
        Arrays.setAll(ids, i -> random());
        return worldRepository.findAllById(Arrays.asList(ids));
    }

    @RequestMapping("/updates")
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
