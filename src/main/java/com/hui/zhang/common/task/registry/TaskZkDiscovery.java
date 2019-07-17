package com.hui.zhang.common.task.registry;

import com.alibaba.fastjson.JSON;
import com.hui.zhang.common.task.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 重构task zk 服务，新增监控zookeeper 节点
 * @author:jiangshun@centaur.cn
 * @create 2018-09-04 15:26
 **/
public class TaskZkDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskZkDiscovery.class);
    public static final TaskZkDiscovery INSTANCE=new TaskZkDiscovery();
    //registName appName + taskName
    public String discover(String registName ) {
        String data = null;
        try{
            String dirPath = TaskZkManager.INSTANCE.getServiceTaskDiscovery(registName);
            List<String> nodeList= TaskZkManager.INSTANCE.client().getChildren().forPath(dirPath);
            if (nodeList.size() > 0){
                int size = nodeList.size();
                if (size == 1){
                    LOGGER.info("获取task 服务唯一节点：{}", nodeList.get(0));
                    data = nodeList.get(0);
                }
                else{
                    data = nodeList.get(ThreadLocalRandom.current().nextInt(size));
                    LOGGER.info("随机获取节点，节点信息:{}",data);
                }
            }
            else{
                LOGGER.error("无节点");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException("获取节点异常");
        }
        return data;
    }
    public List<String> infoDiscover(String registName ) {
        String dirPath = TaskZkManager.INSTANCE.getOnlineTaskServicePath(registName);
        try{
            List<String> nodeList= TaskZkManager.INSTANCE.client().getChildren().forPath(dirPath);
            LOGGER.info("获取所有服务节点，节点为{}", JSON.toJSONString(nodeList));
            return  nodeList;
        }
        catch (Exception e){
            e.printStackTrace();
            throw  new RuntimeException();
        }
    }
}
