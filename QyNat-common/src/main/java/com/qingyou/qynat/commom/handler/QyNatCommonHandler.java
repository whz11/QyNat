package com.qingyou.qynat.commom.handler;

import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author whz
 * @date 2021/7/16 00:15
 **/
public class QyNatCommonHandler extends ChannelInboundHandlerAdapter {
    protected ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception caught ...");
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                System.out.println("Read idle loss connection.");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                NatMessage natMessage = new NatMessage();
                natMessage.getMessageHeader().setNatMessageType(NatMessageType.KEEPALIVE);
                ctx.writeAndFlush(natMessage);
            }
        }
    }
}
