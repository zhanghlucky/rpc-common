package com.hui.zhang.common.util.etc;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;
import com.hui.zhang.common.util.EnvPropertyUtil;
import com.hui.zhang.common.util.PropertyUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhanghui on 2017/10/13.
 */
public class BaseEtc {
    private static final Logger logger = LoggerFactory.getLogger(BaseEtc.class);
    //private static MongoClient mongoClient;
    protected static final boolean IS_CONFIG_LOAD_SERVER;//是否读取配置中心模式
    static {
        //配置读取模式
        String pattern= PropertyUtil.getProperty("config.pattern");
        if (null==pattern||pattern.equals("server")){//读取配置管理中心服务
            logger.info("配置读取模式为：配置管理中心");
            IS_CONFIG_LOAD_SERVER=true;
        }else {//本地服务
            EnvPropertyUtil.loadEnvProperties();
            logger.info("配置读取模式为：本地配置");
            IS_CONFIG_LOAD_SERVER=false;
        }
    }
}
