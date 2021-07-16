package handler;

import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.ProtostuffUtil;
import com.qingyou.qynat.commom.protocol.TestMsg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whz
 * @date 2021/7/16 01:08
 **/
public class QyNatClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("MyClientHandler.channelActive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("read Message:"+msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
