package com.hui.zhang.common.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by tian.luan
 * 轻量级多任务调度
 */
public class Clock {
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public static void set(Runnable task, long delay) {
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    public static void set(Runnable task, long delay, long interval) {
        if (interval > 0) {
            executor.scheduleWithFixedDelay(task, delay, interval, TimeUnit.MILLISECONDS);
        } else {
            executor.schedule(task, delay, TimeUnit.MILLISECONDS);
        }
    }
}