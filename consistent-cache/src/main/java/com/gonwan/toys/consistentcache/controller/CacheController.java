package com.gonwan.toys.consistentcache.controller;

import com.gonwan.toys.consistentcache.service.Cache2Service;
import com.gonwan.toys.consistentcache.service.CacheService;
import com.gonwan.toys.consistentcache.model.UserDO;
import io.github.dtm.cache.Consistency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/api")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private Cache2Service cache2Service;

    @GetMapping("/getUser")
    public UserDO getUser(@RequestParam("id") Long id) {
        return cacheService.getUser(id);
    }

    @PostMapping("/setUser")
    public void setUser(@RequestBody UserDO userDO) {
        cacheService.setUser(userDO);
    }

    @GetMapping("/findUser")
    public UserDO findUser(@RequestParam("id") Long id,
                           @RequestParam(value = "consistency", defaultValue = "EVENTUAL") Consistency consistency) {
        return cache2Service.findUser(id, consistency);
    }

    @PostMapping("/saveUser")
    public void saveUser(@RequestBody UserDO userDO) {
        cache2Service.saveUser(userDO);
    }

    @GetMapping("/deleteUser")
    public void deleteUser(@RequestParam("id") Long id) {
        cache2Service.deleteUser(id);
    }

}
