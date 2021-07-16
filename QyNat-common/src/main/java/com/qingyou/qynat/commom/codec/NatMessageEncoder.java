package com.qingyou.qynat.commom.codec;

import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author whz
 * @date 2021/7/15 19:45
 **/
public class NatMessageEncoder extends MessageToByteEncoder<NatMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NatMessage natMessage, ByteBuf byteBuf) throws Exception {
        byte[] body = ProtostuffUtil.serialize(natMessage);
        byteBuf.writeInt(body.length);
        byteBuf.writeBytes(body);
    }
}
