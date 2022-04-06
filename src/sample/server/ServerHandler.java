package sample.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import sample.helpers.*;
import sample.helpers.classes.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception " + cause.toString());

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected ");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageHelp messageHelp = (MessageHelp) msg;
        Map<MessageType, MessageFunctions> messageFunctionsMap = new MessageMap().getMessageMap();
        messageFunctionsMap.get(messageHelp.getType()).accept(ctx, msg);
    }


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
