package com.qingyou.qynat.client.client;

import com.qingyou.qynat.commom.handler.QyNatCommonHandler;
import com.qingyou.qynat.commom.protocol.proto.NatProto;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;

/**
 * @author whz
 * @date 2021/7/16 01:06
 **/
public class QyNatClient {

    public QyNatClient() {

    }

    public <T extends QyNatCommonHandler> void connect(String serverAddress, String serverPort, T natClientHandler) throws IOException, InterruptedException {

        TcpConnection natConnection = new TcpConnection();
        ChannelFuture future = natConnection.connect(serverAddress, serverPort, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        new ProtobufVarint32FrameDecoder(),//用于半包处理
                        //ProtobufDecoder解码器，参数是NatMessage，也就是需要接收到的消息解码为NatMessage类型的对象
                        new ProtobufDecoder(NatProto.NatMessage.getDefaultInstance()),
                        new ProtobufVarint32LengthFieldPrepender(),
                        new ProtobufEncoder(),
                        new IdleStateHandler(60, 30, 0), natClientHandler);
            }
        });
        // channel close retry connect
        future.addListener(future1 -> new Thread(() -> {
            while (true) {
                try {
                    connect(serverAddress, serverPort, natClientHandler);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start());
    }

}
