package com.hui.zhang.common.task.registry;


import com.hui.zhang.common.task.data.Address;
import com.hui.zhang.common.task.data.TaskServiceMeta;
import com.hui.zhang.common.task.netty.client.connector.TaskConnector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.ConcurrentSet;
import io.netty.util.internal.ThreadLocalRandom;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zuti on 2018/2/27.
 * email zuti@centaur.cn
 */
public class RpcMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcMonitor.class);

	public static ConcurrentHashMap<Address, CopyOnWriteArrayList<Channel>> channelMap = new ConcurrentHashMap<>();

	/**
	 * 每个提供者的服务列表
	 */
	public static final ConcurrentMap<Address, ConcurrentSet<TaskServiceMeta>> providerServiceMap = new ConcurrentHashMap<>();

	/**
	 * 获取channel 重试次数
	 */
	private static final int RETRY_TIMES = 3;

	/**
	 * 服务提供者当前连接数
	 */
	public static final ConcurrentMap<Address, AtomicInteger> providerConnCount = new ConcurrentHashMap<>();

	/**
	 * 总服务列表
	 */
	public static final ConcurrentSet<TaskServiceMeta> serviceMetas = new ConcurrentSet<>();

	/**
	 * 最大连接数
	 */
	private static final int MAX_CONN_COUNT = 3;

	/**
	 * 保存channel
	 * @param address
	 * @param channel
	 */
	public static void addChannel(Address address, Channel channel) {
		CopyOnWriteArrayList<Channel> channels = channelMap.get(address);
		if (channels == null) {
			CopyOnWriteArrayList<Channel> newChannels = new CopyOnWriteArrayList<>();
			channels = channelMap.putIfAbsent(address, newChannels);
			if (channels == null) {
				channels = newChannels;
			}
		}
		channels.add(channel);
		channel.closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				removeChannel(address, channel);
			}
		});
	}

	/**
	 * 获取channel
	 * @param address
	 * @return
	 */
	public static Channel getChannel(Address address) {
		Channel channel = null;
		CopyOnWriteArrayList<Channel> channels = channelMap.get(address);

		if (channels != null && CollectionUtils.isNotEmpty(channels)) {

			int retryTime = 1;
			while (retryTime <= RETRY_TIMES) {
				try {
					int size = channels.size();
					if(size==1){
						channel = channels.get(0);
					}else{
  						channel = channels.get(ThreadLocalRandom.current().nextInt(size));
					}
					return channel;
				} catch (Exception e) {
					LOGGER.error("获取可用channel异常, 重试次数：{}, {}", retryTime, e);
				}

				retryTime++;
			}

		}
		if (channel == null) {
			// 创建链接
			channel = new TaskConnector().connect(address.getHost(), address.getPort());
			RpcMonitor.addChannel(address, channel);
			return channel;
		}
		return channel;
	}

	/**
	 * 删除channel
	 * @param address
	 * @param channel
	 */
	public static void removeChannel(Address address, Channel channel) {
		CopyOnWriteArrayList<Channel> channels = channelMap.get(address);
		if (CollectionUtils.isNotEmpty(channels)) {
			Iterator<Channel> iterator = channels.iterator();
			while (iterator.hasNext()) {
				Channel channel1 = iterator.next();
				if (channel1.equals(channel)) {
					channels.remove(channel1);
					break;
				}
			}
		}
		providerConnCount.get(address).decrementAndGet();
	}

	public static void removeChannel(Address address) {
		CopyOnWriteArrayList<Channel> channels = channelMap.get(address);
		if (CollectionUtils.isNotEmpty(channels)) {
			Iterator<Channel> iterator = channels.iterator();
			while (iterator.hasNext()) {
				Channel channel1 = iterator.next();
				channels.remove(channel1);
			}
		}
		providerConnCount.put(address, new AtomicInteger(0));
	}


	/**
	 * 添加Task
	 */
	public synchronized static void addProvider(Address address, TaskServiceMeta serviceMeta) {

		ConcurrentSet<TaskServiceMeta> spiderServiceMetas = providerServiceMap.get(address);

		if (!CollectionUtils.isNotEmpty(spiderServiceMetas)) {
			ConcurrentSet<TaskServiceMeta> newPpiderServiceMetas = new ConcurrentSet<>();
			spiderServiceMetas = providerServiceMap.putIfAbsent(address, newPpiderServiceMetas);
			if (spiderServiceMetas == null) {
				spiderServiceMetas = newPpiderServiceMetas;
			}

			AtomicInteger connCount = providerConnCount.get(address);
			if (connCount == null) {
				AtomicInteger newConnCount = new AtomicInteger(0);
				connCount = providerConnCount.putIfAbsent(address, newConnCount);
				if (connCount == null) {
					connCount = newConnCount;
				}
			}

			int count = MAX_CONN_COUNT - connCount.get();
			for (int i = 0; i < count; i++) {
				Channel channle = new TaskConnector().connect(address.getHost(), address.getPort());
				RpcMonitor.addChannel(address, channle);
				connCount.getAndIncrement();
			}

		}
		spiderServiceMetas.add(serviceMeta);

	}

	/**
	 *  删除提供者
	 * @param address
	 * @param serviceMeta
	 */
	public synchronized static void removeProvider(Address address, TaskServiceMeta serviceMeta) {
		ConcurrentSet<TaskServiceMeta> spiderServiceMetas = providerServiceMap.get(address);
		boolean flag = false;
		if (spiderServiceMetas == null) {
			flag = true;
		} else {
			spiderServiceMetas.remove(serviceMeta);
			if (spiderServiceMetas.size() == 0) {
				flag = true;
			}
		}
		if (flag) {
			removeChannel(address);
		}
	}

	/**
	 * 添加服务
	 * @param serviceMeta
	 */
	public static void addService(TaskServiceMeta serviceMeta) {
		serviceMetas.add(serviceMeta);
	}

	/**
	 * 删除服务
	 * @param serviceMeta
	 */
	public static void remove(TaskServiceMeta serviceMeta) {
		serviceMetas.remove(serviceMeta);
	}


}
