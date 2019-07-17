package com.hui.zhang.common.util.performance.run;

import com.hui.zhang.common.util.performance.CommonUtils;
import com.hui.zhang.common.util.performance.model.DataControl;
import com.hui.zhang.common.util.performance.model.MethodGroupExecuteInfo;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class MethodGroupExecuteProcessRunnable implements Runnable {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger("performance");

	/** 上一次打印的时间 */
	private static long LAST_STATISTICS_TIME;

	/** 默认的首列长度 */
	private static final int DEFAULT_NAME_LEN = 20;

	/** 系统换行符 */
	private static final String NEWLINE = System.getProperty("line.separator");

	@Override
	public void run() {
		//从整点数开始，把零头去掉
		LAST_STATISTICS_TIME = System.currentTimeMillis() / DataControl.PRINT_LOG_TIME_SLICE * DataControl.PRINT_LOG_TIME_SLICE;

		try {
			while (true) {
				long interval = System.currentTimeMillis() - LAST_STATISTICS_TIME;
				if (interval < DataControl.PRINT_LOG_TIME_SLICE) {
					Thread.sleep(1000);
					continue;
				}
				DataControl.PRINTING = true;
				long startTime = LAST_STATISTICS_TIME;
				LAST_STATISTICS_TIME += DataControl.PRINT_LOG_TIME_SLICE;
				Map<String, MethodGroupExecuteInfo> methodGroupedNodeInfoMap = null;
				synchronized (DataControl.LOCK){
					methodGroupedNodeInfoMap = DataControl.currentMethodGroupExecuteInfoMap;
					DataControl.currentMethodGroupExecuteInfoMap = new TreeMap<String, MethodGroupExecuteInfo>();
				}
				DataControl.PRINTING = false;
				logger.info(generateString(startTime, methodGroupedNodeInfoMap));

			}
		} catch (Exception e) {
			logger.error(MethodGroupExecuteProcessRunnable.class.getName() + " run error: "+e.getMessage());
		}
	}

	private String generateString(long startTime, Map<String, MethodGroupExecuteInfo> methodGroupExecuteInfoMap) {
		StringBuilder returnValue = new StringBuilder();

		int maxKeyLen = Math.max(CommonUtils.getLongestStrLen(methodGroupExecuteInfoMap.keySet()), DEFAULT_NAME_LEN);

		returnValue.append("Task PerfInfo   ")
				.append(CommonUtils.formatDate(startTime))
				.append(" - ")
				.append(CommonUtils.formatDate(startTime + DataControl.PRINT_LOG_TIME_SLICE))
				.append(NEWLINE);

		returnValue.append(String.format("%-" + maxKeyLen + "s%12s%12s%12s%12s%n",
				"task.type", "Avg(ms)", "Min", "Max", "Count"));

		for (Map.Entry<String, MethodGroupExecuteInfo> entry : methodGroupExecuteInfoMap.entrySet()) {
			String name = entry.getKey();
			MethodGroupExecuteInfo methodGroupExecuteInfo = entry.getValue();
			returnValue.append(String.format("%-" + maxKeyLen + "s%12.1f%12d%12d%12d%n",
					name,
					methodGroupExecuteInfo.getAvg(),
					methodGroupExecuteInfo.getMin(),
					methodGroupExecuteInfo.getMax(),
					methodGroupExecuteInfo.getCount()));
		}

		return returnValue.toString();

	}

}
