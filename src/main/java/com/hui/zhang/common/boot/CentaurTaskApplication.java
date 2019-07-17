package com.hui.zhang.common.boot;

import com.hui.zhang.common.listener.ApplicationStartUpListener;
import com.hui.zhang.common.task.Task;
import com.hui.zhang.common.task.TaskInfo;
import com.hui.zhang.common.task.excxutors.Excutors;
import com.hui.zhang.common.task.netty.server.TaskNettyServerThread;
import com.hui.zhang.common.task.registry.TaskZkRegistry;
import com.hui.zhang.common.task.service.Executor;
import com.hui.zhang.common.util.IpUtils;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.StrKit;
import com.hui.zhang.common.util.docker.RegistHostPortUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tian.luan
 */
public class CentaurTaskApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(CentaurTaskApplication.class);

    private SpringApplication application;

    protected ApplicationContext context;

    public CentaurTaskApplication(Class cls) {
        buildDefaultApplication(cls);
    }

    public CentaurTaskApplication(Class cls, List<ApplicationListener> listenerList) {
        buildDefaultApplication(cls);
        /**
         * 添加监听器
         */
        for (ApplicationListener applicationListener : listenerList) {
            application.addListeners(applicationListener);
        }
    }

    private void buildDefaultApplication(Class cls) {
        application = new SpringApplication(cls);
        application.addListeners(new ApplicationStartUpListener());
        Map<String, Object> pro = new HashMap<>();
        pro.put("server.port", AppConfigUtil.getCfgAppPO().getWebPort());
        pro.put("server.contextPath", "/" + AppConfigUtil.getCfgAppPO().getWebName());
        pro.put("spring.output.ansi.enabled","never");
        application.setDefaultProperties(pro);
    }

    public void run() {
        context = application.run();
        scanTasks(); // 初始化本地所有注解TASK
        AppConfigUtil.initAppParams(); // 配置中心本地化
    }

    private void scanTasks() {
        int port= RegistHostPortUtil.getRegistProtocolPort() + 10000;
        Map<String, Executor> beans = context.getBeansOfType(Executor.class);
        LOGGER.info("Find {} tasks.", beans.size());
        beans.forEach((executorName, executor) -> {
            Class<?> executorClass = executor.getClass();
            Task task = executorClass.getAnnotation(Task.class);
            if (task != null) {
                String taskName = StrKit.isBlank(task.name()) ? executorClass.getSimpleName() : task.name();
                Excutors.addExecutors(taskName, executor);
                registerTask(taskName,port); // 注册服务
                LOGGER.info("Register task: {} -> {}.", taskName, executorClass.getName());
            }
        });
        new TaskNettyServerThread(IpUtils.getLoalhostIP(),port).start();
    }

    public void registerTask(String taskName,int port) {
        TaskInfo taskInfo = new TaskInfo(PropertyUtil.getProperty("app.name"),taskName,IpUtils.getLoalhostIP() + ":" + port);
        TaskZkRegistry.INSTANCE.register(taskInfo);
    }


}

