package com.gonwan.snippet.grpc;

import com.gonwan.snippet.grpc.service.ClientService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GrpcClientApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(GrpcClientApplication.class, args);
        ClientService clientService = applicationContext.getBean(ClientService.class);
        System.out.println(clientService.receiveGreeting("World"));
    }

}
