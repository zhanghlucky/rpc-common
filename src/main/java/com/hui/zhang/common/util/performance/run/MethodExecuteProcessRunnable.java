package com.hui.zhang.common.util.performance.run;

import com.hui.zhang.common.util.performance.model.DataControl;
import com.hui.zhang.common.util.performance.model.MethodExecuteInfo;
import com.hui.zhang.common.util.performance.model.MethodGroupExecuteInfo;
import org.slf4j.LoggerFactory;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class MethodExecuteProcessRunnable implements Runnable {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger("performance");

	private MethodExecuteInfo methodExecuteInfo;

	public MethodExecuteProcessRunnable(MethodExecuteInfo methodExecuteInfo) {
		this.methodExecuteInfo = methodExecuteInfo;
	}

	@Override
	public void run() {
		//如果正在打印则等待
		while (DataControl.PRINTING) {}

		String type = methodExecuteInfo.getTask().getType();

		synchronized (DataControl.LOCK) {
			MethodGroupExecuteInfo methodGroupExecuteInfo = DataControl.currentMethodGroupExecuteInfoMap.get(type);
			if (methodGroupExecuteInfo == null) {
				methodGroupExecuteInfo = new MethodGroupExecuteInfo();
			}
			methodGroupExecuteInfo.addMethodExecuteInfo(methodExecuteInfo);
			DataControl.currentMethodGroupExecuteInfoMap.put(type, methodGroupExecuteInfo);
		}

		logger.info("method:{} execute, startTime:{}, costTime:{}, hasException:{}, hasTimeout:{}, errorMsg:{}",
				methodExecuteInfo.getMethodName(), methodExecuteInfo.getStartTime(), methodExecuteInfo.getCostTime(),
				methodExecuteInfo.isHasException(), methodExecuteInfo.isHasTimeout(), methodExecuteInfo.getErrorMsg());

	}

	public MethodExecuteInfo getMethodExecuteInfo() {
		return methodExecuteInfo;
	}
}
