package com.hui.zhang.common.util.performance.queue;

import com.hui.zhang.common.util.performance.model.ITask;
import com.hui.zhang.common.util.performance.model.MethodExecuteInfo;
import com.hui.zhang.common.util.performance.run.MethodExecuteProcessRunnable;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class TaskThreadPoolExecutor extends ThreadPoolExecutor {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskThreadPoolExecutor.class);

	/**
	 * 拒绝策略 处理逻辑
	 */
	private static TaskPersister taskPersister = new TaskPersister();

	/**
	 * 已提交任务数
	 */
	private final AtomicInteger submittedCount = new AtomicInteger(0);

	public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
								  TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
				new RejectHandler());
	}



	public TaskThreadPoolExecutor() {
		super(1, 5, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		submittedCount.decrementAndGet();
		super.afterExecute(r, t);
	}

	public int getSubmittedCount() {
		return submittedCount.get();
	}

	@Override
	public void execute(Runnable command) {
		execute(command, 0, TimeUnit.MILLISECONDS);
	}

	public int size() {
		return this.getQueue().size();
	}

	public void execute(Runnable command, long timeout, TimeUnit unit) {
		submittedCount.incrementAndGet();
		try {
			super.execute(command);
		} catch (RejectedExecutionException rx) {
			if (super.getQueue() instanceof TaskQueue) {
				final TaskQueue queue = (TaskQueue) super.getQueue();
				try {
					if (!queue.offer(command, timeout, unit)) {
						submittedCount.decrementAndGet();
						logger.warn("taskquere is full");
						return;
					}
				} catch (InterruptedException x) {
					submittedCount.decrementAndGet();
					Thread.interrupted();
					logger.warn("InterruptedException:" + x.getMessage());
				}
			} else {
				submittedCount.decrementAndGet();
				logger.warn("InterruptedException:" + rx.getMessage());
			}

		}
	}

	/**
	 * 任务被拒绝处理逻辑
	 */
	public static class RejectHandler implements RejectedExecutionHandler {

		public RejectHandler() {}

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			if (taskPersister != null && (r instanceof MethodExecuteProcessRunnable)) {
				MethodExecuteInfo methodExecuteInfo = ((MethodExecuteProcessRunnable) r).getMethodExecuteInfo();
				if (methodExecuteInfo != null) {
					ITask<?> task = methodExecuteInfo.getTask();
					taskPersister.persist(task);
				}
			}
		}
	}
}
