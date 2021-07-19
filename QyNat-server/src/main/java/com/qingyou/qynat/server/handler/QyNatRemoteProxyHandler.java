package com.qingyou.qynat.server.handler;

import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.proto.NatProto;

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
    public void channelActive(ChannelHandlerContext ctx) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        NatProto.NatMessage message = NatProtoCodec.createNatMessage(0, NatProto.Type.CONNECTED, metaData, null);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        NatProto.NatMessage message = NatProtoCodec.createNatMessage(0, NatProto.Type.DISCONNECTED, metaData, null);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] data = (byte[]) msg;
        System.out.println(msg);
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        NatProto.NatMessage message = NatProtoCodec.createNatMessage(0, NatProto.Type.DATA, metaData, data);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}
