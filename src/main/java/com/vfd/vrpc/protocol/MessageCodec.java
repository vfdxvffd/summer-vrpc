package com.vfd.vrpc.protocol;

import com.vfd.vrpc.message.Message;
import com.vfd.vrpc.protocol.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @PackageName: com.vfd.protocol
 * @ClassName: MessageCodec
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:17
 */
@SuppressWarnings("unused")
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    private final Serializer serializer;

    public MessageCodec(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> list) {
        ByteBuf buf = ctx.alloc().buffer();
        // 以下按照协议编码
        // 4个字节的魔数
        buf.writeBytes(new byte[] {3, 3, 3, 3});
        // 1个字节的版本
        buf.writeByte(1);
        // 1个字节的消息类型
        buf.writeByte(message.getMessageType());
        // 4个字节的序列号
        buf.writeInt(message.getSequenceId());
        // 2个字节的对齐位无意义
        buf.writeBytes(new byte[]{0, 0});

        final byte[] bytes = serializer.serializer(message);

        // 4个字节的消息长度
        buf.writeInt(bytes.length);
        // 消息具体内容
        buf.writeBytes(bytes);

        list.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) {
        final int magicNum = buf.readInt();
        final byte version = buf.readByte();
        final byte messageType = buf.readByte();
        final int sequenceId = buf.readInt();
        buf.readBytes(2);
        final int length = buf.readInt();

        byte[] bytes = new byte[length];
        buf.readBytes(bytes, 0, length);

        // 反序列化出内容
        final Message message = serializer.deserializer(Message.getMessageClass(messageType), bytes);
        list.add(message);
    }
}
