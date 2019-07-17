package com.hui.zhang.common.util.performance.queue;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 圣特尔线程工厂
 */
public class CentaurThreadFactory implements ThreadFactory {

	private final ThreadGroup threadGroup;

	private final AtomicInteger threadNumber = new AtomicInteger();

	private String namePrefix;

	public CentaurThreadFactory() {
		this("localtask-thread-");
	}

	public CentaurThreadFactory(final String namePrefix) {
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = namePrefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(threadGroup, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}
