package com.hui.zhang.common.spring;

import com.hui.zhang.common.directive.DateDirective;
import com.hui.zhang.common.directive.FUploadDirective;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//@Configuration
public abstract class FreeMarkerConfig {
    private  Map<String, Object> freemarkerVariablesMap=new HashMap<>();


    /**
     * 添加freemarker变量
     * @param key
     * @param obj
     */
    protected void addFreemarkerVariables(String key,Object obj){
        freemarkerVariablesMap.put(key,obj);
    }

    /**
     * 初始化freemarker配置
     */
    protected abstract void  initFreeMarkerConfig();

    @Bean(name ="freemarkerConfig")
    public FreeMarkerConfigurer getFreemarkerConfig() {
        initFreeMarkerConfig();
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        //configurer.setTemplateLoaderPath("**classpath:**");
        configurer.setTemplateLoaderPaths("classpath:/templates/","classpath:/static/");
        Properties settings=new Properties();

        settings.setProperty("template_update_delay","0");
        settings.setProperty("default_encoding","UTF-8");
        settings.setProperty("number_format","0.##########");
        settings.setProperty("datetime_format","yyyy-MM-dd HH:mm:ss");
        settings.setProperty("classic_compatible","true");
        settings.setProperty("template_exception_handler","ignore");

        configurer.setFreemarkerSettings(settings);

        Map<String, Object> map = new HashMap<>();
        map.putAll(freemarkerVariablesMap);
        map.put("fupload",new FUploadDirective());
        map.put("date",new DateDirective());
        configurer.setFreemarkerVariables(map);
        return configurer;
    }

    @Bean(name ="freeMarkerViewResolver")
    public FreeMarkerViewResolver getFreeMarkerViewResolver(){
        FreeMarkerViewResolver freeMarkerViewResolver=new FreeMarkerViewResolver();
        freeMarkerViewResolver.setViewClass(FreeMarkerView.class);
        freeMarkerViewResolver.setSuffix(".ftl");
        freeMarkerViewResolver.setContentType("text/html;charset=utf-8");
        freeMarkerViewResolver.setExposeRequestAttributes(true);
        freeMarkerViewResolver.setExposeSessionAttributes(true);
        freeMarkerViewResolver.setExposeSpringMacroHelpers(true);
        freeMarkerViewResolver.setRequestContextAttribute("request");
        return freeMarkerViewResolver;
    }
}