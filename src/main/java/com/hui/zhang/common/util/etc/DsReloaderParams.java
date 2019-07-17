package com.hui.zhang.common.util.etc;

import com.hui.zhang.common.datasource.mybatis.spring.DsReloader;
import com.hui.zhang.common.datasource.mybatis.spring.MyBatisConfig;
import com.hui.zhang.common.rocketmq.annotation.MQConsumer;
import com.hui.zhang.common.rocketmq.base.AbstractMQPushConsumer;
import com.hui.zhang.common.util.PropertyUtil;
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
@MQConsumer(topic = "AppParamsreload", consumerGroup = "appParamsreload" + "_" + "appParams", tag = {"appParams"}, messageMode = "BROADCASTING")
public class DsReloaderParams extends AbstractMQPushConsumer<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DsReloaderParams.class);
    @Override
    public boolean process(String message, Map<String, Object> extMap) {
        try{
            if (((String)PropertyUtil.getProperty("app.name")).contains(message)){
                LOGGER.info("配置中心动态数据正在重新load,message：{}",message);
                AppConfigUtil.clearAppParam(); // 先清空
                LOGGER.info("配置中心动态数据正在清空,message：{}",message);
                AppConfigUtil.initAppParams(); // 重新加载
                LOGGER.info("配置中心动态数据重置完成,message：{}",message);
            }
        }catch (Exception e){
            LOGGER.error("配置中心动态数据重置失败,失败原因{},切换key：{}",e.getMessage(),message);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
