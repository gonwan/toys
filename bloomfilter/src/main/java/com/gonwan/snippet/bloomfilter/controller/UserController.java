package com.gonwan.snippet.bloomfilter.controller;

import com.gonwan.snippet.bloomfilter.model.UserRouteInfo;
import com.gonwan.snippet.bloomfilter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/query")
    public Mono<UserRouteInfo> query(@RequestParam("userId") String userId, @RequestParam(value = "multi", defaultValue = "false") boolean multi) {
        return userService.query(userId, multi);
    }

}
