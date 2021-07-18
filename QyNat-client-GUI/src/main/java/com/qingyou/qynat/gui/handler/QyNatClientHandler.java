package com.qingyou.qynat.gui.handler;

import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatProto;
import com.qingyou.qynat.gui.Main;
import com.qingyou.qynat.gui.client.TcpConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whz
 * @date 2021/7/16 01:08
 **/
public class QyNatClientHandler extends QyNatCommonHandler {
    private final String port;
    private final String password;
    private final String proxyAddress;
    private final String serverAddress;
    private final String proxyPort;

    private  final ConcurrentHashMap<String, QyNatCommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static String channelId;

    public QyNatClientHandler(String port, String password, String proxyAddress, String serverAddress, String proxyPort) {
        this.port = port;
        this.password = password;
        this.proxyAddress = proxyAddress;
        this.serverAddress = serverAddress;
        this.proxyPort = proxyPort;
    }

    //    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//
//        // register client information
//        NatProto.NatMessage natMessage = new NatProto.NatMessage();
//        natMessage.setMessageHeader(new NatProto.NatMessageHeader(NatProto.NatMessageType.REGISTER));
//        HashMap<String, Object> metaData = new HashMap<>();
//        metaData.put("port", port);
//        metaData.put("password", password);
//        natMessage.setMetaData(metaData);
//        ctx.writeAndFlush(natMessage);
//
//        super.channelActive(ctx);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        NatProto.NatMessage natMessage = (NatProto.NatMessage) msg;
//        NatProto.NatMessageHeader header = natMessage.getMessageHeader();
//        NatProto.NatMessageType type = header.getNatProto.NatMessageType();
//        if (type == NatProto.NatMessageType.REGISTER_RESULT) {
//            processRegisterResult(natMessage);
//            System.out.println("注册返回："+natMessage);
//        } else if (type == NatProto.NatMessageType.CONNECTED) {
//            processConnected(natMessage);
//        } else if (type == NatProto.NatMessageType.DISCONNECTED) {
//            processDisconnected(natMessage);
//        } else if (type == NatProto.NatMessageType.DATA) {
//            processData(natMessage);
//        } else if (type == NatProto.NatMessageType.KEEPALIVE) {
//            // 心跳包, 不处理
//        } else {
//            throw new QyNatException("Unknown type: " + type);
//        }
//
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        channelGroup.close();
//        System.out.println("Loss connection to qynat.server, Please restart!");
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.REGISTER_RESULT
//     */
//    private void processRegisterResult(NatProto.NatMessage natMessage) {
//        if ((Boolean) natMessage.getMetaData().get("success")) {
//            System.out.println("Register to qynat.server");
//        } else {
//            System.out.println("Register fail: " + natMessage.getMetaData().get("reason"));
//            ctx.close();
//        }
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.CONNECTED
//     */
//    private void processConnected(NatProto.NatMessage natMessage) throws Exception {
//
//        try {
//            QyNatClientHandler thisHandler = this;
//            TcpConnection localConnection = new TcpConnection();
//            localConnection.connect(proxyAddress, proxyPort, new ChannelInitializer<SocketChannel>() {
//                @Override
//                public void initChannel(SocketChannel ch) throws Exception {
//                    QyNatLocalProxyHandler localProxyHandler = new QyNatLocalProxyHandler(thisHandler, natMessage.getMetaData().get("channelId").toString());
//                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), localProxyHandler);
//
//                    channelHandlerMap.put(natMessage.getMetaData().get("channelId").toString(), localProxyHandler);
//                    channelGroup.add(ch);
//                }
//            });
//        } catch (Exception e) {
//            NatProto.NatMessage message = new NatProto.NatMessage();
//            message.setMessageHeader(new NatProto.NatMessageHeader(NatProto.NatMessageType.DISCONNECTED));
//            HashMap<String, Object> metaData = new HashMap<>();
//            metaData.put("channelId", natMessage.getMetaData().get("channelId"));
//            message.setMetaData(metaData);
//            ctx.writeAndFlush(message);
//            channelHandlerMap.remove(natMessage.getMetaData().get("channelId"));
//            throw e;
//        }
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DISCONNECTED
//     */
//    private void processDisconnected(NatProto.NatMessage natMessage) {
//        String channelId = natMessage.getMetaData().get("channelId").toString();
//        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
//        if (handler != null) {
//            handler.getCtx().close();
//            channelHandlerMap.remove(channelId);
//        }
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DATA
//     */
//    private void processData(NatProto.NatMessage natMessage) {
//        String channelId = natMessage.getMetaData().get("channelId").toString();
//        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
//        if (handler != null) {
//            ChannelHandlerContext ctx = handler.getCtx();
//            ctx.writeAndFlush(natMessage.getData());
//        }
//    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // register client information
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put("port", port);
        metaData.put("password", password);
        metaData.put("addr", serverAddress);
        NatProto.NatMessage natMessage = NatProtoCodec.createNatMessage(0, NatProto.Type.REGISTER, metaData, null);
        ctx.writeAndFlush(natMessage);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NatProto.NatMessage natMessage = (NatProto.NatMessage) msg;
        NatProto.Type type = natMessage.getType();
        if (type == NatProto.Type.REGISTER_RESULT) {
            processRegisterResult(natMessage);
        } else if (type == NatProto.Type.CONNECTED) {
            processConnected(natMessage);
        } else if (type == NatProto.Type.DISCONNECTED) {
            processDisconnected(natMessage);
        } else if (type == NatProto.Type.DATA) {
            processData(natMessage);
        } else if (type == NatProto.Type.KEEPALIVE) {
            // 心跳包, 不处理
        } else {
            throw new QyNatException("Unknown type: " + type);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        Main.updateTextAreaContentStatic("Loss connection to QyNat, Please restart!");
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.REGISTER_RESULT
     */
    private void processRegisterResult(NatProto.NatMessage natMessage) {
        Map<String, String> metaData = natMessage.getMetaDataMap();
        if (Boolean.parseBoolean(metaData.get("success"))) {
            Main.updateTextAreaContentStatic("Register to qynat");
            Main.updateTextAreaContentStatic("try this address:\n" + metaData.get("mapping") + "\n" + "to proxy you address");
        } else {
            Main.updateTextAreaContentStatic("Register fail: " + natMessage.getMetaDataMap().get("reason"));
            ctx.close();
        }
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.CONNECTED
     */
    private void processConnected(NatProto.NatMessage natMessage) throws Exception {

        try {
            QyNatClientHandler thisHandler = this;
            TcpConnection localConnection = new TcpConnection();
            localConnection.connect(proxyAddress, proxyPort, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    QyNatLocalProxyHandler localProxyHandler = new QyNatLocalProxyHandler(thisHandler, natMessage.getMetaDataMap().get("channelId"));
                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), localProxyHandler);
                    channelId=natMessage.getMetaDataMap().get("channelId");
                    channelHandlerMap.put(channelId, localProxyHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            HashMap<String, String> metaData = new HashMap<>();
            metaData.put("channelId",channelId);
            NatProto.NatMessage message = NatProtoCodec.createNatMessage(0, NatProto.Type.DISCONNECTED, metaData, null);
            ctx.writeAndFlush(message);
            channelHandlerMap.remove(channelId);
            throw e;
        }
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DISCONNECTED
     */
    private void processDisconnected(NatProto.NatMessage natMessage) {
//        String channelId = natMessage.getMetaDataMap().get("channelId");
        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DATA
     */
    private void processData(NatProto.NatMessage natMessage) {
        //String channelId = natMessage.getMetaDataMap().get("channelId");
        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            byte[] data = natMessage.getData().toByteArray();
            ctx.writeAndFlush(data);
        }
    }

}
