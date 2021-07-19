package com.qingyou.qynat.gui.handler;

import com.qingyou.qynat.client.client.TcpConnection;
import com.qingyou.qynat.client.handler.QyNatLocalProxyHandler;
import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.proto.NatProto;
import com.qingyou.qynat.gui.Main;
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

    private final ConcurrentHashMap<String, QyNatCommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static String channelId;

    public QyNatClientHandler(String port, String password, String proxyAddress, String serverAddress, String proxyPort) {
        this.port = port;
        this.password = password;
        this.proxyAddress = proxyAddress;
        this.serverAddress = serverAddress;
        this.proxyPort = proxyPort;
    }

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
            Main.updateTextAreaContentStatic("如果代理失败，确保proxy端口已打开");
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
                    channelId = natMessage.getMetaDataMap().get("channelId");
                    channelHandlerMap.put(channelId, localProxyHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            HashMap<String, String> metaData = new HashMap<>();
            metaData.put("channelId", channelId);
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
        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            byte[] data = natMessage.getData().toByteArray();
            ctx.writeAndFlush(data);
        }
    }

}
