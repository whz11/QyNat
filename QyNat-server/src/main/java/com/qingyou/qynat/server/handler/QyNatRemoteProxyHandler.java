package com.qingyou.qynat.server.handler;

import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.NatMessageType;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * @author whz
 * @date 2021/7/16 12:47
 **/
public class QyNatRemoteProxyHandler extends QyNatCommonHandler {

    private QyNatCommonHandler proxyHandler;

    public QyNatRemoteProxyHandler(QyNatCommonHandler proxyHandler) {
        this.proxyHandler = proxyHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NatMessage message = new NatMessage();
        message.setMessageHeader(new NatMessageHeader(NatMessageType.CONNECTED));
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NatMessage message = new NatMessage();
        message.setMessageHeader(new NatMessageHeader(NatMessageType.DISCONNECTED));
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        NatMessage message = new NatMessage();
        message.setMessageHeader(new NatMessageHeader(NatMessageType.DATA));
        message.setData(data);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}
