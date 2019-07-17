package com.hui.zhang.common.rocketmq.config;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.hui.zhang.common.rocketmq.annotation.MQProducer;
import com.hui.zhang.common.rocketmq.base.AbstractMQProducer;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import com.aliyun.openservices.ons.api.Producer;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Properties;

/**
 * Created by tian.luan
 * 自动装配消息生产者
 */
//@Slf4j
@Configuration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQProducerAutoConfiguration extends MQBaseAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MQConsumerAutoConfiguration.class);

    @Setter
    private static Producer  producer;
  /*  *//**
     * 事务
     *//*
    @Setter
    private  static TransactionMQProducer transactionMQProducer;
*/
    @PostConstruct
    public void init() throws Exception {
        //add by zhanghui 是否使用mq
        boolean mqFlag=true;
        if (null!= PropertyUtil.getProperty("mq.flag")){
            mqFlag=Boolean.valueOf(PropertyUtil.getProperty("mq.flag").toString());
        }
        if (!mqFlag){//不使用mq的时候直接返回
            logger.info("不使用mq");
            return;
        }
        boolean isTransaction = false;
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQProducer.class);
        //对于仅仅只存在消息消费者的项目，无需构建生产者
        if(CollectionUtils.isEmpty(beans)){
            return;
        }
        /**
         * 判断是否有开启事物注解
         */
       /* for (Map.Entry<String, Object> entry : beans.entrySet()) {
            MQProducer mqProducer = applicationContext.findAnnotationOnBean(entry.getKey(), MQProducer.class);
            if (mqProducer.transaction()){
                isTransaction = true;
                break;
            }
        }*/
        if(producer == null) {
            System.setProperty("rocketmq.client.log.loadconfig","false");
           /* mqProperties.setNameServerAddress(AppConfigUtil.getCfgEnvironmentPO().getMqAddress());
            Assert.notNull(mqProperties.getProducerGroup(), "producer group must be defined");
            Assert.notNull(mqProperties.getNameServerAddress(), "name server address must be defined");
            producer = new DefaultMQProducer(mqProperties.getProducerGroup());
            producer.setNamesrvAddr(mqProperties.getNameServerAddress());
            producer.setSendMsgTimeout(mqProperties.getSendMsgTimeout());
            producer.setVipChannelEnabled(false);
            producer.start();*/
            Properties properties = new Properties();
            // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
            properties.put(PropertyKeyConst.AccessKey,RocketMqConfigBean.CLIENT_MAP.get("AccessKey"));
            // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
            properties.put(PropertyKeyConst.SecretKey, RocketMqConfigBean.CLIENT_MAP.get("Access"));
            //设置发送超时时间，单位毫秒
            properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, "50000");
            // 设置 TCP 接入域名，进入控制台的实例管理页面的“获取接入点信息”区域查看
            properties.put(PropertyKeyConst.NAMESRV_ADDR,
                    RocketMqConfigBean.CLIENT_MAP.get("NAMESRV_ADDR"));
            String group = this.getGroup(mqProperties.getProducerGroup());
            properties.put(PropertyKeyConst.GROUP_ID,group);
            producer = ONSFactory.createProducer(properties);
            // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可
            producer.start();
            logger.info(String.format("producer is Started. group:%s ,NAMESRV_ADDR: %s ,AccessKey: %s SecretKey: %s,",group, RocketMqConfigBean.CLIENT_MAP.get("NAMESRV_ADDR"),RocketMqConfigBean.CLIENT_MAP.get("AccessKey"),RocketMqConfigBean.CLIENT_MAP.get("Access")));
        }
       /* //TODO 事物消息暂定
        if(transactionMQProducer == null && isTransaction){
            Assert.notNull(mqProperties.getTransactionProducer(), "transactionProducer group must be defined");
            Assert.notNull(mqProperties.getNameServerAddress(), "name server address must be defined");
            transactionMQProducer =  new TransactionMQProducer(mqProperties.getTransactionProducer());
            transactionMQProducer.setNamesrvAddr(mqProperties.getNameServerAddress());
            transactionMQProducer.setTransactionCheckListener((MessageExt msg) ->{
                logger.info("事务回查机制！");
                return  LocalTransactionState.COMMIT_MESSAGE;
            });
            // 事务回查最小并发数
            transactionMQProducer.setCheckThreadPoolMinSize(2);
            // 事务回查最大并发数
            transactionMQProducer.setCheckThreadPoolMaxSize(5);
            // 队列数
            transactionMQProducer.setCheckRequestHoldMax(2000);
            transactionMQProducer.start();
            logger.info("TransactionMQProducer is Started.");
        }*/
        // register default mq producer to spring context
        registerBean(DefaultMQProducer.class.getName(), producer);
         //register TransactionMQProducer mq producer to spring context
    /*    registerBean(TransactionMQProducer.class.getName(), transactionMQProducer);*/

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            publishProducer(entry.getKey(), entry.getValue());
        }
    }

    private void publishProducer(String beanName, Object bean) throws Exception {
        if(!AbstractMQProducer.class.isAssignableFrom(bean.getClass())) {
            throw new RuntimeException(beanName + " - producer未继承AbstractMQProducer");
        }
        AbstractMQProducer abstractMQProducer = (AbstractMQProducer) bean;
        abstractMQProducer.setProducer(producer);
        // begin build producer level topic
        MQProducer mqProducer = applicationContext.findAnnotationOnBean(beanName, MQProducer.class);
        String topic = mqProducer.topic();
        topic = this.getTopic(topic);
        if(!StringUtils.isEmpty(topic)) {
            String transTopic = applicationContext.getEnvironment().getProperty(topic);
            if(StringUtils.isEmpty(transTopic)) {
                abstractMQProducer.setTopic(topic);
            } else {
                abstractMQProducer.setTopic(transTopic);
            }
        }
        // begin build producer level tag
        String tag = mqProducer.tag();
        if(!StringUtils.isEmpty(tag)) {
            String transTag = applicationContext.getEnvironment().getProperty(tag);
            if(StringUtils.isEmpty(transTag)) {
                abstractMQProducer.setTag(tag);
            } else {
                abstractMQProducer.setTag(transTag);
            }
        }
        logger.info(String.format("%s is ready to produce message,topic: %s ", beanName,topic));
    }
    private String getGroup(String group){
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        logger.info(String.format("rocketmq env : %s", env));
        if (env.contains("PRD") || env.contains("pre")){
            group="GID_prd_"+group;
        }else if (env.contains("PRE")){
            group="GID_pre_"+group;
        }
        else if(env.contains("QAS")){
            group="GID_qas_"+group;
        }
        return group;
    }
    private String getTopic(String topic){
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        if (env.contains("PRD") || env.contains("pre") ){
            topic="prd_"+topic;
        }else if (env.contains("PRE")){
            topic="pre_"+topic;
        }
        else if(env.contains("QAS")){
            topic="qas_"+topic;
        }
        return topic;
    }
}