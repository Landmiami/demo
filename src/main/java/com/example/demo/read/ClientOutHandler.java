package com.example.demo.read;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;

public class ClientOutHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("=========ClientOutHandler.write "+ in.toString(CharsetUtil.UTF_8));
/*        ctx.writeAndFlush(Unpooled.copiedBuffer(in,
                Unpooled.copiedBuffer("ClientOutHandler", CharsetUtil.UTF_8)));*/
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
