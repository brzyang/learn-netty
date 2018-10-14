package com.brzyang.netty.one2one;

import com.brzyang.netty.base.BaseNettyClient;
import com.brzyang.netty.protocol.PacketCodec;
import com.brzyang.netty.protocol.request.MessageRequestPacket;
import com.brzyang.netty.util.LoginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ProtocolOne2OneNettyClient extends BaseNettyClient {

    private final static int MAX_RETRY = 10;
    public static void main(String[] args) throws InterruptedException {
//        ch.pipeline().addLast(new StringEncoder());
//                        ch.pipeline().addLast(new FrameClientHandler());

        List<ChannelHandler> channels = Collections.singletonList(new ProtocolClientHandler());
        Channel channel = initClient(channels);
        // 连接成功之后，启动控制台线程
        startProtocolConsoleThread(channel);
    }


    protected static void startProtocolConsoleThread(Channel channel) {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (LoginUtil.hasLogin(channel)) {
                    System.out.println("输入消息发送至服务端: ");
                    Scanner sc = new Scanner(System.in);
                    String line = sc.nextLine();

                    MessageRequestPacket packet = new MessageRequestPacket();
                    packet.setMessage(line);
                    ByteBuf byteBuf = PacketCodec.INSTANCE.encode(channel.alloc(), packet);
                    channel.writeAndFlush(byteBuf);
                }
            }
        }).start();
    }
}