package com.hui.zhang.common.util.etc;

import com.hui.zhang.common.datasource.mongo.util.MgoUtil;
import com.hui.zhang.common.util.DESUtil;
import com.hui.zhang.common.util.EnvPropertyUtil;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.buffer.UrlPathBuffer;
import com.hui.zhang.common.util.etc.bean.*;
import com.hui.zhang.common.util.etc.po.*;
import com.hui.zhang.common.util.etc.vo.CfgEsShardVO;
import com.hui.zhang.common.util.etc.vo.CfgShardVO;
import com.hui.zhang.common.util.http.HttpClientUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghui on 2018/6/19.
 */
public class AppConfigUtil extends  BaseEtc {

    private static final Logger logger = LoggerFactory.getLogger(AppConfigUtil.class);

    private static final Map<String,CfgMongoPO> MONGO_PROPERTY_MAP=new HashedMap();
    private static final Map<String,CfgRedisPO> REDIS_PROPERTY_MAP=new HashedMap();
    private static final Map<String,CfgDatabasePO> DATABASE_PROPERTY_MAP=new HashedMap();
    private static final Map<String,CfgElasticsearchPO> ELASTICSEARCH_PROPERTY_MAP=new HashedMap();
    private static final Map<String,CfgShardVO> SHARD_PROPERTY_MAP=new HashedMap();
    private static final Map<String,CfgEsShardVO> ES_SHARD_PROPERTY_MAP=new HashedMap();
    //private static final Map<String,CfgAppPO> APP_PROPERTY_MAP=new HashedMap();
    //private static final Map<String,List<CfgAppParamPO>> APP_PARAMS_PROPERTY_MAP=new HashedMap();
    private static CfgAppPO cfgAppPO=null;
    private static Map<String,String> APP_PARAMS_MAP=new HashedMap();
    private static Map<String,Long> APP_PARAMS_KEY_TIME_MAP=new HashedMap();

    private static CfgEnvironmentPO cfgEnvironmentPO=null;

    private static final String appName;

    static {
         appName=PropertyUtil.getProperty("app.name");
         initAppParams();
    }


