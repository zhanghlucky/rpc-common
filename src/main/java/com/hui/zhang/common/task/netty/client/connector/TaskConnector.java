package com.hui.zhang.common.task.netty.client.connector;


import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.task.data.Result;
import com.hui.zhang.common.task.netty.TaskDecoder;
import com.hui.zhang.common.task.netty.TaskEncoder;
import com.hui.zhang.common.task.netty.client.handler.ClientIdleStateTrigger;
import com.hui.zhang.common.task.netty.client.handler.ConnectionWatcher;
import com.hui.zhang.common.task.netty.client.handler.TaskClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TaskConnector extends TaskAbstractConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskConnector.class);

    private static final int WRITE_IDLE_TIME = 10;

    protected final HashedWheelTimer timer = new HashedWheelTimer();

    private Bootstrap boot = new Bootstrap();

    private static EventLoopGroup group =
            new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("task-connector", Thread.MAX_PRIORITY));
    
    @Override
    public Channel connect(String host, int port) {
        
        boot.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
            
        final ConnectionWatcher watchdog = new ConnectionWatcher(boot, timer, port,host, true) {

                @Override
                public ChannelHandler[] handlers() {
                    return new ChannelHandler[] {
                            this,
                            new IdleStateHandler(0, WRITE_IDLE_TIME, 0, TimeUnit.SECONDS),
                            new ClientIdleStateTrigger(),
                            new TaskEncoder(ExecuteParam.class),
                            new TaskDecoder(Result.class),
                            new TaskClientHandler()
                    };
                }
            };
            
            ChannelFuture future;

            //进行连接
            try {
                synchronized (boot) {
                    boot.handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(watchdog.handlers());
                        }
                    });

                    future = boot.connect(host,port);
                }

                future.sync();
            } catch (Throwable t) {
                throw new RuntimeException("connects to " + host + ":" + port + " fails", t);
            }

        Channel channel = future.channel();

        return channel;

    }

}
