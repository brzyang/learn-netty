package com.brzyang.netty.im.handler;

import com.brzyang.netty.util.LoginUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class ImAuthHandler extends ChannelInboundHandlerAdapter {
    public static final ImAuthHandler INSTANCE = new ImAuthHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!LoginUtil.hasLogin(ctx.channel())) {
            ctx.channel().close();
        }else{
            // 一行代码实现逻辑的删除
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (LoginUtil.hasLogin(ctx.channel())) {
            System.out.println("当前连接登录验证完毕，无需再次验证, ImAuthHandler 被移除");
        } else {
            System.out.println("无登录验证，强制关闭连接!");
        }
        super.handlerRemoved(ctx);
    }
}
