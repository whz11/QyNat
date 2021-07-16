package com.qingyou.qynat.server.server;

import com.qingyou.qynat.commom.codec.NatMessageDecoder;
import com.qingyou.qynat.commom.codec.NatMessageEncoder;
import com.qingyou.qynat.server.handler.QyNatServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
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
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new NatMessageDecoder(), new NatMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), natServerHandler);
            }
        });
    }

}
