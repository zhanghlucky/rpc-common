package com.hui.zhang.common.util.performance.queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 任务队列
 */
public class TaskQueue extends LinkedBlockingQueue<Runnable> {

	public TaskQueue() {
		super();
	}

	public TaskQueue(int maxQueueSize) {
		super(maxQueueSize);
	}

}
