package com.hui.zhang.common.util.performance.model;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public abstract class AbstractTask<T> implements ITask<T>{

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.NORMAL;
	}

	@Override
	public int getTimeoutMS() {
		return 500;
	}

	@Override
	public T call() throws Exception {
		return doCall();
	}

	protected abstract T doCall() throws Exception;

}
