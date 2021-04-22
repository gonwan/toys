package com.gonwan.snippet.dubbo.impl;

import com.eastmoney.snippet.dubbo.api.TestService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class TestServiceImpl implements TestService {

    @Override
    public String sayHello(String name) {
        return "Hello: " + name;
    }

}
