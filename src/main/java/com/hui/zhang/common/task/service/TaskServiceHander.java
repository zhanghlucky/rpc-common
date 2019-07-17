package com.hui.zhang.common.task.service;

import com.hui.zhang.common.spring.SpringBeanUtil;
import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.task.data.Result;
import com.hui.zhang.common.task.data.TaskContext;
import com.hui.zhang.common.task.excxutors.Excutors;
import com.hui.zhang.common.task.registry.DistributedLock;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.StrKit;
import com.hui.zhang.common.util.UUIDGenerator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-02-08 10:55
 **/
public class TaskServiceHander extends SimpleChannelInboundHandler<ExecuteParam> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceHander.class);
    private TaskService taskService =  SpringBeanUtil.getBeanByType(TaskService.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ExecuteParam param) throws Exception {
        if (!isHeartbeatMsg(param)){
            LOGGER.info("[TASK] 接收任务，任务名：{},appName:{},执行器：{}，调度时间：{}，描述：{}",param.getTaskName(),param.getAppId(),param.getExecuter(),param.getCron(),param.getTaskNote() );
            Result result = new Result();
            result = this.taskService.execute(param);
            result.setRequestId(param.getTaskId());
            ChannelFuture channelFuture = ctx.writeAndFlush(result);
        }else{
            LOGGER.debug("[TASK] heartbeat message channel: {}.", ctx.channel());
            ReferenceCountUtil.release(param);
        }
    }
    private boolean isHeartbeatMsg(ExecuteParam request) {
        return request.getTaskName().equals("task-heartbeat");
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.debug("[TASK] server exception:{}", cause);
        ctx.close();
    }

}
