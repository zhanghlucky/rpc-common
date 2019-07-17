package com.hui.zhang.common.task.registry;

import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.hui.zhang.common.task.TaskInfo;
import com.hui.zhang.common.task.serialize.TaskSerialize;
import io.netty.util.internal.ConcurrentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.*;

/**
 * 重构task zk 服务，新增监控zookeeper 节点
 * @author:jiangshun@centaur.cn
 * @create 2018-09-04 14:37
 **/
public class TaskZkRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskZkRegistry.class);

    public final static TaskZkRegistry INSTANCE=new TaskZkRegistry();

    private final LinkedBlockingQueue<TaskInfo> taskQueue = new LinkedBlockingQueue<>();

    private final ExecutorService taskRegisterExecutor =
            Executors.newSingleThreadExecutor(new NamedThreadFactory("task executor"));

    private final ScheduledExecutorService taskRegisterScheduledExecutor =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("task schedule executor"));

    /**
     * 检查注册情况线程
     */
    private final ExecutorService taskLocalRegisterWatchExecutor =
            Executors.newSingleThreadExecutor(new NamedThreadFactory("taskProvider check executor"));

    private final ConcurrentSet<TaskInfo> providerSet = new ConcurrentSet<>();

    public TaskZkRegistry(){
        //处理注册信息
        taskRegisterExecutor.execute(() -> {
            TaskInfo taskInfo = null;
            try {
                while (true) {
                    taskInfo = taskQueue.take();
                    providerSet.add(taskInfo);
                    taskRegister(taskInfo);
                }
            } catch (Throwable throwable) {
                LOGGER.error("TaskZkRegistry registe exception, try again", throwable);
                //每秒执行一次 重新注册失败的注册信息
                final TaskInfo finalTaskInfo = taskInfo;
                taskRegisterScheduledExecutor.schedule(() -> {
                    taskQueue.add(finalTaskInfo);
                }, 1, TimeUnit.SECONDS);
            }
        });

        taskLocalRegisterWatchExecutor.execute(() -> {
            while (true) {
                try {
                    //每三秒检查一次
                    Thread.sleep(3000);
                    //遍历需要发布的服务， 如果断开 则重新注册
                    Iterator<TaskInfo> iterator = providerSet.iterator();
                    while (iterator.hasNext()) {
                        TaskInfo taskInfo = iterator.next();
                        String path= TaskZkManager.INSTANCE.getServiceTaskServicePath(taskInfo.getRegistryName(),taskInfo.getAddress());
                        //需要重新注冊
                        if (TaskZkManager.INSTANCE.client().checkExists().forPath(path) == null) {
                            register(taskInfo);
                        }
                    }
                } catch (Throwable throwable) {
                    LOGGER.error("ZkRegistry check register status exception, try again", throwable);
                }
            }
        });
    }

    public void register(TaskInfo taskInfo){
        taskQueue.add(taskInfo);
    }
    private   void taskRegister(TaskInfo taskInfo){
        try {
            String path = TaskZkManager.INSTANCE.getServiceTaskServicePath(taskInfo.getRegistryName(),taskInfo.getAddress());
            LOGGER.info("【保存创建服务临时节点】 :任务名称{} ，节点路径：{}" ,taskInfo.getTaskName(),path);
            TaskZkManager.INSTANCE.createNode(path, true);
            TaskZkManager.INSTANCE
                    .client()
                    .setData().forPath(path, TaskSerialize.serialize(taskInfo));
            // 保存在线服务节点
            String infoPath = TaskZkManager.INSTANCE.getOnlineTaskServicePath(taskInfo.getRegistryName(),taskInfo.getAddress());
            if (TaskZkManager.INSTANCE.client().checkExists().forPath(infoPath) == null){
                LOGGER.info("【保存创建服务在线服务节点】  ,任务名称为：{}, 节点路径为:{}" ,taskInfo.getTaskName(),infoPath);
                TaskZkManager.INSTANCE.createNode(infoPath, true);
                TaskZkManager.INSTANCE
                        .client()
                        .setData().forPath(path, TaskSerialize.serialize(taskInfo));
            }
            else{
                LOGGER.info("【TASK 在线节点已存在，无需注册,节点路径为:{}】",infoPath);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException("task 注册异常");
        }
    }
}
