package com.hui.zhang.common.task.service;

import com.hui.zhang.common.task.data.TaskContext;

/**
 * Created by tian.luan
 */
@FunctionalInterface
public interface Executor {
    void execute(TaskContext ctx);
}
