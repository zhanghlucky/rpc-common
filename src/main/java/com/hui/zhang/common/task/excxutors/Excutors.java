package com.hui.zhang.common.task.excxutors;

import com.hui.zhang.common.task.service.Executor;

import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2017-11-13 20:31
 **/
public class Excutors {
    private static Map<String, Executor> executors;

    public static synchronized void addExecutors(String taskName, Executor executor) {
        if (null == executors) {
            executors = new HashMap<>();
        }
        executors.put(taskName, executor);
    }

    public static Executor getExecutor(String taskName) {
        Executor executor = executors.get(taskName);
        if (null != executor) {
            return executor;
        } else {
            return null;
        }
    }
}
