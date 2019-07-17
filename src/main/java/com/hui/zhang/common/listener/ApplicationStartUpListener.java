package com.hui.zhang.common.listener;

import com.hui.zhang.common.util.PropertyUtil;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.util.Log4jWebConfigurer;

/**
 * 容器初始化完成之后执行
 */
public class ApplicationStartUpListener  implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartUpListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        PropertyUtil.loadAllProperties();
        logger.info("ApplicationStartUpListener EXEC");
    }
}