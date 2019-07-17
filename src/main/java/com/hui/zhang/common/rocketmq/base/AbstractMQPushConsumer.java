package com.hui.zhang.common.rocketmq.base;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import com.hui.zhang.common.logger.EdbLogger;
import com.hui.zhang.common.logger.EdbLoggerFactory;
import com.hui.zhang.common.rocketmq.mongo.MessageBiz;
import com.hui.zhang.common.util.etc.AppParamsUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by tian.luan
 * RocketMQ的消费者(Push模式)处理消息的接口
 */
public abstract class AbstractMQPushConsumer<T> extends AbstractMQConsumer<T>  {
    private static final EdbLogger logger = EdbLoggerFactory.getLogger(AbstractMQPushConsumer.class);

    private Consumer consumer;

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    @Autowired
    private MessageBiz messageBiz;
    private static Gson gson = new Gson();


    public AbstractMQPushConsumer() {
    }

    /**
     * 继承这个方法处理消息
     *
     * @param message 消息范型
     * @param extMap  存放消息附加属性的map, map中的key存放在 @link MessageExtConst 中
     * @return 处理结果
     */
    public abstract boolean process(T message, Map<String, Object> extMap);

    /**
     * 原生dealMessage方法，可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @param list                       消息列表
     * @param consumeConcurrentlyContext 上下文
     * @return 消费状态
     */
/*    public ConsumeConcurrentlyStatus dealMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt messageExt : list) {
            logger.info("receive msgId: {}, tags : {}", messageExt.getMsgId(), messageExt.getTags());
            // parse message body
            T t = parseMessage(messageExt);
            // parse ext properties
            Map<String, Object> ext = parseExtParam(messageExt);
            if (null != t && !process(t, ext)) {
                int num = messageExt.getReconsumeTimes();
                logger.error("consume fail , ask for re-consume  msgId: {}, tags : {},重复察费次数：{}", messageExt.getMsgId(), messageExt.getTags(),num);
                if (num >= 3) {
                    logger.error("消息异常消费3次，防止消息挤压，丢弃此消息，receive msgId: {}, tags : {} ,存入mongodb:{}", messageExt.getMsgId(), messageExt.getTags());
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; // 丢弃消息 为了防止消息挤压，默认三次消费失败消息，删除消息
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER; // 重新放入队列
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }*/

    /**
     * 原生dealMessage方法，可以重写此方法自定义序列化和返回消费成功的相关逻辑
     *
     * @return 处理结果
     */
/*    public ConsumeOrderlyStatus dealMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
        for (MessageExt messageExt : list) {
            logger.info("receive msgId: {}, tags : {}", messageExt.getMsgId(), messageExt.getTags());
            T t = parseMessage(messageExt);
            Map<String, Object> ext = parseExtParam(messageExt);
            if (null != t && !process(t, ext)) {
                int num = messageExt.getReconsumeTimes();
                logger.error("consume fail , ask for re-consume , msgId: {},重复消费次数：{}", messageExt.getMsgId(),num);
                if (num  >= 3) {
                    logger.error("消息异常消费大于3次，防止消息挤压，丢弃此消息，receive msgId: {}, tags : {},存入mongodb:{}", messageExt.getMsgId(), messageExt.getTags());
                    //saveMessage(ext,t);
                    logger.error("消息存入mongo 成功");
                    return ConsumeOrderlyStatus.SUCCESS; // 丢弃消息
                }
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }*/


    public Action consume(Message message, ConsumeContext context) {
        /*logger.info(" aliyun receive msgId: {}, tags : {}", message.getMsgID(), message.getTag());
        T t = parseMessage(message);
        Map<String, Object> ext = parseExtParam(message);
        if (null != t && !process(t, ext)) {
            int num = message.getReconsumeTimes();
            logger.error("consume fail , ask for re-consume , msgId: {},重复消费次数：{}", message.getMsgID(),num);
            if (num  >= 3) {
                logger.error("消息异常消费大于3次，防止消息挤压，丢弃此消息，receive msgId: {}, tags : {},存入mongodb:{}", message.getMsgID(), message.getTag());
                //saveMessage(ext,t);
                logger.error("消息存入mongo 成功");
                return Action.ReconsumeLater; // 丢弃消息
            }

            return Action.CommitMessage;
        }
        return Action.CommitMessage;*/

        logger.info("receive msg msgId;{},topic:{},tag:{}", message.getMsgID(),message.getTopic(), message.getTag());
        /**
         * 限流开始
         * 限流 by zhanghui
         * 客户端限流 请在程序参数配置里配置 topic=限流数 例如 aa_topic=2000;
         * 表示 aa_topic 限流2000;不配置默认不限流。
         */
        int RCV_INTERVAL_TIME=0;
        String rcvIntervalTimeStr=AppParamsUtil.getParamValue(message.getTopic());
        if (StringUtils.isNotEmpty(rcvIntervalTimeStr)){
            try {
                RCV_INTERVAL_TIME=Integer.valueOf(rcvIntervalTimeStr);
            }catch (Exception e){
                logger.warn("程序参数配置的topic限流值错误:{}",message.getTopic());
            }
        }
        int rcvIntervalTimeLeft = RCV_INTERVAL_TIME;
        while (rcvIntervalTimeLeft > 0) {
            logger.debug("topic：{}，thread：{}，限流延迟：{}",message.getTopic(),Thread.currentThread().getId(),RCV_INTERVAL_TIME);
            if (rcvIntervalTimeLeft > RCV_INTERVAL_TIME) {
                rcvIntervalTimeLeft = RCV_INTERVAL_TIME;
            }
            try {
                if (rcvIntervalTimeLeft >= 100) {
                    rcvIntervalTimeLeft -= 100;
                    Thread.sleep(100);
                } else {
                    Thread.sleep(rcvIntervalTimeLeft);
                    rcvIntervalTimeLeft = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //--限流结束

        //消息队列 RocketMQ Subscribe interval logical ends
        T t = parseMessage(message);
        Map<String, Object> ext = parseExtParam(message);
        if (null != t && !process(t, ext)) {
            int num = message.getReconsumeTimes();
            logger.error("consume fail , ask for re-consume , msgId: {},重复消费次数：{}", message.getMsgID(),num);
            if (num  >= 3) {
                logger.error("消息异常消费大于3次,丢弃此消息 msgId;{},topic:{},tag:{}", message.getMsgID(),message.getTopic(), message.getTag());
                //saveMessage(ext,t);
                //logger.error("消息存入mongo 成功");
                return Action.ReconsumeLater; // 丢弃消息
            }

            return Action.CommitMessage;
        }
        return Action.CommitMessage;

    }

    /*public void saveMessage( Map<String, Object> ext,T t){
        MessageBean bean = new MessageBean();
        bean.setMessage(gson.toJson(t));
        bean.setMsgId(ext.get(MessageExtConst.PROPERTY_EXT_MSG_ID).toString());
        bean.setTopic(ext.get(MessageExtConst.PROPERTY_TOPIC).toString());
        bean.setTag(ext.get(MessageExtConst.PROPERTY_TAGS).toString());
        bean.setCreateTime(System.currentTimeMillis());
        this.messageBiz.saveMessage(bean);

    }*/
}
