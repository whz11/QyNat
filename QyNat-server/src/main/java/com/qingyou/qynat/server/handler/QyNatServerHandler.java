package com.qingyou.qynat.server.handler;

import com.qingyou.qynat.commom.codec.NatProtoCodec;
import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.proto.NatProto;

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

    private final String token;
    private int port;

    private boolean register = false;

    public QyNatServerHandler(String token) {
        this.token = token;
    }


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


    private void processRegister(NatProto.NatMessage natMessage) {
        HashMap<String, String> metaData = new HashMap<>();

        String token = natMessage.getMetaDataMap().get("token");
        String addr = natMessage.getMetaDataMap().get("addr");
        if (this.token != null && !this.token.equals(token)) {
            metaData.put("success", "false");
            metaData.put("errMsg", "token is wrong");
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
                metaData.put("mapping", addr + ":" + port);
                this.port = port;
                register = true;
                System.out.println("Register success, addr: " + addr + ":" + port);
            } catch (Exception e) {
                metaData.put("success", "false");
                metaData.put("errMsg", e.getMessage());
                e.printStackTrace();
            }
        }

        NatProto.NatMessage sendBackMessage = NatProtoCodec.createNatMessage(0, NatProto.Type.REGISTER_RESULT, metaData, null);
        ctx.writeAndFlush(sendBackMessage);

        if (!register) {
            System.out.println("start QyNat-client register error: " + metaData.get("errMsg"));
            ctx.close();
        }
    }


    private void processData(NatProto.NatMessage natMessage) {
        channels.writeAndFlush(natMessage.getData().toByteArray(), channel -> channel.id().asLongText().equals(natMessage.getMetaDataMap().get("channelId")));
    }


    private void processDisconnected(NatProto.NatMessage natMessage) {
        channels.close(channel -> channel.id().asLongText().equals(natMessage.getMetaDataMap().get("channelId")));
    }
}
