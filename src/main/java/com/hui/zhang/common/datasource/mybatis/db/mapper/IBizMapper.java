package com.hui.zhang.common.datasource.mybatis.db.mapper;


import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;

import java.util.List;
import java.util.Map;


public interface IBizMapper {
    /**
     * 插入
     * @param insertId
     * @param parameter
     * @return
     */
    public int insert(String insertId, Map parameter);

    /**
     * 更新
     * @param updateId
     * @param parameter
     * @return
     */
    public int update(String updateId, Map parameter);

    /**
     * 删除
     * @param deleteId
     * @param parameter
     * @return
     */
    public int delete(String deleteId, Map parameter);

    /**
     * 查询一个
     * @param selectId
     * @param parameter
     * @param <M>
     * @return
     */
    public <M> M selectOne(String selectId, Map parameter);

    /**
     * 查询列表
     * @param selectId
     * @param parameter
     * @param <E>
     * @return
     */
    public <E> List<E> selectList(String selectId, Map parameter) ;

    /**
     * 查询列表的第一个
     * @param selectId
     * @param parameter
     * @param <M>
     * @return
     */
    public<M> M  selectFirstFromList(String selectId, Map parameter);

    /**
     * 分页查询
     * @param selectId
     * @param parameter
     * @param page
     * @param size
     * @param <E>
     * @return
     */
    public <E> List<E> selectListForPage(String selectId, Map parameter, int page, int size);

    /**
     * 查询总数
     * @param countId
     * @param parameter
     * @return
     */
    public int selectCount(String countId, Map parameter);

    /**
     * 获得分页对象
     * @param selectId
     * @param parameter
     * @param pager
     * @param <T>
     * @return
     */
    public <T> DataPager<T> selectDataPager(String selectId, Map parameter, Pager pager);
}
