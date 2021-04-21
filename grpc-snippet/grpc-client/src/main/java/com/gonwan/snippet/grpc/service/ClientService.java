package com.gonwan.snippet.grpc.service;

import com.gonwan.snippet.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Service
public class ClientService {

    @GrpcClient("testService")
    private TestServiceGrpc.TestServiceBlockingStub testServiceStub;

    public void sayHello(String name) {
        System.out.printf("=== Saying hello: %s ===\n", name);
        HelloRequest request = HelloRequest.newBuilder()
                .setName(name)
                .build();
        HelloResponse response = testServiceStub.sayHello(request);
        System.out.println(response.getMessage());
    }

    public void downloadFile(String filename) {
        System.out.printf("=== Downloading file: %s ===\n", filename);
        DownloadRequest request = DownloadRequest.newBuilder()
                .setFilename(filename)
                .build();
        for (Iterator<DownloadResponse> it = testServiceStub.downloadFile(request); it.hasNext(); ) {
            DownloadResponse response = it.next();
            System.out.printf("%02d: %s\n", response.getId(), response.getChunk().toString(StandardCharsets.UTF_8));
        }
    }

}
