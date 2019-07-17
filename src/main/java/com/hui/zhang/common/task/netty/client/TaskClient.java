package com.hui.zhang.common.task.netty.client;

import com.hui.zhang.common.task.data.Address;
import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.task.data.Result;
import com.hui.zhang.common.task.future.InvokeFuture;
import com.hui.zhang.common.task.netty.TaskDecoder;
import com.hui.zhang.common.task.netty.TaskEncoder;
import com.hui.zhang.common.task.netty.client.handler.TaskClientHandler;
import com.hui.zhang.common.task.registry.RpcMonitor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * RPC服务客户端
 * @author zhanghui
 *
 */
public class TaskClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskClient.class);

    private String host;
    private int port;
    private Address address;
    private static  final  long TIME_OUT = 60 * 60 * 24 * 1000; // 由于task 不确定超时时间，此时间配过长  默认24 小时

    public TaskClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.address = Address.of(host, port);
    }
    public Result send(ExecuteParam request) throws Throwable {
        LOGGER.info("发送请求：task 名称 ，task 调度器 {},AppName:{}", request.getTaskName(), request.getExecuter(), request.getAppId());
        Channel channel = RpcMonitor.getChannel(address);
        InvokeFuture invokeFuture = write(request, channel);
        return (Result) invokeFuture.get(TIME_OUT, TimeUnit.MILLISECONDS);
    }
  /* 发送消息
     * @param request
     * @param channel
     */
    private InvokeFuture write(ExecuteParam request, Channel channel) {
        InvokeFuture invokeFuture = new InvokeFuture();
        channel.writeAndFlush(request);
        InvokeFuture.futures.putIfAbsent(request.getTaskId(), invokeFuture);
        return invokeFuture;
    }

}
