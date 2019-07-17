package com.hui.zhang.common.task.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-09-14 19:56
 **/
@ChannelHandler.Sharable
public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerIdleStateTrigger.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            //读超时
            if(event.state() == IdleState.READER_IDLE){
                LOGGER.warn("[task] server read timeout, close channel {}", ctx.channel());
                ctx.channel().close();//关闭channel
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
