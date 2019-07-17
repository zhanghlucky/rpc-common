package com.hui.zhang.common.util.logger;

/**
 * Created by zuti on 2017/11/22.
 * email zuti@centaur.cn
 */
public interface LoggerAdapter {

	/**
	 * 获取日志输出器
	 *
	 * @param key 分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	CentaurLogger getLogger(Class<?> key);

	/**
	 * 获取日志输出器
	 *
	 * @param key 分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	CentaurLogger getLogger(String key);

}
