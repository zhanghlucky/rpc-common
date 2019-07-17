package com.hui.zhang.common.util.performance.interceptor;

import com.hui.zhang.common.util.logger.CentaurLogger;
import com.hui.zhang.common.util.logger.CentaurLoggerFactory;
import com.hui.zhang.common.util.performance.model.AbstractTask;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class PerformanceTask extends AbstractTask {

	static CentaurLogger logger = CentaurLoggerFactory.getLogger(PerformanceTask.class.getName());

	@Override
	protected Object doCall() throws Exception {
		return "";
	}

	private String type;

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getSerializedMsg() {
		return "";
	}

}
