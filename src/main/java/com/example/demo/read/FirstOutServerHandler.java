package com.example.demo.read;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;

public class FirstOutServerHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        System.out.println("==========Server console: FirstOutServerHandler.read ");
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("==========Server console: FirstOutServerHandler.write " + in.toString(CharsetUtil.UTF_8));
/*        ctx.writeAndFlush(Unpooled.copiedBuffer(in,
                Unpooled.copiedBuffer("FirstOutServerHandler", CharsetUtil.UTF_8)))
                .addListener(ChannelFutureListener.CLOSE);*/
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
