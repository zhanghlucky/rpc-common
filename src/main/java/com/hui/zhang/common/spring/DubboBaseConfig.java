package com.hui.zhang.common.spring;

import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;
import brave.spring.beans.CurrentTraceContextFactoryBean;
import brave.spring.beans.TracingFactoryBean;
import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.docker.RegistHostPortUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.ip.IpGetter;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.SimpleTransactionStatus;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.beans.AsyncReporterFactoryBean;
import zipkin2.reporter.beans.OkHttpSenderFactoryBean;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhanghui on 2017/10/11.
 */
public class DubboBaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DubboBaseConfig.class);

    @Bean
    public RegistryConfig registry() {
        RegistryConfig registryConfig = new RegistryConfig();
        //registryConfig.setAddress(AppPropertyUtil.getEnvironmentConfig().getZookeeperAddress());
        registryConfig.setAddress(AppConfigUtil.getCfgEnvironmentPO().getZookeeperAddress());
        registryConfig.setProtocol("zookeeper");
        registryConfig.setTimeout(5000);
        if(!IpGetter.isWindowsOS()){
            String webName=AppConfigUtil.getCfgAppPO().getWebName();
            registryConfig.setFile("/home/.dubbo/"+webName+"-dubbo-registry.cache");
            logger.info("set dubbo-registry.cache at:{}",registryConfig.getFile());
        }
        //registryConfig.setFile("/home/dubbo/app/dubbo-registry.properties");
        return registryConfig;
    }

    @Bean
    public ApplicationConfig application() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(PropertyUtil.getProperty("app.name"));
        applicationConfig.setQosEnable(false);//禁用QOS
        return applicationConfig;
    }
   /* @Bean
    public MonitorConfig monitorConfig() {
        MonitorConfig mc = new MonitorConfig();
        mc.setProtocol("registry");
        return mc;
    }


    @Bean
    public ReferenceConfig referenceConfig() {
        ReferenceConfig rc = new ReferenceConfig();
        rc.setMonitor(monitorConfig());
        return rc;
    }*/

    @Bean
    public ProtocolConfig protocol() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(RegistHostPortUtil.getRegistProtocolPort());
        protocolConfig.setName(AppConfigUtil.getCfgAppPO().getDubboProtocolName() );

        return protocolConfig;
    }


    @Bean("tracing")
    public TracingFactoryBean tracingFactoryBean(@Qualifier("reporter") AsyncReporter reporter) throws Exception {
        TracingFactoryBean tracingFactoryBean = new TracingFactoryBean();
        tracingFactoryBean.setLocalServiceName(AppConfigUtil.getCfgAppPO().getWebName());
        tracingFactoryBean.setSpanReporter(reporter);
        CurrentTraceContextFactoryBean currentTraceContextFactoryBean = new CurrentTraceContextFactoryBean();
        CurrentTraceContext.ScopeDecorator scopeDecorator = MDCScopeDecorator.create();
        currentTraceContextFactoryBean.setScopeDecorators(Arrays.asList(scopeDecorator));
        tracingFactoryBean.setCurrentTraceContext(currentTraceContextFactoryBean.getObject());
        return tracingFactoryBean;
    }

    @Bean
    public AsyncReporterFactoryBean reporter(@Qualifier("okHttpSender")OkHttpSender sender){
        AsyncReporterFactoryBean asyncReporterFactoryBean = new AsyncReporterFactoryBean();
        asyncReporterFactoryBean.setSender(sender);
        asyncReporterFactoryBean.setCloseTimeout(1000);
        return asyncReporterFactoryBean;
    }

    @Bean("okHttpSender")
    public OkHttpSenderFactoryBean okHttpSender(){
        OkHttpSenderFactoryBean okHttpSenderFactoryBean = new OkHttpSenderFactoryBean();
        okHttpSenderFactoryBean.setEndpoint("http://"+AppConfigUtil.getCfgEnvironmentPO().getZipkinUrl()+"/api/v2/spans");
        return okHttpSenderFactoryBean;
    }

    @Bean
    public ProviderConfig provider() {
        ProviderConfig providerConfig = new ProviderConfig();
//        providerConfig.setMonitor(monitorConfig());
        providerConfig.setHost(RegistHostPortUtil.getRegistHost());
        //providerConfig.setThreads(4000); // 核心线程数
        providerConfig.setThreadpool("limited");
        providerConfig.setDispatcher("message");
        providerConfig.setFilter("tracing");
        providerConfig.setRetries(0);//取消重试
        return providerConfig;
    }
    @Bean
    public ConsumerConfig consumer(){
        ConsumerConfig consumerConfig=new ConsumerConfig();
        consumerConfig.setFilter("tracing");
        consumerConfig.setTimeout(60000);
        consumerConfig.setRetries(0);//取消重试
        return consumerConfig;
    }

    /*@Bean
    public AnnotationBean annotationBean() {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(AppConfigUtil.getCfgAppPO().getDubboScan());
        return annotationBean;
    }*/
    @Bean
    public ServiceAnnotationBeanPostProcessor serviceAnnotationBeanPostProcessor() {
        //RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment);
        Set<String> packagesToScan =new HashSet<>();
        String scan=AppConfigUtil.getCfgAppPO().getDubboScan();
        if(StringUtils.isNotEmpty(scan)){
            String packages[]=scan.split(",");
            for (String pack:packages) {
                packagesToScan.add(pack);
            }
        }
        //resolver.getProperty(BASE_PACKAGES_PROPERTY_NAME, Set.class, emptySet());
        return new ServiceAnnotationBeanPostProcessor(packagesToScan);
    }

    @Bean
    public HystrixCommandAspect hystrixCommandAspect() {
        return new HystrixCommandAspect();
    }



}