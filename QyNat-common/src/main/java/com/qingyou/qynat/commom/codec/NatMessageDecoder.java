package com.qingyou.qynat.commom.codec;

import com.qingyou.qynat.commom.exception.QyNatException;
import com.qingyou.qynat.commom.protocol.NatMessage;
import com.qingyou.qynat.commom.protocol.NatMessageHeader;
import com.qingyou.qynat.commom.protocol.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author whz
 * @date 2021/7/15 19:45
 **/
public class NatMessageDecoder extends ByteToMessageDecoder {
    private final Integer REQUEST_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < REQUEST_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        int head = byteBuf.readInt();
        if (head < 0) {
            channelHandlerContext.close();
        }
        if (byteBuf.readableBytes() < head) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] body = new byte[head];
        byteBuf.readBytes(body);
        NatMessage natMessage = ProtostuffUtil.deserialize(body, NatMessage.class);
        out.add(natMessage);
    }
}
