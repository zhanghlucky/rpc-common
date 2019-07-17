package com.hui.zhang.common.task.registry;

import com.hui.zhang.common.task.contsant.TaskConstant;
import com.hui.zhang.common.task.data.Address;
import com.hui.zhang.common.task.data.TaskServiceMeta;
import com.hui.zhang.common.util.MD5Util;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *  重构task服务，替换zk 包
 * @author:jiangshun@centaur.cn
 * @create 2018-09-04 14:06
 **/
public class TaskZkManager {
    private static final Logger logger = LoggerFactory.getLogger(TaskZkManager.class);
    public static final TaskZkManager INSTANCE=new TaskZkManager();
    private final int sessionTimeoutMs = 3 * 1000;
    private final int connectionTimeoutMs = 30 * 1000;
    private static final Map<String,CuratorFramework> CLIENT_MAP=new HashMap<>();
    private static CuratorFramework client;
    static{
        try {
            String zkAddress = AppConfigUtil.getCfgEnvironmentPO().getZookeeperAddress().replace("zookeeper://", "").replace("?backup=", ",");
            client=TaskZkManager.INSTANCE.connect(zkAddress);
            logger.info("curator zookeeper connect success!");
        } catch (Exception e) {
            logger.error("curator zookeeper connect fail!", e);
        }
    }
    /**
     * 连接zookeeper
     * @param zkAddress
     */
    public CuratorFramework connect(String zkAddress) {
        /*if (null!=client){
            try {
                client.close();
            }catch (Exception e){
                logger.error("client close error:{}",e.getMessage());
            }
        }*/
        String key= MD5Util.MD5(zkAddress);
        CuratorFramework client=CLIENT_MAP.get(key);
        if (null!=client){
            return client;
        }

        client = CuratorFrameworkFactory.newClient(
                zkAddress, sessionTimeoutMs, connectionTimeoutMs, new ExponentialBackoffRetry(1000, 20));

        client.getConnectionStateListenable().addListener((clt, newState) -> {

            logger.info("[TaskZk] Zookeeper connection state changed {}.", newState);

            if (newState == ConnectionState.LOST) {
                logger.info("[TaskZk] lost connection with zookeeper");
            } else if (newState == ConnectionState.CONNECTED) {
                //连接新建
                logger.info("[TaskZk] connected with zookeeper");
            } else if (newState == ConnectionState.RECONNECTED) {
                //重连成功
                logger.info("[TaskZk] reconnected with zookeeper");
            }

        });
        client.start();
        CLIENT_MAP.put(key,client);
        return client;

    }
    public CuratorFramework client() {
        return client;
    }

    public CuratorFramework client(String zkAddress) {
        client=connect(zkAddress);
        return client;
    }
    /**
     *
     * 创建节点
     * @param path 路径
     * @param ephemeral 是否临时节点
     */
    public void createNode(String path, boolean ephemeral) throws Exception {
        int i = path.lastIndexOf('/');
        if (i > 0) {//递归创建目录节点
            createNode(path.substring(0, i), false);
        }
        if (ephemeral) {
            Stat stat = client.checkExists().forPath(path);
            if(stat == null){
                client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
            }
        } else {
            Stat stat = client.checkExists().forPath(path);
            if (stat == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(path);
            }
        }
    }
    public String getOnlineTaskServicePath(String registryName,String appAddress) {
        return String.format("/task/info/%s/%s", registryName, appAddress);
    }
    public String getServiceTaskServicePath(String registryName,String appAddress) {
       return  TaskConstant.ZK_REGISTRY_PATH + "/" + registryName + TaskConstant.ZK_DATA_PATH + "/" + appAddress;
    }
    public String getServiceTaskDiscovery(String registryName){
       return TaskConstant.ZK_REGISTRY_PATH + "/" + registryName + TaskConstant.ZK_DATA_PATH;
    }
    public String getOnlineTaskServicePath(String registryName) {
        return String.format("/task/info/%s", registryName);
    }

    /**
     * 抽取提供者
     * @param path
     * @return
     */
    public static Address parseProvider(String path) {
        String address = null;
        String[] directorys = path.split("/");
        if (directorys.length == 5) {
            address = directorys[4];
        }
        if (StringUtils.isNotBlank(address)) {
            String host = address.split(":")[0];
            String port = address.split(":")[1];
            return Address.of(host, Integer.valueOf(port));
        } else {
            return Address.EMPTY;
        }
    }

    /**
     * 抽取服务
     * @param path
     * @return
     */
    public static TaskServiceMeta parseService(String path) {
        TaskServiceMeta meta;
        String[] directorys = path.split("/");
        String metaDire = directorys[2];
        String[] service_version = metaDire.split(":");
        if (service_version.length > 1) {
            meta = new TaskServiceMeta(service_version[0], service_version[1]);
        } else {
            meta = new TaskServiceMeta(service_version[0], StringUtils.EMPTY);
        }
        return meta;
    }

}
