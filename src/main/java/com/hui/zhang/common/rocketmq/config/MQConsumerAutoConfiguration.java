package com.hui.zhang.common.rocketmq.config;


import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.hui.zhang.common.rocketmq.annotation.MQConsumer;
import com.hui.zhang.common.rocketmq.base.AbstractMQPushConsumer;
import com.hui.zhang.common.rocketmq.base.MessageExtConst;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by tian.luan
 * 自动装配消息消费者
 */
//@Slf4j
@Configuration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQConsumerAutoConfiguration extends MQBaseAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MQConsumerAutoConfiguration.class);

    private static final int consumeThreadMin = 64;
    private static final int consumeThreadMax = 128;

    @PostConstruct
    public void init() throws Exception {

        //add by zhanghui 是否使用mq
        boolean mqFlag=true;
        if (null!=PropertyUtil.getProperty("mq.flag")){
            mqFlag=Boolean.valueOf(PropertyUtil.getProperty("mq.flag").toString());
        }
        if (!mqFlag){//不使用mq的时候直接返回
            logger.info("不使用mq");
            return;
        }

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQConsumer.class);
        //对于只存在生产者的工程 不许构建消费者
        if(CollectionUtils.isEmpty(beans)){
            return;
        }
   /* if(!CollectionUtils.isEmpty(beans) && mqProperties.getTraceEnabled()) {
        initAsyncAppender();
    }*/
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            publishConsumer(entry.getKey(), entry.getValue());
        }


    }



    private void publishConsumer(String beanName, Object bean) throws Exception {
        MQConsumer mqConsumer = applicationContext.findAnnotationOnBean(beanName, MQConsumer.class);
        mqProperties.setNameServerAddress(AppConfigUtil.getCfgEnvironmentPO().getMqAddress());
        if (StringUtils.isEmpty(mqProperties.getNameServerAddress())) {
            throw new RuntimeException("name server address must be defined");
        }
        Assert.notNull(mqConsumer.consumerGroup(), "consumer's consumerGroup must be defined");
        Assert.notNull(mqConsumer.topic(), "consumer's topic must be defined");
        if (!AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
            throw new RuntimeException(bean.getClass().getName() + " - consumer未实现Consumer抽象类");
        }

        String consumerGroup = applicationContext.getEnvironment().getProperty(mqConsumer.consumerGroup());
        if (StringUtils.isEmpty(consumerGroup)) {
            consumerGroup = mqConsumer.consumerGroup();
        }
        String topic = applicationContext.getEnvironment().getProperty(mqConsumer.topic());
        if (StringUtils.isEmpty(topic)) {
            topic = mqConsumer.topic();
        }

        // 配置push consumer
        if (AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
            System.setProperty("rocketmq.client.log.loadconfig","true");
            Properties properties = new Properties();
            // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
            properties.put(PropertyKeyConst.AccessKey,RocketMqConfigBean.CLIENT_MAP.get("AccessKey"));
            // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
            properties.put(PropertyKeyConst.SecretKey, RocketMqConfigBean.CLIENT_MAP.get("Access"));
            //设置发送超时时间，单位毫秒
            properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "3000");
            // 设置 TCP 接入域名，进入控制台的实例管理页面的“获取接入点信息”区域查看
            properties.put(PropertyKeyConst.NAMESRV_ADDR,
                    RocketMqConfigBean.CLIENT_MAP.get("NAMESRV_ADDR"));
            properties.put(PropertyKeyConst.MessageModel, MessageModel.valueOf(mqConsumer.messageMode()));
            properties.put(PropertyKeyConst.GROUP_ID,this.getGroup(consumerGroup));
            Consumer consumer = ONSFactory.createConsumer(properties);
            AbstractMQPushConsumer abstractMQPushConsumer = (AbstractMQPushConsumer) bean;
            consumer.subscribe(this.getTopic(topic), StringUtils.join(mqConsumer.tag(), "||"),( Message message, ConsumeContext context) -> abstractMQPushConsumer.consume(message,context));
          /*  DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(mqProperties.getNameServerAddress());
            consumer.setMessageModel(MessageModel.valueOf(mqConsumer.messageMode()));
            consumer.subscribe(topic, StringUtils.join(mqConsumer.tag(), "||"));
            consumer.setInstanceName(UUID.randomUUID().toString());
            *//** 注意新主题从来没有消费过,producer如果先启动,consumer后启动，间隔时间内producer发出的消息默认是接不到的,需要如下设置 *//*
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeThreadMin(consumeThreadMin);
            consumer.setConsumeThreadMax(consumeThreadMax);
            consumer.setVipChannelEnabled(false);
            consumer.setConsumeMessageBatchMaxSize(150);*/

           /* if (MessageExtConst.CONSUME_MODE_CONCURRENTLY.equals(mqConsumer.consumeMode())) {
                consumer.registerMessageListener((List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) ->
                        abstractMQPushConsumer.dealMessage(list, consumeConcurrentlyContext));
            } else if (MessageExtConst.CONSUME_MODE_ORDERLY.equals(mqConsumer.consumeMode())) {
                consumer.registerMessageListener((List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) ->
                        abstractMQPushConsumer.dealMessage(list, consumeOrderlyContext));
            } else {
                throw new RuntimeException("unknown consume mode ! only support CONCURRENTLY and ORDERLY");
            }*/
            abstractMQPushConsumer.setConsumer(consumer);
            // 为Consumer增加消息轨迹回发模块
            consumer.start();
        }

        //logger.info(String.format("%s is ready to subscribe message,topic: %s ,tag : %s , group : %s ", bean.getClass().getName(),this.getTopic(topic),mqConsumer.tag().toString(),this.getGroup(consumerGroup)));
        logger.info("订阅消息 Customer:{},topic:{},tag:{},group:{}",bean.getClass().getName(),this.getTopic(topic), JsonEncoder.DEFAULT.encode(mqConsumer.tag()),this.getGroup(consumerGroup));
    }

    /**
     *
     * @param topic
     * @return
     */
    private String getTopic(String topic){
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        if (env.contains("PRD")){
            topic="prd_"+topic;
        }else if (env.contains("PRE")){
            topic="pre_"+topic;
        }
        else if(env.contains("QAS")){
            topic="qas_"+topic;
        }
        return topic;
    }
    private String getGroup(String group){
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        if (env.contains("PRD")){
            group="GID_prd_"+group;
        }else if (env.contains("PRE")){
            group="GID_pre_"+group;
        }
        else if(env.contains("QAS")){
            group="GID_qas_"+group;
        }
        return group;
    }

}