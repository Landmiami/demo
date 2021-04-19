package com.example.demo.read;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class FirstInServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("==========Server console: FirstInServerHandler.channelRead " + in.toString(CharsetUtil.UTF_8));
        ctx.fireChannelRead(Unpooled.copiedBuffer("FirstInServerHandler -> ",
                CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==========Server console: FirstInServerHandler.channelReadComplete ");
        super.channelReadComplete(ctx);
    }
}

