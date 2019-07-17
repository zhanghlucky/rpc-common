package com.hui.zhang.common.datasource.mybatis.db.mapper;


import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;

import java.util.List;


public interface ITableMapper {

    /**
     * 通过主键查询
     * @param cls
     * @param key
     * @param <M>
     * @return
     */
    //public <M> M  selectByPrimaryKey(Class<M> cls,Object key);

    /**
     * 通过主键删除
     * @param cls
     * @param key
     * @param <M>
     * @return
     */
   //public <M> int deleteByPrimaryKey(Class<M> cls,Object key);
    /**
     * 通过expample查询列表
     * @param example
     * @param <E>
     * @return
     */
    public <E> List<E> selectByExample(Object example);

    /**
     * 通过example查询类别数据
     * @param example
     * @param <E>
     * @return
     */
    public <E> List<E>  selectByExampleWithBLOBs(Object example);

    /**
     * 通过example查询列表第一个
     * @param example
     * @param <M>
     * @return
     */
    public <M> M selectFirstByExample(Object example);

    /**
     * 通过example分页查询
     * @param example
     * @param page
     * @param size
     * @param <E>
     * @param <M>
     * @return
     */
    public <E,M> List<E> selectByExampleForPage(M example, int page, int size);

    /**
     * 通过example查询总数
     * @param example
     * @return
     */
    public int countByExample(Object example);

    /**
     * 通过example删除
     * @param example
     * @return
     */
    public int deleteByExample(Object example);

    /**
     * 插入数据
     * @param parameter
     * @return
     */
    public int  insert(Object parameter);

    /**
     * 有选择性插入数据
     * @param record
     * @return
     */
    public int insertSelective(Object record);

    /**
     * 通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExample(Object record, Object example);

    /**
     * 有选择性的通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExampleSelective(Object record, Object example);

    /**
     * 有选择性的通过主键更新
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective(Object record);

    /**
     * 通过主键更新
     * @param record
     * @param <M>
     * @return
     */
    public <M> int updateByPrimaryKey(Object record) ;

    /**
     * 获得分页对象
     * @param example
     * @param pager
     * @param <T>
     * @param <M>
     * @return
     */
    public <T,M> DataPager<T> selectDataPager(M example, Pager pager);


}
