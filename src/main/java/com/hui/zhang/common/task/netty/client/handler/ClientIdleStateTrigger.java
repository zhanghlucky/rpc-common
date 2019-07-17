package com.hui.zhang.common.task.netty.client.handler;

import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.util.UUIDGenerator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by zuti on 2018/3/1.
 * email zuti@centaur.cn
 */
@ChannelHandler.Sharable
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientIdleStateTrigger.class);

	private static ExecuteParam HEARTBEAT;

	static {
		HEARTBEAT = new ExecuteParam();
		HEARTBEAT.setTaskId(UUIDGenerator.random16UUID());
		HEARTBEAT.setTaskName("task-heartbeat");
		HEARTBEAT.setAppId(null);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.WRITER_IDLE) {
				LOGGER.debug("[TASK] channel idle, send heartbeat message, {}", ctx.channel());
				ctx.channel().writeAndFlush(HEARTBEAT);
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

}
