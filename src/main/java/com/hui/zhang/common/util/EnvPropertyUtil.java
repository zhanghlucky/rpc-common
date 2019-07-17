package com.hui.zhang.common.util;

import com.hui.zhang.common.util.etc.GlobalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class EnvPropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(EnvPropertyUtil.class);
    private static Map<String, String> propertiesMap = new HashMap<>();
    // Default as in PropertyPlaceholderConfigurer

    static {
        loadEnvProperties();
    }

    public static void processProperties( Properties props) throws BeansException {

        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString().trim();
            try {
                //PropertiesLoaderUtils的默认编码是ISO-8859-1,在这里转码一下
                propertiesMap.put(keyStr, new String(props.getProperty(keyStr).getBytes("ISO-8859-1"),"utf-8").trim());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            };
        }
        //logger.info(String.valueOf(propertiesMap));
        //System.out.println(propertiesMap);
    }
    public static void loadEnvProperties(){
        String envId=GlobalUtil.getConfig("env.id");
        String proFile="env_"+envId+".properties";
        try {
            logger.info("load local properties:{}",proFile);
            Properties properties = PropertiesLoaderUtils.loadAllProperties(proFile);
            if (properties.size()==0){
                logger.error("can not load local properties file :{}",proFile);
                throw new RuntimeException("load local config error!no "+proFile+"!");
            }
            processProperties(properties);
        } catch (IOException e) {
            logger.error("load local properties {} error:{}",proFile,e.getMessage());
            //e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        if (null!=propertiesMap.get(name)){
           return  propertiesMap.get(name);
        }
        return  null;
    }
    public static <T> T getProperty(String name,Class<T> cls) {
        if (null!=propertiesMap.get(name)){
            if (cls.getName().equals(Integer.class.getName())){
                return (T) Integer.valueOf(propertiesMap.get(name));
            }else if(cls.getName().equals(Long.class.getName())){
                return (T) Long.valueOf(propertiesMap.get(name));
            }else {
                return (T) propertiesMap.get(name);
            }
        }else{
            if (cls.getName().equals(Integer.class.getName())){
                return (T)new Integer(0);
            }else if(cls.getName().equals(Long.class.getName())){
                return (T)new Long(0L);
            }else {
                return null;
            }
        }
    }

    public static <T> T getProperty(String name,T defaultValue,Class<T> cls) {
        if (null!=propertiesMap.get(name)){
            if (cls.getName().equals(Integer.class.getName())){
                return (T) Integer.valueOf(propertiesMap.get(name));
            }else if(cls.getName().equals(Long.class.getName())){
                return (T) Long.valueOf(propertiesMap.get(name));
            }else {
                return (T) propertiesMap.get(name);
            }
        }else{
            return defaultValue;
        }
    }
}
