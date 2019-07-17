package com.hui.zhang.common.util.performance.queue;

import com.hui.zhang.common.util.performance.model.ITask;
import org.slf4j.LoggerFactory;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 队列 拒绝策略 直接打印在日志中
 */
public class TaskPersister {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskPersister.class);

	public void persist(ITask<?> task) {
		logger.warn(task.getType() + ":" + task.getSerializedMsg());
	}

}
