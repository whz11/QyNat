package com.qingyou.qynat.client.client;

import com.qingyou.qynat.client.handler.QyNatClientHandler;
import com.qingyou.qynat.commom.codec.NatMessageDecoder;
import com.qingyou.qynat.commom.codec.NatMessageEncoder;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;

/**
 * @author whz
 * @date 2021/7/16 01:06
 **/
public class QyNatClient {
    public void connect(String serverAddress, int serverPort, String password, int remotePort, String proxyAddress, int proxyPort) throws IOException, InterruptedException {

        TcpConnection natConnection = new TcpConnection();
        ChannelFuture future = natConnection.connect(serverAddress, serverPort, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                QyNatClientHandler natClientHandler = new QyNatClientHandler(remotePort, password,
                        proxyAddress, proxyPort);
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new NatMessageDecoder(), new NatMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), natClientHandler);
            }
        });

        // channel close retry connect
        future.addListener(future1 -> new Thread(() -> {
            while (true) {
                try {
                    connect(serverAddress, serverPort, password, remotePort, proxyAddress, proxyPort);
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
