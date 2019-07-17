package com.hui.zhang.common.util.logger;

import com.hui.zhang.common.util.logger.slf4j.Slf4jLoggerAdapter;

/**
 * Created by zuti on 2017/11/22.
 * email zuti@centaur.cn
 */
public class CentaurLoggerFactory {

	private CentaurLoggerFactory(){}
	private static volatile LoggerAdapter LOGGER_ADAPTER;

	static {
		/**
		 * 使用slf4j
		 */
		LOGGER_ADAPTER = new Slf4jLoggerAdapter();
	}

	/**
	 * 获取日志输出器
	 *
	 * @param key 分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	public static CentaurLogger getLogger(Class<?> key) {
		return LOGGER_ADAPTER.getLogger(key);
	}

	/**
	 * 获取日志输出器
	 *
	 * @param key 分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	public static CentaurLogger getLogger(String key) {
		return LOGGER_ADAPTER.getLogger(key);
	}

}
