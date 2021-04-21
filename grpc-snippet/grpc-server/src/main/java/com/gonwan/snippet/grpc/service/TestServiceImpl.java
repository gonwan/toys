package com.gonwan.snippet.grpc.service;

import com.gonwan.snippet.grpc.*;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.nio.charset.StandardCharsets;

@GrpcService
public class TestServiceImpl extends TestServiceGrpc.TestServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse response = HelloResponse.newBuilder()
                .setMessage("Hello: " + request.getName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void downloadFile(DownloadRequest request, StreamObserver<DownloadResponse> responseObserver) {
        if (!request.hasFilename()) {
            super.downloadFile(request, responseObserver);
        } else {
            switch (request.getFilename()) {
                case "a":
                    for (int i = 0; i < 30; i++) {
                        DownloadResponse response = DownloadResponse.newBuilder()
                                .setId(i)
                                .setChunk(ByteString.copyFrom("aaaaaaaa", StandardCharsets.UTF_8))
                                .build();
                        responseObserver.onNext(response);
                    }
                    responseObserver.onCompleted();
                    break;
                case "b":
                    for (int i = 0; i < 100; i++) {
                        DownloadResponse response = DownloadResponse.newBuilder()
                                .setId(i)
                                .setChunk(ByteString.copyFrom("bbbbbbbb", StandardCharsets.UTF_8))
                                .build();
                        responseObserver.onNext(response);
                    }
                    responseObserver.onCompleted();
                    break;
                default:
                    super.downloadFile(request, responseObserver);
                    break;
            }
        }
    }

}
