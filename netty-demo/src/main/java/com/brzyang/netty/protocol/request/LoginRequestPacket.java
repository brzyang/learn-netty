package com.brzyang.netty.protocol.request;

import com.brzyang.netty.protocol.Packet;
import com.brzyang.netty.protocol.command.Command;

public class LoginRequestPacket extends Packet {

    private String userId;
    private String username;
    private String password;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST;
    }
}
