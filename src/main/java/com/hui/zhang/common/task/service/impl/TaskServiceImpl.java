package com.hui.zhang.common.task.service.impl;

import com.hui.zhang.common.logger.EdbLogger;
import com.hui.zhang.common.logger.EdbLoggerFactory;
import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.task.data.Result;
import com.hui.zhang.common.task.data.TaskContext;
import com.hui.zhang.common.task.excxutors.Excutors;
import com.hui.zhang.common.task.registry.DistributedLock;
import com.hui.zhang.common.task.service.Executor;
import com.hui.zhang.common.task.service.TaskService;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2017-11-09 14:39
 **/
@Service
public class TaskServiceImpl implements TaskService {
    private static final EdbLogger logger = EdbLoggerFactory.getLogger(TaskServiceImpl.class);
    private ExecutorService pool = Executors.newCachedThreadPool();
    private ConcurrentMap<String, String> runningTasks = new ConcurrentHashMap<>();
    /**
     * 执行任务
     *
     * @param param 任务参数
     * @return
     */
    @Override
    public Result execute(ExecuteParam param) {
       /* logger.info("接收任务:{}", JsonEncoder.DEFAULT.encode(param));*/
        if (param.getTaskName().equals("task-heartbeat")){
            return new Result(true,"心跳检查");
        }
        boolean lock = DistributedLock.INSTRANCE.getLock("/"+param.getTaskName()); // 获取锁
        if(runningTasks.containsKey(param.getTaskName()) && !lock){
            pool.execute(() -> {
                String error = String.format("任务 %s 正在执行, 跳过此次调度(如果多次发生类型情况, 请检查调度时间是否合理)", param.getTaskName());
                logger.error(error);
                Date start = new Date();
                // 异步写入数据库
            });
            return new Result(false, "任务正在执行:"+param.getTaskName());
        }
        String name = param.getExecuter();//StrKit.isBlank(param.getTaskAlias()) ? param.getTaskName() : param.getTaskAlias();
        Executor executor = Excutors.getExecutor(name);
        if (executor == null) {
            logger.error("找不到任务:{}", name);
            return new Result(false, "找不到任务: " + name);
        }
        try {
            runningTasks.put(param.getTaskName(), param.getExecuter());
            pool.execute(() -> this.execute(executor, param));
           // this.execute(executor, param);
        } catch (Exception e) {
            logger.error("提交任务到线程池失败", e);
            return new Result(false, "提交任务到线程池失败: " + e.getMessage());
        }

        return new Result(true, null);
    }
    private void execute(Executor executor, ExecuteParam param) {
        Date start = new Date();
        try {
            logger.info("开始执行任务:{}，执行方式:{}", param.getTaskName()/*, param.getType()*/);
            TaskContext ctx = new TaskContext(param);
            executor.execute(ctx);
          // TODO  异步通知
            logger.info("任务执行成功, 耗时:{}", Duration.ofMillis(new Date().getTime() - start.getTime()));
        } catch (Exception e) {
            String error = e.getMessage();
            if (StrKit.isBlank(error)) {
                error = e.toString();
            }
            // TODO  异步通知
            logger.error("任务执行失败, 耗时:{}, 错误信息:{}", Duration.ofMillis(new Date().getTime() - start.getTime()),
                    e.getMessage());
        } finally {
           // if (param.getType() == ExecuteParam.ExecuteType.AUTO) {
                runningTasks.remove(param.getTaskName());
                DistributedLock.INSTRANCE.unLock("/"+param.getTaskName()); // 释放锁
          //  }
        }
    }
}
