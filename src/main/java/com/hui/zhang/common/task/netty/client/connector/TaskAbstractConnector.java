package com.hui.zhang.common.task.netty.client.connector;

import io.netty.channel.Channel;

/**
 *  task连接服务器
 */
public abstract class TaskAbstractConnector {

	/**
	 * 连接服务器
	 * @return
	 */
	public abstract Channel connect(String host, int port) throws Exception;

}
