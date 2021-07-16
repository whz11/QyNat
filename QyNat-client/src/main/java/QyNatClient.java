import com.qingyou.qynat.commom.codec.NatMessageDecoder;
import com.qingyou.qynat.commom.codec.NatMessageEncoder;
import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.ProtostuffUtil;
import com.qingyou.qynat.commom.protocol.TestMsg;
import handler.QyNatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whz
 * @date 2021/7/16 01:06
 **/
public class QyNatClient {
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "7777"));

    public static void main(String[] args) throws Exception {

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            ch.pipeline().addLast(new NatMessageEncoder());
                            ch.pipeline().addLast(new NatMessageDecoder());
                            ch.pipeline().addLast(new QyNatClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            /*future.channel().writeAndFlush("Hello Netty Server ,I am a common client");
            future.channel().closeFuture().sync();*/
            NatMessageHeader header=new NatMessageHeader();
            header.setRequestId(123L);
            header.setToken("sadasd");
            Map<String, Object> data=new HashMap<>();
            data.put("key",12312);
            NatMessage natMessage = new NatMessage(header,data,null);
            future.channel().writeAndFlush(natMessage);
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
