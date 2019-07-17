package com.hui.zhang.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;

//@WebListener
@Component("baseInitListener")
public class BaseInitListener extends BaseServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseInitListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.setDefaultConxtext(event);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

}
