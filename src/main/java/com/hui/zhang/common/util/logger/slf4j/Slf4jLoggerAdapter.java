package com.hui.zhang.common.util.logger.slf4j;

import com.hui.zhang.common.util.logger.CentaurLogger;
import com.hui.zhang.common.util.logger.LoggerAdapter;

/**
 * Created by zuti on 2017/11/22.
 * email zuti@centaur.cn
 */
public class Slf4jLoggerAdapter implements LoggerAdapter {

	@Override
	public CentaurLogger getLogger(Class<?> key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
	}

	@Override
	public CentaurLogger getLogger(String key) {
		return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
	}
}
