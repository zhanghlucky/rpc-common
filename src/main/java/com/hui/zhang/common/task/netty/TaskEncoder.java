package com.hui.zhang.common.task.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TaskEncoder extends MessageToByteEncoder {

	private Class<?> genericClass;

    public TaskEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            //byte[] data = SerializationUtils.serialize(in);
            byte[] data= TaskHessianSerialize.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}