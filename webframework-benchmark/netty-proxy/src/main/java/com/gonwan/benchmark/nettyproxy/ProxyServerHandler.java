package com.gonwan.benchmark.nettyproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ProxyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ProxyServerHandler.class);

    private static final AttributeKey<Channel> UPSTREAM_KEY = AttributeKey.valueOf("upstream");

    public static final ProxyServerHandler INSTANCE = new ProxyServerHandler();

    private AtomicInteger index = new AtomicInteger(0);

    public ProxyServerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(4096, 16384))
                //.option(ChannelOption.AUTO_READ, false)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                //.addLast(new FlushConsolidationHandler(256, true))
                                .addLast(new ProxyUpstreamHandler(clientChannel));
                    }
                });
        int idx = (Math.abs(index.getAndIncrement())) % (NettyProxy.UPSTREAM.size()); /* index wraps */
        ChannelFuture channelFuture = bootstrap.connect(NettyProxy.UPSTREAM.get(idx).getT1(), NettyProxy.UPSTREAM.get(idx).getT2());
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ctx.channel().attr(UPSTREAM_KEY).set(channelFuture.channel());
                    /* start to read */
                    ctx.read();
                    ctx.channel().config().setOption(ChannelOption.AUTO_READ, true);
                } else {
                    ctx.close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        Channel upstreamChannel = ctx.channel().attr(UPSTREAM_KEY).get();
        if (upstreamChannel != null) {
            upstreamChannel.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel upstreamChannel = ctx.channel().attr(UPSTREAM_KEY).get();
        upstreamChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws InterruptedException {
                if (!future.isSuccess()) {
                    ctx.close();
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
