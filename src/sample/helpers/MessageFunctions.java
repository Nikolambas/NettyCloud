package sample.helpers;

import io.netty.channel.ChannelHandlerContext;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface MessageFunctions <ChannelHandlerContext,MessageHelp> {
    void accept(io.netty.channel.ChannelHandlerContext chx, MessageHelp messageHelp);
}
