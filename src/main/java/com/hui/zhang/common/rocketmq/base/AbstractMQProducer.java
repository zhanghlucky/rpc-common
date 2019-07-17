package com.hui.zhang.common.rocketmq.base;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import com.hui.zhang.common.rocketmq.annotation.MQKey;
import com.hui.zhang.common.rocketmq.error.MQException;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * Created by tian.luan
 * RocketMQ的生产者的抽象基类
 */
public abstract class AbstractMQProducer {
    private static final Logger log = LoggerFactory.getLogger(AbstractMQProducer.class);
    private static final String[] DELAY_ARRAY = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h".split(" ");

    private static Gson gson = new Gson();

    private MessageQueueSelector messageQueueSelector = new SelectMessageQueueByHash();

    public AbstractMQProducer() {
    }

    private String tag;

    /**
     * 重写此方法,或者通过setter方法注入tag设置producer bean 级别的tag
     *
     * @return tag
     */
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    /*public TransactionMQProducer getTransactionMQProducer() {
        return transactionMQProducer;
    }

    public void setTransactionMQProducer(TransactionMQProducer transactionMQProducer) {
        this.transactionMQProducer = transactionMQProducer;
    }*/

    private Producer producer;
   /* private TransactionMQProducer transactionMQProducer;*/

    private String topic;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 重写此方法定义bean级别的topic，如果有返回有效topic，可以直接使用 sendMessage() 方法发送消息
     *
     * @return topic
     */
    public String getTopic() {
        return this.topic;
    }

