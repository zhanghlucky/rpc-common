package com.hui.zhang.common.logger;

import com.hui.zhang.common.rocketmq.base.AbstractMQPushConsumer;
import com.hui.zhang.common.trace.TraceTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhanghui on 2019-06-04.
 */
public class EdbLogger implements  IEdbLogger {
    private final  Logger logger ;

    public EdbLogger(Class<?> clazz){
        this.logger=LoggerFactory.getLogger(clazz);
        //设置tradeId
        /*if (clazz== AbstractMQPushConsumer.class){
            //tradeHead= TraceTool.MQ_HANDLER_REQUEST;
            TraceTool.getInstance().setRandomTraceId(TraceTool.MQ_HANDLER_REQUEST);
        }*/
    }

    private String initMsg(String msg){
        msg=    "[tid:"+TraceTool.getInstance().getTraceId()+
                ",sid:"+TraceTool.getInstance().getSpanId()+
                ",pid:"+TraceTool.getInstance().getParentId()+
                ",zid:"+TraceTool.getInstance().getZipkinTraceId()
                +"]-"+msg;
        return msg;
    }

    @Override
    public void trace(String msg) {
        logger.trace(initMsg(msg));
    }

    @Override
    public void trace(Throwable e) {
        logger.trace(initMsg(e.getMessage()), e);
    }

    @Override
    public void trace(String msg, Throwable e) {
        logger.trace(initMsg(msg), e);
    }

    @Override
    public void trace(String var1, Object... var2) {
        logger.trace(initMsg(var1), var2);
    }

    @Override
    public void debug(String msg) {
        logger.debug(initMsg(msg));
    }

    @Override
    public void debug(Throwable e) {
        logger.debug(initMsg(e.getMessage()), e);
    }

    @Override
    public void debug(String msg, Throwable e) {
        logger.debug(initMsg(msg), e);
    }

    @Override
    public void debug(String var1, Object... var2) {
        logger.debug(initMsg(var1), var2);
    }

    @Override
    public void info(String msg) {
        logger.info(initMsg(msg));
    }

    @Override
    public void info(Throwable e) {
        logger.info(initMsg(e.getMessage()), e);
    }

    @Override
    public void info(String msg, Throwable e) {
        logger.info(initMsg(msg), e);
    }

    @Override
    public void info(String var1, Object... var2) {
        logger.info(initMsg(var1), var2);
    }

    @Override
    public void warn(String msg) {
        logger.warn(initMsg(msg));
    }

    @Override
    public void warn(Throwable e) {
        logger.warn(initMsg(e.getMessage()), e);
    }

    @Override
    public void warn(String msg, Throwable e) {
        logger.warn(initMsg(msg), e);
    }

    @Override
    public void warn(String var1, Object... var2) {
        logger.warn(initMsg(var1), var2);
    }

    @Override
    public void error(String msg) {
        logger.error(initMsg(msg));
    }

    @Override
    public void error(Throwable e) {
        logger.error(initMsg(e.getMessage()), e);
    }

    @Override
    public void error(String msg, Throwable e) {
        logger.error(initMsg(msg), e);
    }

    @Override
    public void error(String var1, Object... var2) {
        logger.error(initMsg(var1), var2);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }
}
