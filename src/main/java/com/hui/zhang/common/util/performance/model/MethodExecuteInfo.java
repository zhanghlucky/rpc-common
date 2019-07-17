package com.hui.zhang.common.util.performance.model;

import java.io.Serializable;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 方法执行信息
 */
public class MethodExecuteInfo implements Serializable {

	private static final long serialVersionUID = 7470460781607986780L;

	/**
	 * 方法名称
	 */
	private String methodName;
	/**
	 * 开始时间
	 */
	private long startTime;

	/**
	 * 消耗时间
	 */
	private long costTime;

	/**
	 * 关联任务
	 */
	private ITask<?> task;

	/**
	 * 是否发生异常
	 */
	private boolean hasException;

	/**
	 * 是否超时
	 */
	private boolean hasTimeout;

	/**
	 * 错误msg
	 */
	private String errorMsg;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getCostTime() {
		return costTime;
	}

	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}

	public ITask<?> getTask() {
		return task;
	}

	public void setTask(ITask<?> task) {
		this.task = task;
	}

	public boolean isHasException() {
		return hasException;
	}

	public void setHasException(boolean hasException) {
		this.hasException = hasException;
	}

	public boolean isHasTimeout() {
		return hasTimeout;
	}

	public void setHasTimeout(boolean hasTimeout) {
		this.hasTimeout = hasTimeout;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
