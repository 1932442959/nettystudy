package com.lucw.rpc.protocol;

import com.lucw.rpc.pojo.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
/**
 * 必须和 {@link io.netty.handler.codec.LengthFieldBasedFrameDecoder} 一起使用，确保接到的ByteBuf是完整的
 */
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 4个字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 1个字节的版本号
        out.writeByte(1);
        // 1个字节的序列化算法   0：JDK   1：Json
        out.writeByte(0);
        // 1个字节的指令类型
        out.writeByte(msg.getMessageType());
        // 4个字节的指令请求序号
        out.writeInt(msg.getSequenceId());
        //补充为16位对齐--2的n次方
        out.writeByte(0xff);
        // 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 4个字节长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message);
    }
}
