package com.qingyou.qynat.server.handler;

import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatProto;

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

    //    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        NatProto.NatMessage message = new NatProto.NatMessage();
//        message.setMessageHeader(new NatProto.NatMessageHeader(NatProto.NatMessageType.CONNECTED));
//        HashMap<String, Object> metaData = new HashMap<>();
//        metaData.put("channelId", ctx.channel().id().asLongText());
//        message.setMetaData(metaData);
//        proxyHandler.getCtx().writeAndFlush(message);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        NatProto.NatMessage message = new NatProto.NatMessage();
//        message.setMessageHeader(new NatProto.NatMessageHeader(NatProto.NatMessageType.DISCONNECTED));
//        HashMap<String, Object> metaData = new HashMap<>();
//        metaData.put("channelId", ctx.channel().id().asLongText());
//        message.setMetaData(metaData);
//        proxyHandler.getCtx().writeAndFlush(message);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        byte[] data = (byte[]) msg;
//        NatProto.NatMessage message = new NatProto.NatMessage();
//        message.setMessageHeader(new NatProto.NatMessageHeader(NatProto.NatMessageType.DATA));
//        message.setData(data);
//        HashMap<String, Object> metaData = new HashMap<>();
//        metaData.put("channelId", ctx.channel().id().asLongText());
//        message.setMetaData(metaData);
//        proxyHandler.getCtx().writeAndFlush(message);
//    }
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
