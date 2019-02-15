package com.gonwan.snippet.json.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gonwan.snippet.json.demo.model.TTest;
import com.gonwan.snippet.json.demo.model.TTest2;
import com.gonwan.snippet.json.demo.model.TTestRepository;
import com.gonwan.snippet.json.demo.model.TTestRepository2;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TTestRepository testRepository;

    @Autowired
    private TTestRepository2 testRepository2;

    @GetMapping("/1")
    public List<TTest> get() {
        return testRepository.findAll();
    }

    @GetMapping("/2")
    public List<TTest2> get2() {
        return testRepository2.findAll();
    }

}
