package com.gonwan.benchmark.nettyproxy;

import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.*;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.handler.flush.FlushConsolidationHandler;
import io.netty5.util.AttributeKey;
import io.netty5.util.concurrent.Future;
import io.netty5.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class ProxyServerHandler extends ChannelHandlerAdapter {

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
        bootstrap.group(ctx.channel().executor())
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
        Future<Channel> channelFuture = bootstrap.connect(NettyProxy.UPSTREAM.get(idx).getT1(), NettyProxy.UPSTREAM.get(idx).getT2());
        channelFuture.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<? extends Channel> future) throws Exception {
                if (future.isSuccess()) {
                    ctx.channel().attr(UPSTREAM_KEY).set(channelFuture.getNow());
                    /* start to read */
                    ctx.read();
                    ctx.channel().setOption(ChannelOption.AUTO_READ, true);
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
        upstreamChannel.writeAndFlush(msg).addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<? extends Void> future) throws InterruptedException {
                if (!future.isSuccess()) {
                    ctx.close();
                    upstreamChannel.close();
                }
            }
        });
    }

    @Override
    public void channelInboundEvent(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.channelInboundEvent(ctx, evt);
    }

    @Override
    public void channelExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.channelExceptionCaught(ctx, cause);
    }

    @Override
    public boolean isSharable() {
        return true;
    }

}
