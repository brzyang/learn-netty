package com.brzyang.netty.im.client;

import com.brzyang.netty.base.BaseNettyClient;
import com.brzyang.netty.chainedhandler.PacketDecoder;
import com.brzyang.netty.chainedhandler.PacketEncoder;
import com.brzyang.netty.im.handler.ImLoginResponseHandler;
import com.brzyang.netty.im.handler.ImMessageResponseHandler;
import com.brzyang.netty.im.handler.Splitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

public class ImNettyClient extends BaseNettyClient {
    public static void main(String[] args) {
        List<ChannelHandler> channels = new ArrayList<>();
        channels.add(new Splitter());
        channels.add(new PacketDecoder());
        channels.add(new ImLoginResponseHandler());
        channels.add(new ImMessageResponseHandler());
        channels.add(new PacketEncoder());
        Channel channel = initClient(channels);
        startProtocolConsoleThread(channel);
    }
}
