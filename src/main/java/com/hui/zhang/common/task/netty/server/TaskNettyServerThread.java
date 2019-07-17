package com.hui.zhang.common.task.netty.server;

import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.task.data.Result;
import com.hui.zhang.common.task.netty.TaskDecoder;
import com.hui.zhang.common.task.netty.TaskEncoder;
import com.hui.zhang.common.task.service.TaskServiceHander;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-02-08 10:46
 **/
public class TaskNettyServerThread extends Thread{
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskNettyServerThread.class);
    private Channel channel;
    private String host;
    private int port;
    public TaskNettyServerThread(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        //是用来处理I/O操作的线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup();//用来accept客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();//处理客户端数据的读写操作
        try {
            ServerBootstrap bootstrap =  new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            ((Channel) channel).pipeline()
                                    .addLast("handler",new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS))            // 心跳处理
                                    .addLast(new TaskDecoder(ExecuteParam.class)) // 将 RPC 请求进行解码（为了处理请求） ChannelInboundHandlerAdapter  1
                                    .addLast(new TaskEncoder(Result.class)) // 将 RPC 响应进行编码（为了返回响应）ChannelOutboundHandlerAdapter 2*/
                                    .addLast(new TaskServiceHander()); // 处理 RPC 请求 SimpleChannelInboundHandler 3
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(getBindAddress()).sync();
            channel=future.channel();
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("服务端异常{}",e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    private SocketAddress getBindAddress(){

        InetSocketAddress socketAddress= new InetSocketAddress(host,port);
        LOGGER.info("TASK netty服务绑定 server-ip->{},server-port->{}",host,port);
        return  socketAddress;
    }


}
