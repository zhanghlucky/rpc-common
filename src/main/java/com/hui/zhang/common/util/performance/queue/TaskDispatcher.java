package com.hui.zhang.common.util.performance.queue;

import com.hui.zhang.common.util.performance.model.MethodExecuteInfo;
import com.hui.zhang.common.util.performance.run.MethodExecuteProcessRunnable;
import com.hui.zhang.common.util.performance.run.MethodGroupExecuteProcessRunnable;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class TaskDispatcher {

	/**
	 * 线程池
	 */
	private static TaskThreadPoolExecutor necessaryExecutorService = new TaskThreadPoolExecutor();

	private static volatile boolean commonThreadInited = false;

	private static Object commonThreadLock = new Object();

	private static Thread logThread = null;

	static {
		try {
			necessaryExecutorService = buildExecutor(
					100000,
					"local-queue-thread-");
			init();
		} catch (Exception e) {
			throw new RuntimeException("TaskDispatcher init fail", e);
		}
	}

	private static TaskThreadPoolExecutor buildExecutor(
			int queueSize, String threadPrefix) {

		TaskQueue workQueue = new TaskQueue(queueSize);

		ThreadFactory threadFactory = new CentaurThreadFactory(threadPrefix);

		TaskThreadPoolExecutor executor = new TaskThreadPoolExecutor(2,
				2, 60, TimeUnit.SECONDS, workQueue,
				threadFactory);
		return executor;
	}

	private static void init() {
		if (commonThreadInited) {
			return;
		}
		synchronized (commonThreadLock) {
			if (logThread == null) {
				logThread = new Thread(new MethodGroupExecuteProcessRunnable(), "local-queue-log-output-thread");
				logThread.setDaemon(true);
				logThread.start();
			}
			commonThreadInited = true;
		}
	}

	/**
	 * 单方法执行
	 * @param methodExecuteInfo
	 */
	public static void putMethodExecuteInfo(MethodExecuteInfo methodExecuteInfo) {
		MethodExecuteProcessRunnable methodExecuteProcessRunnable = new MethodExecuteProcessRunnable(methodExecuteInfo);
		necessaryExecutorService.execute(methodExecuteProcessRunnable);
	}
}
