package com.hui.zhang.common.util.performance.model;

import com.hui.zhang.common.util.performance.queue.TaskDispatcher;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class StopWatch {

	/**
	 * 开始记录一个method执行info
	 * @param task
	 * @return
	 */
	public MethodExecuteInfo start(ITask<?> task) {
		MethodExecuteInfo methodExecuteInfo = new MethodExecuteInfo();
		methodExecuteInfo.setTask(task);
		methodExecuteInfo.setStartTime(System.currentTimeMillis());
		return methodExecuteInfo;
	}

	/**
	 * 一个method执行计划结束 分发任务
	 * @param methodExecuteInfo
	 */
	public void stop(MethodExecuteInfo methodExecuteInfo) {
		methodExecuteInfo.setCostTime((int) (System.currentTimeMillis() - methodExecuteInfo.getStartTime()));
		TaskDispatcher.putMethodExecuteInfo(methodExecuteInfo);
	}

}
