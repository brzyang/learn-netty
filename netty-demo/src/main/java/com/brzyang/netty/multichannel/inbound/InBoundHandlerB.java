package com.brzyang.netty.multichannel.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InBoundHandlerB extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(this.getClass().getCanonicalName() + msg);
        super.channelRead(ctx, msg);

        ByteBuf byteBuf = ctx.alloc().buffer().writeInt(1);
        ctx.channel().writeAndFlush(byteBuf);
    }

}
