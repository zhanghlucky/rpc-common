package com.hui.zhang.common.task.registry;

import com.hui.zhang.common.task.contsant.TaskConstant;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 * zookeeper  分布式锁
 * 实现思路： 1，创建临时节点，程序执行，判断这个临时节点是否存在，节点存在 返回false ， 节点不存在，创建节点，返回true，
 * 当程序挂掉，节点删除，锁释放，当线程处理结束，手动释放锁
 *
 * @author:jiangshun@centaur.cn
 * @create 2017-11-17 15:59
 **/
public class DistributedLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);
    private ZkClient zkClient;
    public static final String LOCK_ROOT = "/lock";
    private String lockName;
    private static String registryAddress = AppConfigUtil.getCfgEnvironmentPO().getZookeeperAddress().replace("zookeeper://", "").replace("?backup=", ",");// "192.168.3.53:2181";
    public static final DistributedLock INSTRANCE = new DistributedLock();

    public DistributedLock() {
        zkClient = new ZkClient(registryAddress, TaskConstant.ZK_SESSION_TIMEOUT, TaskConstant.ZK_SESSION_TIMEOUT);
    }

    public boolean getLock(String lockName) {
        if(!zkClient.exists(lockName)){
            zkClient.createEphemeral(lockName);
            return  true;
        }
      /*  else{
            LOGGER.info("此任务正在其他节点执行，跳过此调度，或看调度是否合理，任务名称：{}",lockName);
            return  false;
        }*/
      return  false;
    }

    private boolean createNode(String path, boolean ephemeral) {
        int i = path.lastIndexOf('/');
        if (i > 0) {//递归创建目录节点
            createNode(path.substring(0, i), false);
        }
        else{
            ephemeral = true;
        }
        if (ephemeral) {
            if(!zkClient.exists(path)){
                zkClient.createEphemeral(path);
                return  true;
            }
        }
        else{
            if(!zkClient.exists(path)){
                zkClient.createPersistent(path);
            }
        }
        return  false;
    }

    public void unLock(String lockName) {
       // String path = getNodePath(lockName);
        if (zkClient.exists(lockName)) {
            zkClient.delete(lockName);
            LOGGER.info("任务已分布式锁{}删除成功", lockName);
        }
    }

    private String getNodePath(String lockName) {
        return String.format("/lock/%s", lockName);
    }


}
