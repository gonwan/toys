package com.gonwan.benchmark.nettyproxy;

import io.netty5.channel.*;
import io.netty5.util.concurrent.Future;
import io.netty5.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyUpstreamHandler extends ChannelHandlerAdapter {

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
        clientChannel.writeAndFlush(msg).addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<? extends Void> future) throws InterruptedException {
                if (!future.isSuccess()) {
                    ctx.close();
                    clientChannel.close();
                }
            }
        });
    }

}
