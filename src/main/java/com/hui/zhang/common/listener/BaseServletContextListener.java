package com.hui.zhang.common.listener;

import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.po.CfgAppPO;
import com.hui.zhang.common.util.ip.IpGetter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhanghui on 2017/10/13.
 */
public abstract class BaseServletContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(BaseServletContextListener.class);

    public void setDefaultConxtext(ServletContextEvent event){
        //String basePath=event.getServletContext().getContextPath();
        //静态资源地址
        //String staticServerHost= AppPropertyUtil.getEnvironmentConfig().getStaticServerHost();
        String staticServerHost= AppConfigUtil.getCfgEnvironmentPO().getStaticServerHost();
        event.getServletContext().setAttribute("STATIC_SERVER", staticServerHost);

        //log4j配置
        try{
            Properties properties = PropertiesLoaderUtils.loadAllProperties("log4j.properties");
            //AppLog4jBean appLog4jBean= AppPropertyUtil.getAppLog4jConfig(appName);
            //AppBean appBean=AppPropertyUtil.getAppConfig(appName);
            CfgAppPO cfgAppPO=AppConfigUtil.getCfgAppPO();
            String logLevel=cfgAppPO.getLogLevel();

            properties.setProperty("log4j.logger.org.apache.curator.framework.recipes.cache","ERROR");
            properties.setProperty("log4j.logger.org.elasticsearch.client.RestClient","ERROR");
            properties.setProperty("log4j.logger.org.redisson","ERROR");
            properties.setProperty("log4j.logger.io.seata","ERROR");



            //add 每天一个日志文件
            properties.setProperty("log4j.appender.DailyRollingFile","org.apache.log4j.DailyRollingFileAppender");
            String logFile="/data/logs/"+cfgAppPO.getWebName()+"-"+cfgAppPO.getWebPort()+".log";
            if (IpGetter.isWindowsOS()){
                logFile="D://data//logs//"+cfgAppPO.getWebName()+"-"+cfgAppPO.getWebPort()+".log";
            }
            logger.info("保存DailyRollingFile位置：{}",logFile);

            properties.setProperty("log4j.appender.DailyRollingFile.File",logFile);
            properties.setProperty("log4j.appender.DailyRollingFile.layout","org.apache.log4j.PatternLayout");
            properties.setProperty("log4j.appender.DailyRollingFile.layout.ConversionPattern","[%d][%p][%c]-%m%n");
            //--add 每天一个日志文件

            if (StringUtils.isNotEmpty(cfgAppPO.getLogProperties())){
                try{
                    String proStr=cfgAppPO.getLogProperties();
                    String proArray[]=proStr.split("\n");
                    for (String line:proArray) {
                        if (!line.startsWith("#")){
                            String keyValue[]= line.split("=");
                            String key=keyValue[0];
                            String value=keyValue[1];
                            if (StringUtils.isNotEmpty(properties.getProperty(key))){
                                logger.info("覆盖log4j配置->key:{},原值:{},新值:{}",key,properties.getProperty(key),value);
                            }else{
                                logger.info("追加log4j配置->key:{},新值:{}",key,value);
                            }
                            properties.setProperty(key,value);
                        }
                    }
                }catch (Exception e){
                    logger.error("解析自定义log4j配置异常，请确认配置是否正确:{}",e);
                }
            }else {
                //nothing to do
            }
            logger.info("LOG4J日志级别：{}",logLevel);
            properties.setProperty("log4j.rootLogger",""+logLevel+",console,DailyRollingFile");
            properties.setProperty("log4j.appender.console.Threshold",logLevel);
            properties.setProperty("log4j.appender.DailyRollingFile.Threshold",logLevel);
            PropertyConfigurator.configure(properties);

        } catch (IOException e) {
            logger.error("加载log4j.properties配置异常,请正确配置：{}",e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public abstract void contextInitialized(ServletContextEvent servletContextEvent) ;

    @Override
    public abstract void contextDestroyed(ServletContextEvent servletContextEvent) ;
}
