package com.gonwan.benchmark.nettyproxy;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyUpstreamHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ProxyUpstreamHandler.class);

    private Channel clientChannel;

    public ProxyUpstreamHandler(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        clientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
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
