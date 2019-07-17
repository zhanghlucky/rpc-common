package com.hui.zhang.common.util.performance.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 * 方法分组执行信息
 */
public class MethodGroupExecuteInfo implements Serializable {

	private static final long serialVersionUID = -2939675262572899858L;

	/** 名字 */
	private String name;

	/** 最大值 */
	private long max;

	/** 最小值 */
	private long min;

	/** 数量 */
	private int count;

	/** 平均值 */
	private double avg;

	/** 异常个数 */
	private int exceptionCount;

	/** 超时个数 */
	private int timeoutCount;

	/** 错误的信息 */
	private Map<String, Integer> errorMsgs = new ConcurrentHashMap<String, Integer>();


	public void addMethodExecuteInfo(MethodExecuteInfo methodExecuteInfo) {

		long costTime = methodExecuteInfo.getCostTime();
		count++;

		if(methodExecuteInfo.isHasException()){
			exceptionCount++;
		}
		if(methodExecuteInfo.isHasTimeout()){
			timeoutCount++;
		}

		if(StringUtils.isNotBlank(methodExecuteInfo.getErrorMsg())){
			String msg = methodExecuteInfo.getErrorMsg();
			Integer c = errorMsgs.get(msg);
			if (c == null) {
				errorMsgs.put(msg, 1);
			} else {
				errorMsgs.put(msg, ++c);
			}
		}

		double diffFromAvg = costTime - avg;
		avg = avg + (diffFromAvg / count);

		if (count == 1) {
			min = costTime;
			max = costTime;
		} else {
			if (costTime < min) {
				min = costTime;
			}
			if (costTime > max) {
				max = costTime;
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public int getExceptionCount() {
		return exceptionCount;
	}

	public void setExceptionCount(int exceptionCount) {
		this.exceptionCount = exceptionCount;
	}

	public int getTimeoutCount() {
		return timeoutCount;
	}

	public void setTimeoutCount(int timeoutCount) {
		this.timeoutCount = timeoutCount;
	}

	public Map<String, Integer> getErrorMsgs() {
		return errorMsgs;
	}

	public void setErrorMsgs(Map<String, Integer> errorMsgs) {
		this.errorMsgs = errorMsgs;
	}
}
