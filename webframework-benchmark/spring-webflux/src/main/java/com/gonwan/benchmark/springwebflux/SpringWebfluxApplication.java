package com.gonwan.benchmark.springwebflux;

import io.netty.channel.ChannelOption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.netty.resources.LoopResources;

@EnableR2dbcRepositories
@SpringBootApplication
public class SpringWebfluxApplication {

    /*
     * https://www.baeldung.com/spring-boot-reactor-netty
     */
    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return server ->
                server.tcpConfiguration(tcpServer ->
                        tcpServer.runOn(LoopResources.create("reactor-netty", 1, LoopResources.DEFAULT_IO_WORKER_COUNT, true))
                                .selectorOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                );
    }

    /*
     * The default implementation of ReactorResourceFactory create a HttpResources, and start the server, override it.
     */
    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
        webServerFactory.addServerCustomizers(nettyServerCustomizer());
        return webServerFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxApplication.class, args);
    }

}
