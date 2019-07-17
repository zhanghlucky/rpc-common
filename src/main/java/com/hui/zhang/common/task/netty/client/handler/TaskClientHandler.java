package com.hui.zhang.common.task.netty.client.handler;

import com.alibaba.fastjson.JSON;
import com.hui.zhang.common.task.data.Result;
import com.hui.zhang.common.task.future.InvokeFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskClientHandler extends SimpleChannelInboundHandler<Result> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskClientHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Result result) throws Exception {
		LOGGER.debug("[TASK] receive data:{}", JSON.toJSONString(result));
		InvokeFuture invokeFuture = InvokeFuture.futures.remove(result.getRequestId());
		invokeFuture.set(result);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		LOGGER.debug("[TASK] channel exception, close channel {}, {}", ctx.channel(), cause);
		ctx.close();
	}

	/*@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.debug("[TASK] channel ictive {}}", ctx.channel());
		日了狗，真纠结

	}*/
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.debug("[spider] channel inactive {}}", ctx.channel());
		ctx.close();
	}

}
