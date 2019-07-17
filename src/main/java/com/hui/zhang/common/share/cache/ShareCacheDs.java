package com.hui.zhang.common.share.cache;

import com.hui.zhang.common.datasource.redis.db.RedisDB;
import com.hui.zhang.common.util.etc.AppConfigUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by zhanghui on 2018/1/19.
 * update by tian.luan on 2018/05/25
 * Repairing shared cache objects
 * @since   ShareCacheDs 2.0
 */
public class ShareCacheDs  {
    private static  final String sharePrefix="SHARE";
    public static final ShareCacheDs DEFAULT = new ShareCacheDs();
    private  RedisDB redisDB;
    private String shareKey = null;
    private String SHARE_REDIS_DS = null;
    private String envId = null;
    static {
        //String SHARE_REDIS_DS=AppPropertyUtil.getEnvironmentConfig().getShareCacheDs();
    }
    public ShareCacheDs(String envId){
        this.SHARE_REDIS_DS= AppConfigUtil.getCfgEnvironmentPO(envId).getShareCacheDs();
       // redisDB=new RedisDB(SHARE_REDIS_DS,envId);
        this.envId = envId;
    }
    public ShareCacheDs(){
        this.SHARE_REDIS_DS= AppConfigUtil.getCfgEnvironmentPO().getShareCacheDs();
       // redisDB=new RedisDB(SHARE_REDIS_DS);
    }
    public ShareCacheDs(String shareKey, String envId){
        this.SHARE_REDIS_DS= AppConfigUtil.getCfgEnvironmentPO(envId).getShareCacheDs();
       // redisDB=new RedisDB(SHARE_REDIS_DS,envId);
        this.shareKey = shareKey;
        this.envId = envId;
    }

    private RedisDB getRedisDb(){
        if (null == envId){
            return new RedisDB(SHARE_REDIS_DS);
            //return  RedisDB.instance(SHARE_REDIS_DS);
        }else{
            return new RedisDB(SHARE_REDIS_DS,envId);
            //return  RedisDB.instance(SHARE_REDIS_DS,envId);
        }
    }

    /**
     * 设置共享对象
     * @param key
     * @param obj
     */
    public void  setCache(String key,Object obj){
        getRedisDb().set(sharePrefix,key,obj,0);
    }

    /**
     * 设置联合缓存
     * @param key
     * @param obj
     */
    public void setJoinCache(String key,Object obj){
        String name=obj.getClass().getSimpleName();
        key=name+"_"+key;
        getRedisDb().set(sharePrefix,key,obj,0);
    }

    /**
     * 获得共享对象
     * @param key
     * @return
     */
    public String getCache(String key){
        return  getRedisDb().get(sharePrefix,key,String.class);
    }

    /**
     * 获得共享对象
     * @param key
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getCache(String key, Class<T> cls){
        return getRedisDb().get(sharePrefix,key,cls);
    }

    /**
     * 删除缓存
     * @param key
     */
    public void removeCache(String key){
        getRedisDb().remove(sharePrefix,key);
    }





    /**
     * 设置hash 共享缓存对象
     */
    public  void setShareCache(String table,String key,Object object){
        String keyPrefix = sharePrefix + this.shareKey;
        getRedisDb().hset(keyPrefix,table,key,object,0);
    }

    /**
     * 取出单个共享缓存对象
     */
    public  <T> T  getShareCache(String table , String key,Class<T> cls){
        String keyPrefix = sharePrefix + this.shareKey;
        return  getRedisDb().hget(keyPrefix,table,key,cls);
    }
    /**
     * 取出所有共享缓存对象
     */
    public <T> Map<String,String> getAllShareCache(String table){
        String keyPrefix = sharePrefix + this.shareKey;
        return  getRedisDb().hgetAll(keyPrefix,table);
    }

    /**
     * 删除单个共享缓存对象
     * @param table
     * @param key
     */
    public void removeShardCache(String table,String key){
        String keyPrefix = sharePrefix + this.shareKey;
        getRedisDb().hremove(keyPrefix,table,key);
    }

    /**
     * 删除所有共享缓存对象
     */
    public void removeAll(String tableName){
        String keyPrefix = sharePrefix + this.shareKey + "_"+tableName;
        getRedisDb().remove(keyPrefix);
    }
}
