package com.gonwan.benchmark.nettyproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;

public class NettyProxy {

    private static final Logger logger = LoggerFactory.getLogger(NettyProxy.class);

    public static final int PORT = 8091;

    public static final List<Tuple2<String, Integer>> UPSTREAM  = Arrays.asList(
            //Tuples.of("www.baidu.com", 80)
            Tuples.of("10.188.10.75", 8099),
            Tuples.of("10.188.10.76", 8099),
            Tuples.of("10.188.10.78", 8099)
    );

    private static void start() {
        EventLoopGroup bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 3000)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline()
                                    //.addLast(new IdleStateHandler(60, 0, 0))
                                    .addLast(new ProxyServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind("0.0.0.0", PORT).sync();
            if (future.isSuccess()) {
                logger.info("TCP server started on port :{}", PORT);
            }
            future.channel().closeFuture().sync();
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
