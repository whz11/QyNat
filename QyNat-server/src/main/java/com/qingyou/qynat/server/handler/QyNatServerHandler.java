package com.qingyou.qynat.server.handler;

import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.NatMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import com.qingyou.qynat.server.server.TcpServer;

import java.util.HashMap;

/**
 * @author whz
 * @date 2021/7/16 01:03
 **/
public class QyNatServerHandler extends QyNatCommonHandler {

    private final TcpServer remoteConnectionServer = new TcpServer();

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final String password;
    private int port;

    private boolean register = false;

    public QyNatServerHandler(String password) {
        this.password = password;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NatMessage natMessage = (NatMessage) msg;
        System.out.println(msg);
        NatMessageHeader header = natMessage.getMessageHeader();
        NatMessageType type = header.getNatMessageType();
        if (type == NatMessageType.REGISTER) {
            processRegister(natMessage);
        } else if (register) {
            if (type == NatMessageType.DISCONNECTED) {
                processDisconnected(natMessage);
            } else if (type == NatMessageType.DATA) {
                processData(natMessage);
            } else if (type == NatMessageType.KEEPALIVE) {
                // 心跳包, 不处理
            } else {
                throw new QyNatException("Unknown type: " + type);
            }
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        remoteConnectionServer.close();
        if (register) {
            System.out.println("Stop com.qingyou.qynat.server on port: " + port);
        }
    }

    /**
     * if NatMessage.getType() == NatMessageType.REGISTER
     */
    private void processRegister(NatMessage natMessage) {
        HashMap<String, Object> metaData = new HashMap<>();

        String password = natMessage.getMetaData().get("password").toString();
        if (this.password != null && !this.password.equals(password)) {
            metaData.put("success", false);
            metaData.put("reason", "Token is wrong");
        } else {
            int port = (int) natMessage.getMetaData().get("port");

            try {

                QyNatServerHandler thisHandler = this;
                remoteConnectionServer.bind(port, new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new QyNatRemoteProxyHandler(thisHandler));
                        channels.add(ch);
                    }
                });

                metaData.put("success", true);
                this.port = port;
                register = true;
                System.out.println("Register success, start com.qingyou.qynat.server on port: " + port);
            } catch (Exception e) {
                metaData.put("success", false);
                metaData.put("reason", e.getMessage());
                e.printStackTrace();
            }
        }

        NatMessage sendBackMessage = new NatMessage();
        sendBackMessage.setMessageHeader(new NatMessageHeader(NatMessageType.REGISTER_RESULT));
        sendBackMessage.setMetaData(metaData);
        ctx.writeAndFlush(sendBackMessage);

        if (!register) {
            System.out.println("com.qingyou.qynat.client.Client register error: " + metaData.get("reason"));
            ctx.close();
        }
    }

    /**
     * if NatMessage.getType() == NatMessageType.DATA
     */
    private void processData(NatMessage natMessage) {
        channels.writeAndFlush(natMessage.getData(), channel -> channel.id().asLongText().equals(natMessage.getMetaData().get("channelId")));
    }

    /**
     * if NatMessage.getType() == NatMessageType.DISCONNECTED
     *
     * @param natMessage
     */
    private void processDisconnected(NatMessage natMessage) {
        channels.close(channel -> channel.id().asLongText().equals(natMessage.getMetaData().get("channelId")));
    }
}
