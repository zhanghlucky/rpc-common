package com.hui.zhang.common.datasource.mybatis.spring;

import com.hui.zhang.common.rocketmq.annotation.MQConsumer;
import com.hui.zhang.common.rocketmq.base.AbstractMQPushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ${DESCRIPTION}
 *  广播消息模式
 * @author:jiangshun@centaur.cn
 * @create 2018-01-17 17:13
 **/
@Component
@MQConsumer(topic = "mysqlreload", consumerGroup = "mysqlreload" + "_" + "datasource", tag = {"datasource"}, messageMode = "BROADCASTING")
public class DsReloaderMessage extends AbstractMQPushConsumer<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DsReloaderMessage.class);
    @Override
    public boolean process(String message, Map<String, Object> extMap) {
        try{
            if (MyBatisConfig.shardDsNames.contains(message)){
                LOGGER.info("数据库连接池正在重置,切换数据库为：{}",message);
                DsReloader.INSTANCE.reloadDs();
                LOGGER.info("数据库连接池重置完成,切换数据库为：{}",message);
            }
        }catch (Exception e){
            LOGGER.error("数据源连接池重置失败,失败原因{},切换数据库key：{}",e.getMessage(),message);
            e.printStackTrace();
        }
        return false;
    }
}
