package com.qingyou.qynat.client.handler;

import com.qingyou.qynat.client.client.TcpConnection;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.NatMessageType;
import com.qingyou.qynat.commom.protocol.ProtostuffUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whz
 * @date 2021/7/16 01:08
 **/
public class QyNatClientHandler extends QyNatCommonHandler {
    private int port;
    private String password;
    private String proxyAddress;
    private int proxyPort;

    private ConcurrentHashMap<String, QyNatCommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public QyNatClientHandler(int port, String password, String proxyAddress, int proxyPort) {
        this.port = port;
        this.password = password;
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // register client information
        NatMessage natMessage = new NatMessage();
        natMessage.setMessageHeader(new NatMessageHeader(NatMessageType.REGISTER));
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("port", port);
        metaData.put("password", password);
        natMessage.setMetaData(metaData);
        ctx.writeAndFlush(natMessage);

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NatMessage natMessage = (NatMessage) msg;
        NatMessageHeader header = natMessage.getMessageHeader();
        NatMessageType type = header.getNatMessageType();
        if (type == NatMessageType.REGISTER_RESULT) {
            processRegisterResult(natMessage);
            System.out.println("注册返回："+natMessage);
        } else if (type == NatMessageType.CONNECTED) {
            processConnected(natMessage);
        } else if (type == NatMessageType.DISCONNECTED) {
            processDisconnected(natMessage);
        } else if (type == NatMessageType.DATA) {
            processData(natMessage);
        } else if (type == NatMessageType.KEEPALIVE) {
            // 心跳包, 不处理
        } else {
            throw new QyNatException("Unknown type: " + type);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        System.out.println("Loss connection to qynat.server, Please restart!");
    }

    /**
     * if NatMessage.getType() == NatMessageType.REGISTER_RESULT
     */
    private void processRegisterResult(NatMessage natMessage) {
        if ((Boolean) natMessage.getMetaData().get("success")) {
            System.out.println("Register to qynat.server");
        } else {
            System.out.println("Register fail: " + natMessage.getMetaData().get("reason"));
            ctx.close();
        }
    }

    /**
     * if NatMessage.getType() == NatMessageType.CONNECTED
     */
    private void processConnected(NatMessage natMessage) throws Exception {

        try {
            QyNatClientHandler thisHandler = this;
            TcpConnection localConnection = new TcpConnection();
            localConnection.connect(proxyAddress, proxyPort, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    QyNatLocalProxyHandler localProxyHandler = new QyNatLocalProxyHandler(thisHandler, natMessage.getMetaData().get("channelId").toString());
                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), localProxyHandler);

                    channelHandlerMap.put(natMessage.getMetaData().get("channelId").toString(), localProxyHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            NatMessage message = new NatMessage();
            message.setMessageHeader(new NatMessageHeader(NatMessageType.DISCONNECTED));
            HashMap<String, Object> metaData = new HashMap<>();
            metaData.put("channelId", natMessage.getMetaData().get("channelId"));
            message.setMetaData(metaData);
            ctx.writeAndFlush(message);
            channelHandlerMap.remove(natMessage.getMetaData().get("channelId"));
            throw e;
        }
    }

    /**
     * if NatMessage.getType() == NatMessageType.DISCONNECTED
     */
    private void processDisconnected(NatMessage natMessage) {
        String channelId = natMessage.getMetaData().get("channelId").toString();
        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }

    /**
     * if NatMessage.getType() == NatMessageType.DATA
     */
    private void processData(NatMessage natMessage) {
        String channelId = natMessage.getMetaData().get("channelId").toString();
        QyNatCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            ctx.writeAndFlush(natMessage.getData());
        }
    }

}
