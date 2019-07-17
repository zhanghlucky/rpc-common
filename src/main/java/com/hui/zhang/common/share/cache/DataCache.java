package com.hui.zhang.common.share.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.datasource.mybatis.db.ds.MybatisUtil;
import com.hui.zhang.common.util.CentaurBeanUtils;
import com.hui.zhang.common.util.MD5Util;
import com.hui.zhang.common.util.etc.bean.CacheTableBean;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by zhanghui on 2018/2/1.
 */
public class DataCache {

    private static final Logger logger = LoggerFactory.getLogger(DataCache.class);
    /**
     * 插入更新数据缓存
     * @param dsName
     * @param dataObj
     */
    @Deprecated
    public  static void insertUpdate(String dsName,Object dataObj){
        try{
            String className=dataObj.getClass().getSimpleName(); 
            className=className.substring(0,className.length()-2);
            String tableName= MybatisUtil.camel2Underline(className);
            String shareKey=dsName+"_"+tableName;
            ShareCacheDs shareCacheDs = new ShareCacheDs(dsName,null);
            CacheTableBean cacheTableBean= shareCacheDs.getCache(shareKey,CacheTableBean.class);
            if (null!=cacheTableBean){
                String keys=cacheTableBean.getTableKeys();
                String [] keyArray=keys.split(",");
                String [] idArray=new String[keyArray.length];
                for (int i=0;i<keyArray.length;i++) {
                    String key=keyArray[i];
                    String getMethod="get"+MybatisUtil.underline2Camel(key,false);
                    MethodAccess access = MethodAccess.get(dataObj.getClass());
                    String id=(String)access.invoke(dataObj,getMethod);
                    idArray[i]=id;
                    //System.out.println("id->"+id);
                }
                //TO ADD  idArray hash 排序 MD5 得到一个key set到redis中。并把对象放入redis。
                String cacheKey= getIdKey(tableName,idArray);
                ShareCacheDs.DEFAULT.setCache(cacheKey,dataObj);
            }
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }
    }
    /**
     * 添加数据缓存
     */
    public static  void shardInsert(String dsName,Object dataObj){
        try {
            String className=dataObj.getClass().getSimpleName();
            className=className.substring(0,className.length()-2);
            String tableName= MybatisUtil.camel2Underline(className);
            String shareKey=dsName+"_"+tableName;
            ShareCacheDs shareCacheDs = new ShareCacheDs(dsName,null);
            CacheTableBean cacheTableBean= shareCacheDs.getCache(shareKey,CacheTableBean.class);
            if (null != cacheTableBean){
                String keys=cacheTableBean.getTableKeys();
                String [] keyArray=keys.split(",");
                String [] idArray=new String[keyArray.length];
                for (int i=0;i<keyArray.length;i++) {
                    String key=keyArray[i];
                    String getMethod="get"+MybatisUtil.underline2Camel(key,false);
                    MethodAccess access = MethodAccess.get(dataObj.getClass());
                    String id=(String)access.invoke(dataObj,getMethod);
                    idArray[i]=id;
                }
                String cacheKey= DataCache.getIdKey(tableName,idArray);
                shareCacheDs.setShareCache(tableName,cacheKey,dataObj);
            }
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }

    }

