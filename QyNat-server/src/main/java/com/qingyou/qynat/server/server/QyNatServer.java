package com.qingyou.qynat.server.server;

import com.qingyou.qynat.commom.codec.NatMessageDecoder;
import com.qingyou.qynat.commom.codec.NatMessageEncoder;
import com.qingyou.qynat.commom.protocol.NatProto;
import com.qingyou.qynat.server.handler.QyNatServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author whz
 * @date 2021/7/16 01:00
 **/
public class QyNatServer {

    public void start(int port, String password) throws InterruptedException {
        TcpServer natClientServer = new TcpServer();
        natClientServer.bind(port, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                QyNatServerHandler natServerHandler = new QyNatServerHandler(password);
                ch.pipeline().addLast(//new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new ProtobufVarint32FrameDecoder(),//用于半包处理
                        //ProtobufDecoder解码器，参数是NatMessage，也就是需要接收到的消息解码为NatMessage类型的对象
                        new ProtobufDecoder(NatProto.NatMessage.getDefaultInstance()),
                        new ProtobufVarint32LengthFieldPrepender(),
                        new ProtobufEncoder(),
                        new IdleStateHandler(60, 30, 0), natServerHandler);
            }
        });
    }

}
