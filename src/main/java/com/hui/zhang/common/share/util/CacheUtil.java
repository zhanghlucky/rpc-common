package com.hui.zhang.common.share.util;

import com.hui.zhang.common.datasource.mybatis.db.ds.MybatisUtil;
import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.redis.db.RedisDB;
import com.hui.zhang.common.share.annotation.CacheMapper;
import com.hui.zhang.common.share.cache.DataCache;
import com.hui.zhang.common.share.cache.ShareCacheDs;
import com.hui.zhang.common.util.CentaurBeanUtils;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghui on 2018/1/19.
 */
public class CacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);

    /**
     * 缓存中组合 po 到 vo  DataPager
     * @param poDataPager
     * @param voClass
     * @param <PO>
     * @param <VO>
     * @return
     */
    public static <PO,VO> DataPager<VO> dataPagerJoinCache( DataPager<PO> poDataPager,Class<VO> voClass){
        List<PO> list=poDataPager.getRows();
        List<VO> ls=new ArrayList<>();
        for (PO po:list) {
            VO vo=objectJoinCache(po,voClass);
            ls.add(vo);
        }
        DataPager<VO> voDataPager=new DataPager(ls,poDataPager.getTotal(),poDataPager.getPager());
        return  voDataPager;
    }

    /**
     * 缓存中组合shard po 到vo Dp
     * @param poDataPager
     * @param shardName
     * @param voClass
     * @param <PO>
     * @param <VO>
     * @return
     */
    public static <PO,VO> DataPager<VO> dataPagerJoinCache( DataPager<PO> poDataPager,String shardName,Class<VO> voClass){
        List<PO> list=poDataPager.getRows();
        List<VO> ls=new ArrayList<>();
        for (PO po:list) {
            //VO vo=objectJoinCache(po,voClass);
            VO vo=objectJoinShardCache(shardName,po,voClass);
            ls.add(vo);
        }
        DataPager<VO> voDataPager=new DataPager(ls,poDataPager.getTotal(),poDataPager.getPager());
        return  voDataPager;
    }

    /**
     * 缓存中组合 po 到 vo  List
     * @param poList
     * @param voClass
     * @param <PO>
     * @param <VO>
     * @return
     */
    public static <PO,VO> List<VO> listJoinCache( List<PO> poList,Class<VO> voClass){
        List<PO> list=poList;
        List<VO> ls=new ArrayList<>();
        for (PO po:list) {
            VO vo=objectJoinCache(po,voClass);
            ls.add(vo);
        }
        return  ls;
    }

    /**
     * 缓存中组合 po 到 vo  List
     * @param poList
     * @param voClass
     * @param <PO>
     * @param <VO>
     * @return
     */
    public static <PO,VO> List<VO> listJoinCacheShard( String shardName,List<PO> poList,Class<VO> voClass){
        List<PO> list=poList;
        List<VO> ls=new ArrayList<>();
        for (PO po:list) {
            VO vo=objectJoinShardCache(shardName,po,voClass);
            ls.add(vo);
        }
        return  ls;
    }


    /**
     * 缓存中组合 po 到 vo
     * @param po
     * @param voClass
     * @param <PO>
     * @param <VO>
     * @return
     */
    public static <PO,VO> VO objectJoinCache(PO po,Class<VO> voClass){
            VO vo= CentaurBeanUtils.copyBeanProperties(po,voClass);
            MethodAccess poAccess = MethodAccess.get(po.getClass());
            MethodAccess voAccess = MethodAccess.get(vo.getClass());
            Field[] fields = vo.getClass().getDeclaredFields();
            for (Field field : fields) {
                if(null!=field.getAnnotation(CacheMapper.class)){
                    String fieldName=field.getName();
                    String voSetMethod="set"+captureName(fieldName);

                    CacheMapper ann=field.getAnnotation(CacheMapper.class);
                    String[] keyArray= ann.cacheKey();
                    String[] valueArray= new String[keyArray.length];
                    Class cls= ann.cls();
                    String valueColumn= ann.valueColumn();
                    for (int i = 0; i <keyArray.length ; i++) {
                        String key=keyArray[i];
                        String keyGetMethod="get"+captureName(key);
                        Object keyValue=voAccess.invoke(vo, keyGetMethod, null);//反射取值
                        valueArray[i]=String.valueOf(keyValue);
                    }
                    String tabaleName= MybatisUtil.getTableName(cls);
                    String cacheKey=DataCache.getIdKey(tabaleName,valueArray);
                    Map<String,Object> dataMap= ShareCacheDs.DEFAULT.getCache(cacheKey,Map.class);
                    if (null!=dataMap){
                        Object valueObj=dataMap.get(valueColumn);
                        voAccess.invoke(vo, voSetMethod, valueObj);//赋值
                    }else {
                        logger.error("缓存中无值：tabaleName:{},valueArray:{}",tabaleName,valueArray);
                    }

                }
            }
        return  vo;
    }


    /**
     * 缓存中组合 po 到 vo
     * @param po
     * @param voClass
     * @param <PO>
     * @param <VO>
     * @return
     */
    public static <PO,VO> VO objectJoinShardCache( String shardName,PO po,Class<VO> voClass){
        VO vo= CentaurBeanUtils.copyBeanProperties(po,voClass);
        MethodAccess poAccess = MethodAccess.get(po.getClass());
        MethodAccess voAccess = MethodAccess.get(vo.getClass());
        Field[] fields = vo.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(null!=field.getAnnotation(CacheMapper.class)){
                String fieldName=field.getName();
                String voSetMethod="set"+captureName(fieldName);

                CacheMapper ann=field.getAnnotation(CacheMapper.class);
                String[] keyArray= ann.cacheKey();
                String[] valueArray= new String[keyArray.length];
                Class cls= ann.cls();
                String valueColumn= ann.valueColumn();
                for (int i = 0; i <keyArray.length ; i++) {
                    String key=keyArray[i];
                    String keyGetMethod="get"+captureName(key);
                    Object keyValue=voAccess.invoke(vo, keyGetMethod, null);//反射取值
                    valueArray[i]=String.valueOf(keyValue);
                }
                String tabaleName = null;//= ann.tableName();
                if (null != ann.tableName() && cls.getName().equals("java.lang.String")){
                    tabaleName =  ann.tableName();
                }
                else{
                    tabaleName= MybatisUtil.getTableName(cls);
                }
                if (StringUtils.isNotEmpty(ann.shardName())){
                    shardName = ann.shardName();
                }
                String cacheKey=DataCache.getIdKey(tabaleName,valueArray);
                ShareCacheDs shareCacheDs = new ShareCacheDs(shardName,null);
                Map<String,Object> dataMap= shareCacheDs.getShareCache(tabaleName,cacheKey,Map.class); // 读取map
                if (null!=dataMap){
                    Object valueObj=dataMap.get(valueColumn);
                    voAccess.invoke(vo, voSetMethod, valueObj);//赋值
                }else {
                    logger.error("缓存中无值：tabaleName:{},valueArray:{}",tabaleName,valueArray);
                }

            }
        }
        return  vo;
    }


