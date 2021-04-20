package com.gonwan.snippet.grpc.service;

import com.gonwan.snippet.grpc.HelloReply;
import com.gonwan.snippet.grpc.HelloRequest;
import com.gonwan.snippet.grpc.TestServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class TestServiceImpl extends TestServiceGrpc.TestServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder()
                .setMessage("Hello ==> " + request.getName())
                .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