    /**
     * 修改数据缓存
     * @param dsName
     * @param dataObj
     * flag true 修改，false 删除
     */
    public static void shardUpdate(String dsName,Object dataObj,boolean flag){

        try{
            String className=dataObj.getClass().getSimpleName();
            className=className.substring(0,className.length()-2);
            String tableName= MybatisUtil.camel2Underline(className);
            String shareKey=dsName+"_"+tableName;
            ShareCacheDs shareCacheDs = new ShareCacheDs(dsName,null);
            CacheTableBean cacheTableBean= shareCacheDs.getCache(shareKey,CacheTableBean.class);
            if (null != cacheTableBean){
                String keys=cacheTableBean.getTableKeys();
                String [] keyArray=keys.split(",");
                String [] idArray=new String[keyArray.length];
                for (int i=0;i<keyArray.length;i++) {
                    String key=keyArray[i];
                    String getMethod="get"+MybatisUtil.underline2Camel(key,false);
                    MethodAccess access = MethodAccess.get(dataObj.getClass());
                    String id=(String)access.invoke(dataObj,getMethod);
                    idArray[i]=id;
                }
                Map <String ,String> map = shareCacheDs.getAllShareCache(tableName);
                String valueObj= null;/*dataMap.get(valueColumn);*/
                String cacheKey= DataCache.getIdKey(tableName,idArray);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key=entry.getKey();
                    String value=entry.getValue();
                    if (key.equals(cacheKey)){
                        valueObj = map.get(cacheKey);
                        shareCacheDs.removeShardCache(tableName,cacheKey);
                        logger.info("清理缓存成功，重新加入缓存");
                    }
                }

                if (flag){
                    JSONObject jsonObject = null;
                   if (null != valueObj){
                        jsonObject = JSONObject.parseObject(valueObj);
                   }
                    MethodAccess access = MethodAccess.get(dataObj.getClass());
                    Field[] fields = dataObj.getClass().getDeclaredFields();
                    // 循环赋值
                    for (Field field : fields) {
                       if (jsonObject.containsKey(field.getName())){
                               String getMethod = "get"+captureName(field.getName());
                               Object obj =  access.invoke(dataObj,getMethod);
                               if (null == obj){
                                   String setMethod="set"+captureName(field.getName());
                                   if (field.getType().getName().equals("java.lang.Long")){
                                       access.invoke(dataObj,setMethod,Long.valueOf(String.valueOf(jsonObject.get(field.getName()))));
                                   }else{
                                       access.invoke(dataObj,setMethod,jsonObject.get(field.getName()));
                                   }
                               }
                       }
                    }
                    shareCacheDs.setShareCache(tableName,cacheKey,dataObj);
                    logger.info("重新加入缓存成功，缓存数据为:{}", JSON.toJSONString(dataObj));
                }
            }
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }

    }

    public  static void deleteByKeyShard(String dsName,Class cls,Object objKey){
        try{
            String className=cls.getSimpleName();
            className=className.substring(0,className.length()-2);
            String tableName= MybatisUtil.camel2Underline(className);
            String shareKey=dsName+"_"+tableName;
            ShareCacheDs shareCacheDs = new ShareCacheDs(dsName,null);
            CacheTableBean cacheTableBean= shareCacheDs.getCache(shareKey,CacheTableBean.class);
            if (null != cacheTableBean){
                String [] idArray={String.valueOf(objKey)};
                String cacheKey= DataCache.getIdKey(tableName,idArray);
                Map <String ,String> map = shareCacheDs.getAllShareCache(tableName);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key=entry.getKey();
                    String value=entry.getValue();
                    if (key.equals(cacheKey)){
                        shareCacheDs.removeShardCache(tableName,cacheKey);
                        logger.info("清理缓存成功");
                    }
                }
            }
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }


    }

    /**
     * 清除数据缓存
     * @param dsName
     * @param dataObj
     */
    @Deprecated
    public  static void deleteByObject(String dsName,Object dataObj){
        try{
            String className=dataObj.getClass().getSimpleName();
            className=className.substring(0,className.length()-2);
            String tableName= MybatisUtil.camel2Underline(className);
            String shareKey=dsName+"_"+tableName;

            CacheTableBean cacheTableBean= ShareCacheDs.DEFAULT.getCache(shareKey,CacheTableBean.class);
            if (null!=cacheTableBean){
                String keys=cacheTableBean.getTableKeys();
                String [] keyArray=keys.split(",");
                String [] idArray=new String[keyArray.length];
                for (int i=0;i<keyArray.length;i++) {
                    String key=keyArray[i];
                    String getMethod="get"+MybatisUtil.underline2Camel(key,false);
                    MethodAccess access = MethodAccess.get(dataObj.getClass());
                    String id=(String)access.invoke(dataObj,getMethod);
                    idArray[i]=id;
                    //System.out.println("id->"+id);
                }
                //TO ADD  idArray hash 排序 MD5 得到一个key set到redis中。并把对象放入redis。
                String cacheKey= getIdKey(tableName,idArray);
                ShareCacheDs.DEFAULT.removeCache(cacheKey);
            }
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }

    }
    @Deprecated
    public  static void deleteByKey(String dsName,Class cls,Object objKey){

        try {
            String className=cls.getSimpleName();
            className=className.substring(0,className.length()-2);
            String tableName= MybatisUtil.camel2Underline(className);
            String shareKey=dsName+"_"+tableName;
            CacheTableBean cacheTableBean= ShareCacheDs.DEFAULT.getCache(shareKey,CacheTableBean.class);
            if (null!=cacheTableBean){
                String [] idArray={String.valueOf(objKey)};
                String cacheKey= getIdKey(tableName,idArray);
                ShareCacheDs.DEFAULT.removeCache(cacheKey);
            }
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }


    }

    public static   boolean isCached(String dsName,Object objectExample){
        try {
            String tableName= MybatisUtil.getTableName(objectExample.getClass().getSimpleName());
            String shareKey=dsName+"_"+tableName;
            CacheTableBean cacheTableBean= ShareCacheDs.DEFAULT.getCache(shareKey,CacheTableBean.class);
            if (null!=cacheTableBean){
                return  true;
            }
            return  false;
        }catch (Exception e){
            logger.error("update cache error!{}",e.getMessage());
        }
        return false;
    }

    /**
     * 获得idKey
     * @param idArray
     * @return
     */
    public static String getIdKey(String tableName,String[] idArray){
        for (int i = 0; i <idArray.length ; i++) {
            String idi=idArray[i];
            for (int j = i; j <idArray.length ; j++) {
                String idj=idArray[j];
                if (idi.compareTo(idj)>0) {
                    idArray[i] = idj;
                    idArray[j] = idi;
                }
            }
        }
        String ids=tableName;
        for (String id: idArray) {
            //System.out.println(id);
            ids+=id;
        }
        String idmd5= MD5Util.MD5(ids);
        return  idmd5;
    }
    private  static   String captureName(String name) {
        //     name = name.substring(0, 1).toUpperCase() + name.substring(1);
//        return  name;
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }
}
