package com.hui.zhang.common.datasource.mybatis.db.mapper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hui.zhang.common.share.cache.DataCache;
import com.hui.zhang.common.share.cache.ShareCacheDs;
import com.hui.zhang.common.share.util.CacheUtil;
import com.hui.zhang.common.util.CentaurBeanUtils;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.etc.bean.CacheTableBean;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.hui.zhang.common.datasource.mybatis.db.ds.DataSourceSupport;
import com.hui.zhang.common.datasource.mybatis.db.ds.MybatisUtil;
import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;
import com.hui.zhang.common.spring.SpringBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.RowBounds;
import org.omg.CORBA.DATA_CONVERSION;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.Cache;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TableMapper implements ITableMapper {
    private String dsName;
    private static  DataSourceSupport dataSourceSupport;

    static {
        dataSourceSupport= SpringBeanUtil.getBeanByType(DataSourceSupport.class);
    }
    public TableMapper(String dsName){
        this.dsName=dsName;
    }


    /**
     * 通过主键查询
     * @param cls
     * @param key
     * @param <M>
     * @return
     */
    public <M> M  selectByPrimaryKey(Class<M> cls,Object key){
        String selectId= MybatisUtil.getNamespace(cls)+ ExampleEnum.selectByPrimaryKey;
        M m=this.dataSourceSupport.selectOne(selectId, key);
        return m;
    }



    /**
     * 通过expample查询列表
     * @param example
     * @param <E>
     * @return
     */
    public <E> List<E> selectByExample(Object example){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ ExampleEnum.selectByExample;
        List<E> ls=this.dataSourceSupport.selectList(selectId, example);
        return ls;
    }

    /**
     * 通过example查询类别数据
     * @param example
     * @param <E>
     * @return
     */
    public <E> List<E>  selectByExampleWithBLOBs(Object example){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExampleWithBLOBs;
        List<E> ls=this.dataSourceSupport.selectList(selectId, example);
        return ls;
    }

    /**
     * 通过example查询列表第一个
     * @param example
     * @param <M>
     * @return
     */
    public <M> M selectFirstByExample(Object example){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
       /* List<M> ls=this.dataSourceSupport.selectList(selectId, example);
        if(ls.size()>0){
            return  ls.get(0);
        }*/

        RowBounds rowBounds = new RowBounds(1, 1);
        List<M> ls=this.dataSourceSupport.selectLimitedList(selectId, example, rowBounds);
        if(ls.size()>0){
            return  ls.get(0);
        }
        return null;
    }

    /**
     * 通过example查询总数
     * @param example
     * @return
     */
    public int countByExample(Object example){
        String countId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.countByExample;
        int count=this.dataSourceSupport.selectCount(countId, example);
        return count;
    }

    public long countByExampleLong(Object example){
        String countId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.countByExample;
        long count=this.dataSourceSupport.selectCountLong(countId, example);
        return count;
    }


    /**
     * 通过example删除
     * @param example
     * @return
     */
    public int deleteByExample(Object example){
        String deleteId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.deleteByExample;
        // cache start
        boolean flag= DataCache.isCached(dsName,example);
        if (flag){
            //先通过Example查出所有数据，之后再删除...可能会影响效率。
            List<Object> list= this.selectByExample(example);
            for (Object obj:list) {
                DataCache.deleteByObject(dsName,obj);
            }
        }
        // cache end

        int count=this.dataSourceSupport.delete(deleteId, example);
        return count;
    }

    /**
     * 插入数据
     * @param parameter
     * @return
     */
    public int  insert(Object parameter){
        String insertId= MybatisUtil.getNamespace(parameter.getClass())+ExampleEnum.insert;
        int count=this.dataSourceSupport.insert(insertId, parameter);
        DataCache.insertUpdate(dsName,parameter);//写入数据缓存 以后考虑线程池
        return count;
    }

    /**
     * 有选择性插入数据
     * @param parameter
     * @return
     */
    public int insertSelective(Object parameter){
        String insertId= MybatisUtil.getNamespace(parameter.getClass())+ExampleEnum.insertSelective;
        int count=this.dataSourceSupport.insert(insertId, parameter);
        DataCache.insertUpdate(dsName,parameter);//写入数据缓存 以后考虑线程池
        return count;
    }

    /**
     * 通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExample(Object record, Object example) {
        MapperMethod.ParamMap<Object> pm=new MapperMethod.ParamMap<Object>();
        pm.put("record", record);
        pm.put("example", example);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByExample;
        int obj=this.dataSourceSupport.update(updateId, pm);
        DataCache.insertUpdate(dsName,record);
        return obj;
    }

    /**
     * 有选择性的通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExampleSelective(Object record,Object example){
        MapperMethod.ParamMap<Object> pm=new MapperMethod.ParamMap<Object>();
        pm.put("record", record);
        pm.put("example", example);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByExampleSelective;
        int obj=this.dataSourceSupport.update(updateId, pm);
        DataCache.insertUpdate(dsName,record);
        return obj;
    }

    /**
     * 有选择性的通过主键更新
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective(Object record) {
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByPrimaryKeySelective;
        int obj=this.dataSourceSupport.update(updateId, record);
        DataCache.insertUpdate(dsName,record);
        return obj;
    }

    /**
     * 通过主键更新
     * @param record
     * @param <M>
     * @return
     */
    public <M> int updateByPrimaryKey(Object record) {
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByPrimaryKey;
        int obj=this.dataSourceSupport.update(updateId, record);
        DataCache.insertUpdate(dsName,record);
        return obj;
    }

    /**
     * 通过主键删除
     * @param cls
     * @param key
     * @param <M>
     * @return
     */
    public <M> int deleteByPrimaryKey(Class<M> cls,Object key){
        String deleteId= MybatisUtil.getNamespace(cls)+ExampleEnum.deleteByPrimaryKey;
        int count=this.dataSourceSupport.delete(deleteId, key);
        DataCache.deleteByKey(dsName,cls,key);
        return count;
    }

    /**
     * 通过Example分页查询
     * @param example
     * @param page 页码
     * @param size 大小
     * @param <E>
     * @param <M>
     * @return
     */
    public <E,M> List<E> selectByExampleForPage(M example, int page,int size){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
        RowBounds rowBounds = new RowBounds(page, size);
        List<E> ls=this.dataSourceSupport.selectLimitedList(selectId, example, rowBounds);
        return ls;
    }

    /**
     * 通过Example分页查询 DataPager
     * @param example
     * @param pager
     * @param <T>
     * @param <M>
     * @return
     */
    public <T,M> DataPager<T> selectDataPager (M example, Pager pager){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
        RowBounds rowBounds = new RowBounds(pager.getPage(), pager.getSize());
        List<T> ls=this.dataSourceSupport.selectLimitedList(selectId, example, rowBounds);

        DataPager dataPager=new DataPager(pager);
        dataPager.setRows(ls);
        PageInfo page = new PageInfo(ls);
        dataPager.setTotalPage(page.getPages());
        dataPager.setTotal(page.getTotal());
        return  dataPager;
    }


    /**
     * ###################################
     * 以下都是基于共享缓存查询组合的拓展方法
     *####################################
     * */
    /**
     * deleteByExample
     */
    public int deleteByExampleShardCache(String shardCacheName,Object example){
        String deleteId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.deleteByExample;
        // cache start
        boolean flag= DataCache.isCached(shardCacheName,example);
        if (flag){
            //先通过Example查出所有数据，之后再删除...可能会影响效率。
            List<Object> list= this.selectByExample(example);
            for (Object obj:list) {
                DataCache.shardUpdate(shardCacheName,obj,false);
            }
        }
        // cache end
        int count=this.dataSourceSupport.delete(deleteId, example);
        return count;
    }
    /**
     * insert shard
     */
    public int  insertShard(String shardCacheName,Object parameter){
        String insertId= MybatisUtil.getNamespace(parameter.getClass())+ExampleEnum.insert;
        int count=this.dataSourceSupport.insert(insertId, parameter);
        DataCache.shardInsert(shardCacheName,parameter);//写入数据缓存 以后考虑线程池
        return count;
    }
    /**
     * insertSelectiveSahrd
     */
    public int insertSelectiveShard(String shardCacheName,Object parameter){
        String insertId= MybatisUtil.getNamespace(parameter.getClass())+ExampleEnum.insertSelective;
        int count=this.dataSourceSupport.insert(insertId, parameter);
        DataCache.shardInsert(shardCacheName,parameter);//写入数据缓存 以后考虑线程池
        return count;
    }
    /**
     *  通过example更新shard
     */
    public int updateByExample(String shardCacheName,Object record, Object example) {
        MapperMethod.ParamMap<Object> pm=new MapperMethod.ParamMap<Object>();
        pm.put("record", record);
        pm.put("example", example);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByExample;
        int obj=this.dataSourceSupport.update(updateId, pm);
        DataCache.shardUpdate(shardCacheName,record,true);
        return obj;
    }
     /*
     *  有选择性的通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExampleSelectiveShard(String shardCacheName,Object record,Object example){
        MapperMethod.ParamMap<Object> pm=new MapperMethod.ParamMap<Object>();
        pm.put("record", record);
        pm.put("example", example);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByExampleSelective;
        int obj=this.dataSourceSupport.update(updateId, pm);
        DataCache.shardUpdate(shardCacheName,record,true);
        return obj;
    }
    /**
     * 有选择性的通过主键更新 shard
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelectiveShard(String shardCacheName,Object record) {
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByPrimaryKeySelective;
        int obj=this.dataSourceSupport.update(updateId, record);
        DataCache.shardUpdate(shardCacheName,record,true);
        return obj;
    }
    /**
     * 通过主键删除
     * @param cls
     * @param key
     * @param <M>
     * @return
     */
    public <M> int deleteByPrimaryKeyShard(String shardCacheName,Class<M> cls,Object key){
        String deleteId= MybatisUtil.getNamespace(cls)+ExampleEnum.deleteByPrimaryKey;
        int count=this.dataSourceSupport.delete(deleteId, key);
        DataCache.deleteByKeyShard(shardCacheName,cls,key);
        return count;
    }
    /**
     * 通过主键更新
     * @param record shard
     * @param <M>
     * @return
     */
    public <M> int updateByPrimaryKeyShard(String shardCacheName,Object record) {
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByPrimaryKey;
        int obj=this.dataSourceSupport.update(updateId, record);
        DataCache.shardUpdate(shardCacheName,record,true);
        return obj;
    }

    /**
     * 通过主键查询 查询shard
     */
    public <PO,VO> VO  selectByPrimaryKeyJonCacheShard(String shardCacheName,Class<PO> poCls,Object key,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(poCls)+ ExampleEnum.selectByPrimaryKey;
        PO po=this.dataSourceSupport.selectOne(selectId, key);
        VO vo=CacheUtil.objectJoinShardCache(shardCacheName,po,voCls);
        return vo;
    }

    /**
     * 通过expample查询列表 shard
     */
    public <VO> List<VO> selectByExampleJonCacheShard(String shardCacheName,Object example,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ ExampleEnum.selectByExample;
        List ls=this.dataSourceSupport.selectList(selectId, example);
        List<VO> voList=CacheUtil.listJoinCacheShard(shardCacheName,ls,voCls);
        return voList;
    }


    public  <T,VO> DataPager<T>  selectByExampleForPageShard(String shardCacheName,Object example, Pager pager,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
        RowBounds rowBounds = new RowBounds(pager.getPage(), pager.getSize());
        List  ls=this.dataSourceSupport.selectLimitedList(selectId, example, rowBounds);
        List<VO> voList=CacheUtil.listJoinCacheShard(shardCacheName,ls,voCls);

        DataPager dataPager=new DataPager(pager);
        dataPager.setRows(voList);
        PageInfo page = new PageInfo(ls);
        dataPager.setTotalPage(page.getPages());
        dataPager.setTotal(page.getTotal());

        return dataPager;
    }




    /**
     * 通过example查询列表第一个 shard
     */
    public <VO> VO selectFirstByExampleJonCacheShard(String shardCacheName,Object example,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
        List ls=this.dataSourceSupport.selectList(selectId, example);
        if(ls.size()>0){
            Object po=ls.get(0);
            VO vo=CacheUtil.objectJoinShardCache(shardCacheName,po,voCls);
            return vo;
        }
        return null;
    }
    /**
    /**
     * 通过主键查询
     */
    public <PO,VO> VO  selectByPrimaryKeyJonCache(Class<PO> poCls,Object key,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(poCls)+ ExampleEnum.selectByPrimaryKey;
        PO po=this.dataSourceSupport.selectOne(selectId, key);
        VO vo=CacheUtil.objectJoinCache(po,voCls);
        return vo;
    }

    /**
     * 通过expample查询列表
     */
    public <VO> List<VO> selectByExampleJonCache(Object example,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ ExampleEnum.selectByExample;
        List ls=this.dataSourceSupport.selectList(selectId, example);
        List<VO> voList=CacheUtil.listJoinCache(ls,voCls);
        return voList;
    }

    /**
     * 通过example查询类别数据
     */
    public <VO> List<VO>  selectByExampleWithBLOBsJonCache(Object example,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExampleWithBLOBs;
        List ls=this.dataSourceSupport.selectList(selectId, example);
        List<VO> voList=CacheUtil.listJoinCache(ls,voCls);
        return voList;
    }

    /**
     * 通过example查询列表第一个
     */
    public <VO> VO selectFirstByExampleJonCache(Object example,Class<VO> voCls){
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
        List ls=this.dataSourceSupport.selectList(selectId, example);
        if(ls.size()>0){
            Object po=ls.get(0);
            VO vo=CacheUtil.objectJoinCache(po,voCls);
            return vo;
        }
        return null;
    }




}
