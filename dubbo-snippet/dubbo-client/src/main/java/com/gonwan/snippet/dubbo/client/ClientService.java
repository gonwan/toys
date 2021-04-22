package com.gonwan.snippet.dubbo.client;

import com.eastmoney.snippet.dubbo.api.TestService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @DubboReference(version = "1.0.0")
    private TestService TestService;

    public void sayHello(String name) {
        System.out.printf("=== Saying Hello: %s ===\n", name);
        System.out.println(TestService.sayHello(name));
    }

}
