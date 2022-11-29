package com.gonwan.benchmark.nettyproxy;

import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.*;
import io.netty5.channel.epoll.Epoll;
import io.netty5.channel.epoll.EpollHandler;
import io.netty5.channel.epoll.EpollServerSocketChannel;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.channel.socket.nio.NioServerSocketChannel;
import io.netty5.handler.flush.FlushConsolidationHandler;
import io.netty5.util.concurrent.FutureCompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;

public class NettyProxy {

    private static final Logger logger = LoggerFactory.getLogger(NettyProxy.class);

    public static final int PORT = 8091;

    public static final List<Tuple2<String, Integer>> UPSTREAM = Arrays.asList(
            //Tuples.of("www.baidu.com", 80)
            Tuples.of("10.188.10.75", 8099),
            Tuples.of("10.188.10.76", 8099),
            Tuples.of("10.188.10.78", 8099)
    );

    private static void start() {
        EventLoopGroup bossGroup = new MultithreadEventLoopGroup(Epoll.isAvailable() ? EpollHandler.newFactory() : NioHandler.newFactory());
        EventLoopGroup workerGroup = new MultithreadEventLoopGroup(Epoll.isAvailable() ? EpollHandler.newFactory() : NioHandler.newFactory());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 10000)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(4096, 16384))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    //.addLast(new FlushConsolidationHandler(256, true))
                                    .addLast(ProxyServerHandler.INSTANCE);
                        }
                    });
            FutureCompletionStage<Channel> future = bootstrap.bind("0.0.0.0", PORT).asStage().sync();
            if (future.isSuccess()) {
                logger.info("TCP server started on port :{}", PORT);
            }
            future.get().closeFuture().asStage().sync();
        } catch (InterruptedException e) {
            logger.warn("", e);
            System.exit(-1);
        } catch (Exception e) {
            logger.warn("Native error", e);  /* address already in use */
            System.exit(-1);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        start();
    }

}
