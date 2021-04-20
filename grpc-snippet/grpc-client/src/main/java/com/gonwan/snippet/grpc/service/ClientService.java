package com.gonwan.snippet.grpc.service;

import com.gonwan.snippet.grpc.HelloRequest;
import com.gonwan.snippet.grpc.TestServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @GrpcClient("testService")
    private TestServiceGrpc.TestServiceBlockingStub testServiceStub;

    public String receiveGreeting(String name) {
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        return testServiceStub.sayHello(request).getMessage();
    }

}
