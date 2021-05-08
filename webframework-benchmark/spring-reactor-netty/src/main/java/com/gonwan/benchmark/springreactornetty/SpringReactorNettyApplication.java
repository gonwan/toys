package com.gonwan.benchmark.springreactornetty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpServer;

public class SpringReactorNettyApplication {

    public static ByteBuf DELIMITER = Unpooled.wrappedBuffer(new byte[] { '\r', '\n', '\r', '\n' });

    private static void runTcpServer() {
        System.out.println("Starting Tcp Server...");
        DisposableServer server = TcpServer.create()
                .port(8124)
                .handle((inbound, outbound) -> {
                    return inbound.receive().flatMap(x -> {
                        NettyOutbound ob = outbound;
                        int de;
                        while ((de = ByteBufUtil.indexOf(DELIMITER, x)) != -1) {
                            x.readerIndex(de + 4);
                            /* seems to be faster, especially in vm. */
                            Flux<String> fs = Flux.just("HTTP/1.1 200 OK\r\nServer: netty\r\nContent-Type: text/plain\r\nDate: ",
                                    "Mon, 01 Jan 2021 15:04:05 GMT",
                                    "\r\nContent-Length: 13\r\n\r\nHello, World!");
                            ob =  ob.sendString(fs);
/*
                            ob =  ob.sendString(Mono.just("HTTP/1.1 200 OK\r\nServer: netty\r\nContent-Type: text/plain\r\nDate: "))
                                    .sendString(Mono.just("Mon, 01 Jan 2021 15:04:05 GMT"))
                                    .sendString(Mono.just("\r\nContent-Length: 13\r\n\r\nHello, World!"));
*/
                        }
                        return ob;
                    });
                })
                .bindNow();
        server.onDispose().block();
    }

    private static void runHttpServer() {
        System.out.println("Starting Http Server...");
        DisposableServer server = HttpServer.create()
                .port(8124)
                .handle((request, response) -> response.sendString(Mono.just("Hello, World!")))
                .bindNow();
        server.onDispose().block();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runTcpServer();
        } else {
            runHttpServer();
        }
    }

}
