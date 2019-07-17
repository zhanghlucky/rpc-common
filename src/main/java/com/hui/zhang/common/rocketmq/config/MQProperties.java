package com.hui.zhang.common.rocketmq.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by tian.luan
 * RocketMQ的配置参数
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rocketmq")
public class MQProperties {
    /**
     * config name server address
     */
    private String nameServerAddress;// = "192.168.3.65:9876";//;192.168.3.116:9876;192.168.3.117:9876;192.168.3.118:9876
    /**
     * config producer group , default to DPG+RANDOM UUID like DPG-fads-3143-123d-1111
     */
    private String producerGroup;// = "testGroup";
    /**
     *  config transactionProducer group
     */
    private  String transactionProducer;

    /**
     * config send message timeout
     */
    private Integer sendMsgTimeout = 3000;
    /**
     *  消息轨迹追溯，暂时标记为false 后期会采用记录到数据库的形式，数据库采用mongo
     */
    private Boolean traceEnabled = Boolean.FALSE;

    public String getNameServerAddress() {
        return nameServerAddress;
    }

    public void setNameServerAddress(String nameServerAddress) {
        this.nameServerAddress = nameServerAddress;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getTransactionProducer() {
        return transactionProducer;
    }

    public void setTransactionProducer(String transactionProducer) {
        this.transactionProducer = transactionProducer;
    }

    public Integer getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(Integer sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    public Boolean getTraceEnabled() {
        return traceEnabled;
    }

    public void setTraceEnabled(Boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }
}
