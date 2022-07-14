package com.gonwan.toys.consistentcache.controller;

import com.gonwan.toys.consistentcache.service.Cache2Service;
import com.gonwan.toys.consistentcache.service.CacheService;
import com.gonwan.toys.consistentcache.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("/api")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private Cache2Service cache2Service;

    @PostMapping("/setUser")
    public void setUser(@RequestBody UserVO userVO) {
        cacheService.setUser(userVO);
    }

    @GetMapping("/getUser")
    public UserVO getUser(@RequestParam("id") Integer id) {
        return cacheService.getUser(id);
    }

    @PostMapping("/setUser2")
    public void setUser2(@RequestBody UserVO userVO) {
        cache2Service.setUser(userVO);
    }

    @GetMapping("/getUser2")
    public UserVO getUser2(@RequestParam("id") Integer id) {
        return cache2Service.getUser(id);
    }

}
