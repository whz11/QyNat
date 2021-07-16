package handler;

import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.ProtostuffUtil;
import com.qingyou.qynat.commom.protocol.TestMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author whz
 * @date 2021/7/16 01:03
 **/
public class QyNatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
