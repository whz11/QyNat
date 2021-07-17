package com.qingyou.qynat.client.handler;

import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.NatMessageType;
import com.qingyou.qynat.commom.protocol.NatProto;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * @author whz
 * @date 2021/7/16 13:14
 **/
public class QyNatLocalProxyHandler extends QyNatCommonHandler {

    private QyNatCommonHandler proxyHandler;
    private String remoteChannelId;

    public QyNatLocalProxyHandler(QyNatCommonHandler proxyHandler, String remoteChannelId) {
        this.proxyHandler = proxyHandler;
        this.remoteChannelId = remoteChannelId;
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        byte[] data = (byte[]) msg;
//        NatMessage message = new NatMessage();
//        message.setMessageHeader(new NatMessageHeader(NatMessageType.DATA));
//        message.setData(data);
//        HashMap<String, Object> metaData = new HashMap<>();
//        metaData.put("channelId", remoteChannelId);
//        message.setMetaData(metaData);
//        proxyHandler.getCtx().writeAndFlush(message);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        NatMessage message = new NatMessage();
//        message.setMessageHeader(new NatMessageHeader(NatMessageType.DISCONNECTED));
//        HashMap<String, Object> metaData = new HashMap<>();
//        metaData.put("channelId", remoteChannelId);
//        message.setMetaData(metaData);
//        proxyHandler.getCtx().writeAndFlush(message);
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("channelId", remoteChannelId);
        NatProto.NatMessage message = NatProtoCodec.createNatMessage(0, NatProto.Type.DATA, metaData, data);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("channelId", remoteChannelId);
        NatProto.NatMessage message = NatProtoCodec.createNatMessage(0, NatProto.Type.DISCONNECTED, metaData, null);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}
