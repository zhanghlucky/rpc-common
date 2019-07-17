package com.hui.zhang.common.boot;

import com.hui.zhang.common.listener.ApplicationStartUpListener;
import com.hui.zhang.common.util.docker.RegistHostPortUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghui on 2017/10/13.
 */
public class CentaurApplication {
    private SpringApplication application;


    public CentaurApplication(Class cls){
        buildDefaultApplication(cls);
    }

    public CentaurApplication(Class cls, List<ApplicationListener> listenerList){
        buildDefaultApplication(cls);
        /**
         * 添加监听器
         */
        for (ApplicationListener applicationListener : listenerList) {
            application.addListeners(applicationListener);
        }
    }

    private void buildDefaultApplication(Class cls) {
        application = new SpringApplication(cls);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.addListeners(new ApplicationStartUpListener());
        application.setLogStartupInfo(false);
        //Map<String, Object> defaultProperties=new HashedMap();
        //defaultProperties.put("spring.output.ansi.enabled","NEVER");
        //application.setDefaultProperties(defaultProperties);
        Map<String,Object> pro= new HashMap<String,Object>();
        pro.put("server.port", RegistHostPortUtil.getRegistWebPort());
        //String webName=AppPropertyUtil.getAppConfig(PropertyUtil.getProperty("app.name")).getWebName();
        String webName= AppConfigUtil.getCfgAppPO().getWebName();
        pro.put("server.contextPath","/"+ webName);
        pro.put("spring.output.ansi.enabled","never");
        pro.put("server.max-http-header-size",10000000);
        application.setDefaultProperties(pro);


    }

    public void  run(){
        application.run();
    }
}

