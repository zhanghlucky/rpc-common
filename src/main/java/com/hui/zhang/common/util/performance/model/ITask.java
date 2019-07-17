package com.hui.zhang.common.util.performance.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public interface ITask<T> extends Callable<T>, Serializable {

	/**
	 * 任务类型
	 * @return
	 */
	String getType();

	/**
	 * 超时时间
	 * @return
	 */
	int getTimeoutMS();

	/**
	 * 任务优先级
	 * @return
	 */
	TaskPriority getPriority();

	/**
	 * msg
	 * @return
	 */
	String getSerializedMsg();

}
