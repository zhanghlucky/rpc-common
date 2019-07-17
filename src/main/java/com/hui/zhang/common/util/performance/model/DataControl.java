package com.hui.zhang.common.util.performance.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class DataControl {

	/**
	 * 当前正在计算的 分组合并后的执行信息
	 */
	public static Map<String, MethodGroupExecuteInfo> currentMethodGroupExecuteInfoMap = new TreeMap<String, MethodGroupExecuteInfo>();

	/** 锁对象 */
	public static Object LOCK = new Object();

	/** 是否正在转化信息打印log */
	public static volatile boolean PRINTING = false;

	/** 是否打开task执行性能的log 默认false */
	public static boolean OPEN_TASK_PERFORMANCE_LOG = true;

	/** 每隔 ? 毫秒打印一次 默认60秒 */
	public static long PRINT_LOG_TIME_SLICE = 60 * 1000;

	/** task性能log 在log4j中的logger名字  */
	public static String TASK_PERFORMANCE_LOGGER_NAME = "performanceLog";

	/** 记录个数，用于每隔一段时间 打一个执行时间的log */
	public static AtomicInteger EXEC_COUNT = new AtomicInteger(0);

	private DataControl(){}

}