    private Message genMessage(String topic, String tag, Object msgObj) {
        String messageKey= "";
        try {
            Field[] fields = msgObj.getClass().getDeclaredFields();
            for (Field field : fields) {
                Annotation[] allFAnnos= field.getAnnotations();
                if(allFAnnos.length > 0) {
                    for (int i = 0; i < allFAnnos.length; i++) {
                        if(allFAnnos[i].annotationType().equals(MQKey.class)) {
                            field.setAccessible(true);
                            MQKey mqKey = MQKey.class.cast(allFAnnos[i]);
                            messageKey = StringUtils.isEmpty(mqKey.prefix()) ? field.get(msgObj).toString() : (mqKey.prefix() + field.get(msgObj).toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("parse key error : {}" , e.getMessage());
        }
        String str = gson.toJson(msgObj);
        if(StringUtils.isEmpty(topic)) {
            if(StringUtils.isEmpty(getTopic())) {
                throw new RuntimeException("no topic defined to send this message");
            }
            topic = getTopic();
        }
        Message message = null;
        if (!StringUtils.isEmpty(tag)) {
           message =  new Message(topic,tag, str.getBytes(Charset.forName("utf-8")));
        } else if (!StringUtils.isEmpty(getTag())) {
            message =  new Message(topic,getTag(), str.getBytes(Charset.forName("utf-8")));
        }
      /*  if(!StringUtils.isEmpty(messageKey)) {
            message.setKeys(messageKey);
        }*/
        return message;
    }
    private Message genMessageByWeb(String topic, String tag, String str) {
        String messageKey= "";
        try {
            Field[] fields = str.getClass().getDeclaredFields();
            for (Field field : fields) {
                Annotation[] allFAnnos= field.getAnnotations();
                if(allFAnnos.length > 0) {
                    for (int i = 0; i < allFAnnos.length; i++) {
                        if(allFAnnos[i].annotationType().equals(MQKey.class)) {
                            field.setAccessible(true);
                            MQKey mqKey = MQKey.class.cast(allFAnnos[i]);
                            messageKey = StringUtils.isEmpty(mqKey.prefix()) ? field.get(str).toString() : (mqKey.prefix() + field.get(str).toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("parse key error : {}" , e.getMessage());
        }
        if(StringUtils.isEmpty(topic)) {
            if(StringUtils.isEmpty(getTopic())) {
                throw new RuntimeException("no topic defined to send this message");
            }
            topic = getTopic();
        }
        Message message = null;
        if (!StringUtils.isEmpty(tag)) {
            message =  new Message(topic,tag, str.getBytes(Charset.forName("utf-8")));
        } else if (!StringUtils.isEmpty(getTag())) {
            message =  new Message(topic,getTag(), str.getBytes(Charset.forName("utf-8")));
        }
        return message;
    }

    /**
     * fire and forget 不关心消息是否送达，可以提高发送tps
     *
     * @deprecated may cause message lost because of ignore error
     * @param topic topic
     * @param tag tag
     * @param msgObj 消息体
     * @throws MQException 消息异常
     */
    public void sendOneWay(String topic, String tag, Object msgObj) throws MQException {
        try {
            if(null == msgObj) {
                return;
            }
            producer.sendOneway(genMessage(topic, tag, msgObj));
            log.info("send onway message success : {}", msgObj);
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, e {}", topic, e);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e);
        }
    }

    /**
     * fire and forget 不关心消息是否送达，可以提高发送tps
     *
     * @deprecated may cause message lost because of ignore error
     * @param msgObj 消息体
     * @throws MQException 消息异常
     */
    public void sendOneWay(Object msgObj) throws MQException {
        sendOneWay("", "", msgObj);
    }

    /**
     * fire and forget 不关心消息是否送达，可以提高发送tps
     *
     * @deprecated may cause message lost because of ignore error
     * @param tag tag
     * @param msgObj 消息体
     * @throws MQException 消息异常
     */
    public void sendOneWay(String tag, Object msgObj) throws MQException {
        sendOneWay("", tag, msgObj);
    }


    /**
     * 可以保证同一个queue有序
     *
     * @deprecated may cause message lost because of ignore error
     * @param topic topic
     * @param tag tag
     * @param msgObj 消息体
     * @param hashKey 用于hash后选择queue的key
     *//*
    public void sendOneWayOrderly(String topic, String tag, Object msgObj, String hashKey) {
        if(null == msgObj) {
            return;
        }
        if(StringUtils.isEmpty(hashKey)) {
            // fall back to normal
            sendOneWay(topic, tag, msgObj);
        }
        try {
            producer.send(genMessage(topic, tag, msgObj), messageQueueSelector, hashKey);
            log.info("send onway message orderly success : {}", msgObj);
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("顺序消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }*/

    /**
     * 同步发送消息
     * @param topic  topic
     * @param tag tag
     * @param msgObj  消息体
     * @throws MQException 消息异常
     */
    public void syncSend(String topic, String tag, Object msgObj) throws MQException {
        try {
            if(null == msgObj) {
                return;
            }
            SendResult sendResult = producer.send(genMessage(topic, tag, msgObj));
            log.info("send rocketmq message ,messageId : {}", sendResult.getMessageId());
            this.doAfterSyncSend(sendResult);
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }
    /**
     * 同步发送消息
     * @param topic  topic
     * @param tag tag
     * @param msgObj  消息体
     * @throws MQException 消息异常
     */
    public String syncSendMessage(String topic, String tag, Object msgObj) throws MQException {
        try {
            if(null == msgObj) {
                return null;
            }
            SendResult sendResult = producer.send(genMessage(topic, tag, msgObj));
            log.info("send rocketmq message ,messageId : {}", sendResult.getMessageId());
            //this.doAfterSyncSend(sendResult);
            return  sendResult.getMessageId();
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }
    /**
     * 同步发送消息
     * @param topic  topic
     * @param tag tag
     * @param msgObj  消息体
     * @throws MQException 消息异常
     */
    public void syncSendByWeb(String topic, String tag, String msgObj) throws MQException {
        try {
            if(null == msgObj) {
                return;
            }
            SendResult sendResult = producer.send(genMessageByWeb(topic, tag, msgObj));
            log.info("send rocketmq message ,messageId : {}", sendResult.getMessageId());
            this.doAfterSyncSend(sendResult);
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }



    /**
     * 同步发送消息
     * @param tag tag
     * @param msgObj  消息体
     * @param delayTimeLevel  默认延迟等级 : 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h， 传入1代表1s, 2代表5s, 以此类推
     * @throws MQException 消息异常
     * TODO 如果要支持任意的时间精度，在Broker层面，必须要做消息排序，如果再涉及到持久化，那么消息排序要不可避免的产生巨大性能开销。 不建议
     */
    public void syncSendWithDelay(String tag, Object msgObj, int delayTimeLevel) throws MQException {
        try {
            if(null == msgObj) {
                return;
            }
            Message delayedMsg = genMessage(topic, tag, msgObj);
            if(delayTimeLevel > 0 && delayTimeLevel <= DELAY_ARRAY.length) {
                delayedMsg.setStartDeliverTime(delayTimeLevel);
            }
            SendResult sendResult = producer.send(delayedMsg);
            log.info("sync send rocketmq message with delay, messageId : {}, default delay interval: {}", sendResult.getMessageId(), DELAY_ARRAY[delayTimeLevel-1]);
            this.doAfterSyncSend(sendResult);
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }


    /**
     * 同步发送消息
     * @param msgObj  消息体
     * @throws MQException 消息异常
     */
    public void syncSend(Object msgObj) throws MQException {
        syncSend("", "", msgObj);
    }

    /**
     * 同步发送消息
     * @param tag  消息tag
     * @param msgObj  消息体
     * @throws MQException 消息异常
     */
    public void syncSend(String tag, Object msgObj) throws MQException {
        syncSend("", tag, msgObj);
    }

  /*  *//**
     * 同步发送消息
     * @param topic  topic
     * @param tag tag
     * @param msgObj  消息体
     * @param hashKey  用于hash后选择queue的key
     * @throws MQException 消息异常
     *//*
    public void syncSendOrderly(String topic, String tag, Object msgObj, String hashKey) throws MQException {
        if(null == msgObj) {
            return;
        }
        if(StringUtils.isEmpty(hashKey)) {
            // fall back to normal
            syncSend(topic, tag, msgObj);
        }
        try {
            SendResult sendResult = producer.send(genMessage(topic, tag, msgObj), messageQueueSelector, hashKey);
            log.info("send rocketmq message orderly ,messageId : {}", sendResult.getMsgId());
            this.doAfterSyncSend(sendResult);
        } catch (Exception e) {
            log.error("顺序消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("顺序消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }*/

    /**
     * 异步发送消息带tag
     * @param topic topic
     * @param tag tag
     * @param msgObj msgObj
     * @param sendCallback 回调
     * @throws MQException 消息异常
     */
    public void asyncSend(String topic, String tag, Object msgObj, SendCallback sendCallback) throws MQException {
        try {
            if (null == msgObj) {
                return;
            }
            producer.sendAsync(genMessage(topic, tag, msgObj), sendCallback);
            log.info("send rocketmq message async");
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }

    /**
     * 异步发送消息不带tag和topic
     * @param msgObj msgObj
     * @param sendCallback 回调
     * @throws MQException 消息异常
     */
    public void asyncSend(Object msgObj, SendCallback sendCallback) throws MQException {
        asyncSend("", "", msgObj, sendCallback);
    }

    /**
     * 异步发送消息不带tag和topic
     * @param tag msgtag
     * @param msgObj msgObj
     * @param sendCallback 回调
     * @throws MQException 消息异常
     */
    public void asyncSend(String tag, Object msgObj, SendCallback sendCallback) throws MQException {
        asyncSend("", tag, msgObj, sendCallback);
    }

  /*  *//**
     * 异步发送消息带tag
     * @param topic topic
     * @param tag tag
     * @param msgObj msgObj
     * @param sendCallback 回调
     * @param hashKey 用于hash后选择queue的key
     * @throws MQException 消息异常
     *//*
    public void asyncSend(String topic, String tag, Object msgObj, SendCallback sendCallback, String hashKey) throws MQException {
        if (null == msgObj) {
            return;
        }
        if(StringUtils.isEmpty(hashKey)) {
            // fall back to normal
            asyncSend(topic, tag, msgObj, sendCallback);
        }
        try {
            producer.send(genMessage(topic, tag, msgObj), messageQueueSelector, hashKey, sendCallback);
            log.info("send rocketmq message async");
        } catch (Exception e) {
            log.error("消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }*/

    /**
     * 发送事物消息  执行逻辑，先发送消息，执行本地事物，本地事物执行成功，
     * @param topic topic
     * @param tag tag
     * @param msgObj 消息体
     * @param arg1 验证参数
     *//*
    public void transactionSend(String topic, String tag, Object msgObj,Object arg1)throws MQException{
        if (null == msgObj) {
            return;
        }
        try{
            transactionMQProducer.sendMessageInTransaction(genMessage(topic, tag, msgObj),(Message msg1, Object arg) ->{
                if (executeLocalTransactionBranch(msg1,arg)){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            },arg1);
            log.info("send rocketmq message transaction ,topic : {}, msgObj {}", topic, msgObj);
        }catch (Exception e){
            log.error("事物消息发送失败，topic : {}, msgObj {}", topic, msgObj);
            throw new MQException("消息发送失败，topic :" + topic + ",e:" + e.getMessage());
        }
    }*/

    /**
     * web 直接发送消息
     */
    public void sendByConfigWeb(String topic, String tag, Object msgObj){

    }

    /**
     * 重写方法处理事物消息逻辑
     * @param msg1 消息体
     * @param arg 参数
     * @return true,消息提交，消费端可消费，false 本地事物失败，消息丢弃
     */
    public boolean executeLocalTransactionBranch(Message msg1, Object arg){
        return  true;
    }

    /**
     * 重写此方法处理发送后的逻辑
     *
     * @param sendResult  发送结果
     */
    public void doAfterSyncSend(SendResult sendResult) {}

    /**
     *
     * @param topic
     * @return
     */
    private String getTopic(String topic){
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        if (env.equals("prd")){
            topic="prd_"+topic;
        }else if (env.equals("pre")){
            topic="pre_"+topic;
        }
        else if(env.equals("qas")){
            topic="qas_"+topic;
        }
        return topic;
    }
    private String getGroup(String group){
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        if (env.equals("prd")){
            group="prd_"+group;
        }else if (env.equals("pre")){
            group="pre_"+group;
        }
        else if(env.equals("qas")){
            group="qas_"+group;
        }
        return group;
    }
}