//    private static  void getCatchValue(Object mapperValue,int index,String[] mapperArray,Map<String,Object> vmap) throws  Exception{
//        int next=index+1;
//        if (next<mapperArray.length){
//            String mapperKey=mapperArray[next];
//            String []  mapperKeyArray=mapperKey.split("#");
//            String joinPO=null;
//            if (mapperKeyArray.length==2){
//                joinPO=mapperKeyArray[0];
//                mapperKey=mapperKeyArray[1];
//            }
//
//            String objJson= ShareCacheDs.DEFAULT.getCache((String)mapperValue);//"{'departmentId':'1001','departmentName':'技术部'}";//keyValue 从缓存中读出
//            if (StringUtils.isNotEmpty(objJson)){
//                ObjectMapper mapper=new ObjectMapper();
//                mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//                Map<String,Object> map= mapper.readValue(objJson,Map.class);
//                mapperValue= map.get(mapperKey);
//                index=index+1;
//                vmap.put("value",mapperValue);
//                getCatchValue(mapperValue,index,mapperArray,vmap);
//            }else{
//                logger.error("无法取得共享缓存数据 key{}：,value:{}",mapperKey,mapperValue);
//            }
//        }
//    }

    private  static   String captureName(String name) {
        //     name = name.substring(0, 1).toUpperCase() + name.substring(1);
//        return  name;
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }
}
