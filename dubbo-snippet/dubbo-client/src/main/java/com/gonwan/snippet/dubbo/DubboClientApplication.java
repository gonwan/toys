package com.gonwan.snippet.dubbo;

import com.gonwan.snippet.dubbo.client.ClientService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableDubbo
@SpringBootApplication
public class DubboClientApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(DubboClientApplication.class, args);
        ClientService clientService = applicationContext.getBean(ClientService.class);
        clientService.sayHello("World");
    }

}