    /**
     * 获得CfgDatabasePO
     * @param dsCode
     * @return
     */
    public static CfgDatabasePO getCfgDatabasePO(String dsCode){
        CfgDatabasePO cfgDatabasePO=DATABASE_PROPERTY_MAP.get(dsCode);
        if (null!=cfgDatabasePO){
            return  cfgDatabasePO;
        }

        if (IS_CONFIG_LOAD_SERVER) {
        //if (true) {
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/database").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgDatabasePO = JsonEncoder.DEFAULT.decode(json, CfgDatabasePO.class);
                DATABASE_PROPERTY_MAP.put(dsCode,cfgDatabasePO);
                return cfgDatabasePO;
            }
            logger.error("配置管理中心无{}的database配置！", dsCode);
        }else{
            String dsHeader="app.ds."+dsCode;
            cfgDatabasePO=new CfgDatabasePO();
            cfgDatabasePO.setDriverClassName(EnvPropertyUtil.getProperty(dsHeader+".driverClassName","com.mysql.jdbc.Driver",String.class));
            cfgDatabasePO.setDsHost(EnvPropertyUtil.getProperty(dsHeader+".host"));
            cfgDatabasePO.setDsPort(EnvPropertyUtil.getProperty(dsHeader+".port",Integer.class));
            cfgDatabasePO.setDbName(EnvPropertyUtil.getProperty(dsHeader+".dbName"));
            cfgDatabasePO.setUsername(EnvPropertyUtil.getProperty(dsHeader+".username"));
            cfgDatabasePO.setPassword(EnvPropertyUtil.getProperty(dsHeader+".password"));
            cfgDatabasePO.setInitialSize(EnvPropertyUtil.getProperty(dsHeader+".initialSize",10,Integer.class));
            cfgDatabasePO.setMaxActive(EnvPropertyUtil.getProperty(dsHeader+".maxActive",200,Integer.class));
            cfgDatabasePO.setMaxIdle(EnvPropertyUtil.getProperty(dsHeader+".maxIdle",10,Integer.class));
            cfgDatabasePO.setMaxWait(EnvPropertyUtil.getProperty(dsHeader+".maxWait",3000,Integer.class));
            DATABASE_PROPERTY_MAP.put(dsCode,cfgDatabasePO);
            return cfgDatabasePO;
        }
        return  cfgDatabasePO;
    }

    public static CfgRedisPO getCfgRedisPO(String dsCode){
        CfgRedisPO cfgRedisPO=REDIS_PROPERTY_MAP.get(dsCode);
        if (null!=cfgRedisPO){
            return  cfgRedisPO;
        }

        if (IS_CONFIG_LOAD_SERVER) {
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/redis").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgRedisPO = JsonEncoder.DEFAULT.decode(json, CfgRedisPO.class);
                REDIS_PROPERTY_MAP.put(dsCode,cfgRedisPO);
                return cfgRedisPO;
            }
            logger.error("配置管理中心无{}的redis配置！", dsCode);
        }else{
            String dsHeader="app.redis."+dsCode;
            cfgRedisPO=new CfgRedisPO();
            cfgRedisPO.setDsHost(EnvPropertyUtil.getProperty(dsHeader+".host"));
            cfgRedisPO.setDsPort(EnvPropertyUtil.getProperty(dsHeader+".port",Integer.class));
            cfgRedisPO.setPassword(EnvPropertyUtil.getProperty(dsHeader+".password"));
            cfgRedisPO.setMaxWait(EnvPropertyUtil.getProperty(dsHeader+".maxWait",-1,Integer.class));
            cfgRedisPO.setMaxIdle(EnvPropertyUtil.getProperty(dsHeader+".maxIdle",1,Integer.class));
            cfgRedisPO.setMaxActive(EnvPropertyUtil.getProperty(dsHeader+".maxActive",1000,Integer.class));
            cfgRedisPO.setDsName(dsCode);
            cfgRedisPO.setDsCode(dsCode);
            REDIS_PROPERTY_MAP.put(dsCode,cfgRedisPO);
            return cfgRedisPO;
        }
        return  cfgRedisPO;
    }


    public static CfgRedisPO getCfgRedisPO(String dsCode,String evnId){
        /*CfgRedisPO cfgRedisPO=REDIS_PROPERTY_MAP.get(dsCode);
        if (null!=cfgRedisPO){
            return  cfgRedisPO;
        }*/
        CfgRedisPO cfgRedisPO = new CfgRedisPO();
        if (IS_CONFIG_LOAD_SERVER) {
            String envId = evnId;
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/redis").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgRedisPO = JsonEncoder.DEFAULT.decode(json, CfgRedisPO.class);
                REDIS_PROPERTY_MAP.put(dsCode,cfgRedisPO);
                return cfgRedisPO;
            }
            logger.error("配置管理中心无{}的redis配置！", dsCode);
        }else{
            String dsHeader="app.redis."+dsCode;
            cfgRedisPO=new CfgRedisPO();
            cfgRedisPO.setDsHost(EnvPropertyUtil.getProperty(dsHeader+".host"));
            cfgRedisPO.setDsPort(EnvPropertyUtil.getProperty(dsHeader+".port",Integer.class));
            cfgRedisPO.setPassword(EnvPropertyUtil.getProperty(dsHeader+".password"));
            cfgRedisPO.setMaxWait(EnvPropertyUtil.getProperty(dsHeader+".maxWait",-1,Integer.class));
            cfgRedisPO.setMaxIdle(EnvPropertyUtil.getProperty(dsHeader+".maxIdle",1,Integer.class));
            cfgRedisPO.setMaxActive(EnvPropertyUtil.getProperty(dsHeader+".maxActive",1000,Integer.class));
            cfgRedisPO.setDsName(dsCode);
            REDIS_PROPERTY_MAP.put(dsCode,cfgRedisPO);
            return cfgRedisPO;
        }
        return  cfgRedisPO;
    }

    public static CfgMongoPO getCfgMongoPO(String dsCode){
        CfgMongoPO cfgMongoPO =MONGO_PROPERTY_MAP.get(dsCode);
        if (null!=cfgMongoPO){
            return cfgMongoPO;
        }
        if (IS_CONFIG_LOAD_SERVER) {
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/mongo").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgMongoPO = JsonEncoder.DEFAULT.decode(json, CfgMongoPO.class);
                MONGO_PROPERTY_MAP.put(dsCode,cfgMongoPO);
                return cfgMongoPO;
            }
            logger.error("配置管理中心无{}的mongo配置！", dsCode);
        }else{
            String dsHeader="app.mongo."+dsCode;
            cfgMongoPO=new CfgMongoPO();
            cfgMongoPO.setDsHost(EnvPropertyUtil.getProperty(dsHeader+".host"));
            cfgMongoPO.setDsPort(EnvPropertyUtil.getProperty(dsHeader+".port",Integer.class));
            cfgMongoPO.setDbName(EnvPropertyUtil.getProperty(dsHeader+".dbName"));
            cfgMongoPO.setConnectionsPerHost(EnvPropertyUtil.getProperty(dsHeader+".connectionsPerHost",50,Integer.class));
            cfgMongoPO.setMaxWaitTime(EnvPropertyUtil.getProperty(dsHeader+".maxWaitTime",120000,Integer.class));
            cfgMongoPO.setConnectTimeout(EnvPropertyUtil.getProperty(dsHeader+".connectTimeout",10000,Integer.class));
            cfgMongoPO.setSocketTimeout(EnvPropertyUtil.getProperty(dsHeader+".socketTimeout",0,Integer.class));
            cfgMongoPO.setDsCode(dsCode);

            MONGO_PROPERTY_MAP.put(dsCode,cfgMongoPO);
            return cfgMongoPO;
        }
        return cfgMongoPO;
    }

    public static  CfgShardVO getCfgShardVO(String dsCode){
        CfgShardVO cfgShardVO=SHARD_PROPERTY_MAP.get(dsCode);
        if (null!=cfgShardVO){
            return cfgShardVO;
        }
        if (IS_CONFIG_LOAD_SERVER){
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/shard").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgShardVO = JsonEncoder.DEFAULT.decode(json, CfgShardVO.class);
                SHARD_PROPERTY_MAP.put(dsCode,cfgShardVO);
                return cfgShardVO;
            }
            logger.error("配置管理中心无{}的shard配置！", dsCode);
        }else {
            //待实现本地的配置
            return null;
        }
        return cfgShardVO;

    }

    public static CfgAppPO getCfgAppPO(){
        if (null!=cfgAppPO){
            return cfgAppPO;
        }
        if (IS_CONFIG_LOAD_SERVER){
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String appNameCiphertext = DESUtil.getInstance().encode(appName, key);
            host = new UrlPathBuffer(host).append("/cfg/api/app").append(envIdCiphertext).append(appNameCiphertext).toString();
            //System.out.println(host);

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgAppPO = JsonEncoder.DEFAULT.decode(json, CfgAppPO.class);
                return cfgAppPO;
            }
            logger.error("配置管理中心无{}的app配置！", appName);
        }else{
            cfgAppPO=new CfgAppPO();
            cfgAppPO.setDubboAppName(appName);
            cfgAppPO.setAppName(appName);
            cfgAppPO.setLogLevel(EnvPropertyUtil.getProperty("app.log4j.level","DEBUG",String.class));
            cfgAppPO.setDubboProtocolPort(EnvPropertyUtil.getProperty("app.protocol.port",Integer.class));
            cfgAppPO.setDubboScan(EnvPropertyUtil.getProperty("app.scan"));
            cfgAppPO.setWebName(EnvPropertyUtil.getProperty("app.web.name"));
            cfgAppPO.setWebPort(EnvPropertyUtil.getProperty("app.web.port",Integer.class));
            return cfgAppPO;
        }
        return cfgAppPO;
    }

    public static String getCfgAppParamValue(String paramKey){
        String paramValue=APP_PARAMS_MAP.get(paramKey);
        if (null!=paramValue){
            return paramValue;
        }
        Long lastTime=APP_PARAMS_KEY_TIME_MAP.get(paramKey);
        if (null==lastTime||System.currentTimeMillis()-lastTime>30*1000){//限频，每隔30S才能请求一次
            APP_PARAMS_KEY_TIME_MAP.put(paramKey,System.currentTimeMillis());
            if (IS_CONFIG_LOAD_SERVER){
                String envId = getEnvId();
                String host = getCfgHost();
                String key = getCfgKey();

                String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
                String appNameCiphertext = DESUtil.getInstance().encode(appName, key);
                host = new UrlPathBuffer(host).append("/cfg/api/params").append(envIdCiphertext).append(appNameCiphertext).toString();
                //System.out.println(host);

                Map<String, String> params = new HashedMap();
                String result = HttpClientUtil.doPost(host, params);
                if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                    String json = DESUtil.getInstance().decode(result, key);
                    List<CfgAppParamPO> list = JsonEncoder.DEFAULT.decode(json, List.class,CfgAppParamPO.class);
                    if (null!=list){
                        for (CfgAppParamPO cfgAppParamPO: list) {
                            APP_PARAMS_MAP.put(cfgAppParamPO.getParamKey(),cfgAppParamPO.getParamValue());
                        }
                    }
                    paramValue=APP_PARAMS_MAP.get(paramKey);
                    return paramValue;
                }
                logger.warn("配置管理中心无程序：{}的参数：{}配置！", appName,paramKey);
            }else{
                String paramsKey="app.params."+paramKey;
                String paramsValue=EnvPropertyUtil.getProperty(paramsKey,String.class);
                APP_PARAMS_MAP.put(paramKey,paramsValue);
                return paramsValue;
            }
        }
        return paramValue;
    }

    public static CfgEnvironmentPO getCfgEnvironmentPO(){
        if (null!=cfgEnvironmentPO){
            return cfgEnvironmentPO;
        }
        if (IS_CONFIG_LOAD_SERVER) {
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            host = new UrlPathBuffer(host).append("/cfg/api/environment").append(envIdCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgEnvironmentPO = JsonEncoder.DEFAULT.decode(json, CfgEnvironmentPO.class);
                return cfgEnvironmentPO;
            }
            logger.error("配置管理中心无环境配置！");
        }else{
            cfgEnvironmentPO=new CfgEnvironmentPO();
            cfgEnvironmentPO.setZookeeperAddress(EnvPropertyUtil.getProperty("env.zookeeperAddress"));
            cfgEnvironmentPO.setUploadHost(EnvPropertyUtil.getProperty("env.uploadHost"));
            cfgEnvironmentPO.setFileServerHost(EnvPropertyUtil.getProperty("env.fileServerHost"));
            cfgEnvironmentPO.setStaticServerHost(EnvPropertyUtil.getProperty("env.staticServerHost"));
            cfgEnvironmentPO.setEnvName(EnvPropertyUtil.getProperty("env.environment","dev",String.class));
            cfgEnvironmentPO.setMqAddress(EnvPropertyUtil.getProperty("env.rocketmqServer"));
            cfgEnvironmentPO.setLangs(EnvPropertyUtil.getProperty("env.langs","zh_CN",String.class));
            cfgEnvironmentPO.setShareCacheDs(EnvPropertyUtil.getProperty("env.shareCacheDs"));

            cfgEnvironmentPO.setEsUrl(EnvPropertyUtil.getProperty("env.esUrl"));
            cfgEnvironmentPO.setEsUsername(EnvPropertyUtil.getProperty("env.esUsername"));
            cfgEnvironmentPO.setEsPassword(EnvPropertyUtil.getProperty("env.esPassword"));

            cfgEnvironmentPO.setAccessKeyId(EnvPropertyUtil.getProperty("env.accessKeyId"));
            cfgEnvironmentPO.setAccessKeySecret(EnvPropertyUtil.getProperty("env.accessKeySecret"));
            cfgEnvironmentPO.setEnvId(EnvPropertyUtil.getProperty("env.envId"));
            cfgEnvironmentPO.setZipkinUrl(EnvPropertyUtil.getProperty("env.zipkin.url"));

            //TO ADD
            return cfgEnvironmentPO;
        }
        return cfgEnvironmentPO;

    }


    public static CfgEnvironmentPO getCfgEnvironmentPO(String evnIds){
        if (null!=cfgEnvironmentPO && null == evnIds){
            return cfgEnvironmentPO;
        }
        if (IS_CONFIG_LOAD_SERVER) {
            String envId = null;
            if (null !=evnIds){
                envId = evnIds;
            }
            else {
                envId = getEnvId();
            }
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            host = new UrlPathBuffer(host).append("/cfg/api/environment").append(envIdCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgEnvironmentPO = JsonEncoder.DEFAULT.decode(json, CfgEnvironmentPO.class);
                return cfgEnvironmentPO;
            }
            logger.error("配置管理中心无环境配置！");
        }else{
            cfgEnvironmentPO=new CfgEnvironmentPO();
            cfgEnvironmentPO.setZookeeperAddress(EnvPropertyUtil.getProperty("env.zookeeperAddress"));
            cfgEnvironmentPO.setUploadHost(EnvPropertyUtil.getProperty("env.uploadHost"));
            cfgEnvironmentPO.setFileServerHost(EnvPropertyUtil.getProperty("env.fileServerHost"));
            cfgEnvironmentPO.setStaticServerHost(EnvPropertyUtil.getProperty("env.staticServerHost"));
            cfgEnvironmentPO.setEnvName(EnvPropertyUtil.getProperty("env.environment","dev",String.class));
            cfgEnvironmentPO.setMqAddress(EnvPropertyUtil.getProperty("env.rocketmqServer"));
            cfgEnvironmentPO.setLangs(EnvPropertyUtil.getProperty("env.langs","zh_CN",String.class));
            cfgEnvironmentPO.setShareCacheDs(EnvPropertyUtil.getProperty("env.shareCacheDs"));
            //TO ADD
            return cfgEnvironmentPO;
        }
        return cfgEnvironmentPO;

    }

    /**
     * 获得CfgDatabasePO
     * @param dsCode
     * @return
     */
    public static CfgElasticsearchPO getCfgElasticsearchPO(String dsCode){
        CfgElasticsearchPO cfgElasticsearchPO=getCfgElasticsearchPO(dsCode,getEnvId());
        return  cfgElasticsearchPO;
    }

    public static CfgElasticsearchPO getCfgElasticsearchPO(String dsCode,String envId){
        CfgElasticsearchPO cfgElasticsearchPO=ELASTICSEARCH_PROPERTY_MAP.get(envId+dsCode);
        if (null!=cfgElasticsearchPO){
            return  cfgElasticsearchPO;
        }
        if (IS_CONFIG_LOAD_SERVER) {
            //if (true) {

            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/elasticsearch").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgElasticsearchPO = JsonEncoder.DEFAULT.decode(json, CfgElasticsearchPO.class);
                ELASTICSEARCH_PROPERTY_MAP.put(envId+dsCode,cfgElasticsearchPO);
                return cfgElasticsearchPO;
            }
            logger.error("配置管理中心无环境{}，dsCode{}的elasticsearch配置！",envId,dsCode);
        }else{
            String dsHeader="app.es."+dsCode;
            cfgElasticsearchPO=new CfgElasticsearchPO();
            cfgElasticsearchPO.setDsHost(EnvPropertyUtil.getProperty(dsHeader+".host"));
            cfgElasticsearchPO.setDsPort(EnvPropertyUtil.getProperty(dsHeader+".port",Integer.class));
            cfgElasticsearchPO.setDsTcpPort(EnvPropertyUtil.getProperty(dsHeader+".tcpPort",Integer.class));
            cfgElasticsearchPO.setIndexName(EnvPropertyUtil.getProperty(dsHeader+".indexName"));
            cfgElasticsearchPO.setUsername(EnvPropertyUtil.getProperty(dsHeader+".username"));
            cfgElasticsearchPO.setPassword(EnvPropertyUtil.getProperty(dsHeader+".password"));
            //envId不起作用
            ELASTICSEARCH_PROPERTY_MAP.put(getEnvId()+dsCode,cfgElasticsearchPO);
            return cfgElasticsearchPO;
        }
        return  cfgElasticsearchPO;
    }

    public static  CfgEsShardVO getCfgEsShardVO(String dsCode){
        CfgEsShardVO cfgEsShardVO=ES_SHARD_PROPERTY_MAP.get(dsCode);
        if (null!=cfgEsShardVO){
            return cfgEsShardVO;
        }
        if (IS_CONFIG_LOAD_SERVER){
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String dsCodeCiphertext = DESUtil.getInstance().encode(dsCode, key);
            host = new UrlPathBuffer(host).append("/cfg/api/es-shard").append(envIdCiphertext).append(dsCodeCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                cfgEsShardVO = JsonEncoder.DEFAULT.decode(json, CfgEsShardVO.class);
                ES_SHARD_PROPERTY_MAP.put(dsCode,cfgEsShardVO);
                return cfgEsShardVO;
            }
            logger.error("配置管理中心无{}的es-shard配置！", dsCode);
        }else {
            //待实现本地的配置
            return null;
        }
        return cfgEsShardVO;

    }




    private static String getEnvId(){
        return GlobalUtil.getConfig("env.id");
    }
    private static String getCfgHost(){
        return GlobalUtil.getConfig("cfg.host");
    }
    private static String getCfgKey(){
        return GlobalUtil.getConfig("cfg.key");
    }


    public static  void initAppParams(){
        if (IS_CONFIG_LOAD_SERVER) {
            String envId = getEnvId();
            String host = getCfgHost();
            String key = getCfgKey();

            String envIdCiphertext = DESUtil.getInstance().encode(envId, key);
            String appNameCiphertext = DESUtil.getInstance().encode(appName, key);
            host = new UrlPathBuffer(host).append("/cfg/api/params").append(envIdCiphertext).append(appNameCiphertext).toString();

            Map<String, String> params = new HashedMap();
            String result = HttpClientUtil.doPost(host, params);
            if (StringUtils.isNotEmpty(result) && !result.equals("nodata")) {
                String json = DESUtil.getInstance().decode(result, key);
                List<CfgAppParamPO> list = JsonEncoder.DEFAULT.decode(json, List.class, CfgAppParamPO.class);
                if (null != list) {
                    for (CfgAppParamPO cfgAppParamPO : list) {
                        APP_PARAMS_MAP.put(cfgAppParamPO.getParamKey(), cfgAppParamPO.getParamValue());
                    }
                }
            }
            logger.info("缓存程序：{}的程序参数到本地",appName);
        }else{
            //noting to do
        }
    }

    public  static  void clearAppParam(){
        APP_PARAMS_MAP.clear();
    }
}
