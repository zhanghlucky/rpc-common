package com.hui.zhang.common.rocketmq.error;

/**
 * Created by tian.luan
 * RocketMQ的自定义异常
 */
public class MQException extends RuntimeException {
    public MQException(String msg) {
        super(msg);
    }
}
