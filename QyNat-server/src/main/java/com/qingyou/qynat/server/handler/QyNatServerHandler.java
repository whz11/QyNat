package com.qingyou.qynat.server.handler;

import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.NatProto;

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

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        NatProto.NatMessage NatProto.NatMessage = (NatProto.NatMessage) msg;
//        System.out.println(msg);
//        NatProto.NatMessageHeader header = NatProto.NatMessage.getMessageHeader();
//        NatProto.NatMessageType type = header.getNatProto.NatMessageType();
//        if (type == NatProto.NatMessageType.REGISTER) {
//            processRegister(NatProto.NatMessage);
//        } else if (register) {
//            if (type == NatProto.NatMessageType.DISCONNECTED) {
//                processDisconnected(NatProto.NatMessage);
//            } else if (type == NatProto.NatMessageType.DATA) {
//                processData(NatProto.NatMessage);
//            } else if (type == NatProto.NatMessageType.KEEPALIVE) {
//                // 心跳包, 不处理
//            } else {
//                throw new QyNatException("Unknown type: " + type);
//            }
//        } else {
//            ctx.close();
//        }
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        remoteConnectionServer.close();
//        if (register) {
//            System.out.println("Stop com.qingyou.qynat.server on port: " + port);
//        }
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.REGISTER
//     */
//    private void processRegister(NatProto.NatMessage NatProto.NatMessage) {
//        HashMap<String, Object> metaData = new HashMap<>();
//
//        String password = NatProto.NatMessage.getMetaData().get("password").toString();
//        if (this.password != null && !this.password.equals(password)) {
//            metaData.put("success", false);
//            metaData.put("reason", "Token is wrong");
//        } else {
//            int port = (int) NatProto.NatMessage.getMetaData().get("port");
//
//            try {
//
//                QyNatServerHandler thisHandler = this;
//                remoteConnectionServer.bind(port, new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    public void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new QyNatRemoteProxyHandler(thisHandler));
//                        channels.add(ch);
//                    }
//                });
//
//                metaData.put("success", true);
//                this.port = port;
//                register = true;
//                System.out.println("Register success, start com.qingyou.qynat.server on port: " + port);
//            } catch (Exception e) {
//                metaData.put("success", false);
//                metaData.put("reason", e.getMessage());
//                e.printStackTrace();
//            }
//        }
//
//        NatProto.NatMessage sendBackMessage = new NatProto.NatMessage();
//        sendBackMessage.setMessageHeader(new NatProto.NatMessageHeader(NatProto.NatMessageType.REGISTER_RESULT));
//        sendBackMessage.setMetaData(metaData);
//        ctx.writeAndFlush(sendBackMessage);
//
//        if (!register) {
//            System.out.println("com.qingyou.qynat.client.Client register error: " + metaData.get("reason"));
//            ctx.close();
//        }
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DATA
//     */
//    private void processData(NatProto.NatMessage NatProto.NatMessage) {
//        channels.writeAndFlush(NatProto.NatMessage.getData(), channel -> channel.id().asLongText().equals(NatProto.NatMessage.getMetaData().get("channelId")));
//    }
//
//    /**
//     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DISCONNECTED
//     *
//     * @param NatProto.NatMessage
//     */
//    private void processDisconnected(NatProto.NatMessage NatProto.NatMessage) {
//        channels.close(channel -> channel.id().asLongText().equals(NatProto.NatMessage.getMetaData().get("channelId")));
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NatProto.NatMessage natMessage = (NatProto.NatMessage) msg;
        System.out.println(msg);
        NatProto.Type type = natMessage.getType();
        if (type == NatProto.Type.REGISTER) {
            processRegister(natMessage);
        } else if (register) {
            if (type == NatProto.Type.DISCONNECTED) {
                processDisconnected(natMessage);
            } else if (type == NatProto.Type.DATA) {
                processData(natMessage);
            } else if (type == NatProto.Type.KEEPALIVE) {
                // 心跳包, 不处理
            } else {
                throw new QyNatException("Unknown type: " + type);
            }
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        remoteConnectionServer.close();
        if (register) {
            System.out.println("Stop QyNat-server on port: " + port);
        }
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.REGISTER
     */
    private void processRegister(NatProto.NatMessage natMessage) {
        HashMap<String, String> metaData = new HashMap<>();

        String password = natMessage.getMetaDataMap().get("password");
        String addr = natMessage.getMetaDataMap().get("addr");
        if (this.password != null && !this.password.equals(password)) {
            metaData.put("success", "false");
            metaData.put("reason", "Token is wrong");
        } else {
            int port = Integer.parseInt(natMessage.getMetaDataMap().get("port"));

            try {

                QyNatServerHandler thisHandler = this;
                remoteConnectionServer.bind(port, new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new QyNatRemoteProxyHandler(thisHandler));
                        channels.add(ch);
                    }
                });

                metaData.put("success", "true");
                this.port = port;
                register = true;
                System.out.println("Register success, start QyNat-server on addr: " + addr + ":" + port);
            } catch (Exception e) {
                metaData.put("success", "false");
                metaData.put("reason", e.getMessage());
                e.printStackTrace();
            }
        }

        NatProto.NatMessage sendBackMessage = NatProtoCodec.createNatMessage(0, NatProto.Type.REGISTER_RESULT, metaData, null);
        ctx.writeAndFlush(sendBackMessage);

        if (!register) {
            System.out.println("start QyNat-client register error: " + metaData.get("reason"));
            ctx.close();
        }
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DATA
     */
    private void processData(NatProto.NatMessage natMessage) {
        channels.writeAndFlush(natMessage.getData(), channel -> channel.id().asLongText().equals(natMessage.getMetaDataMap().get("channelId")));
    }

    /**
     * if NatProto.NatMessage.getType() == NatProto.NatMessageType.DISCONNECTED
     *
     * @param natMessage
     */
    private void processDisconnected(NatProto.NatMessage natMessage) {
        channels.close(channel -> channel.id().asLongText().equals(natMessage.getMetaDataMap().get("channelId")));
    }
}
