package com.hui.zhang.common.datasource.mybatis.db.mapper;


import com.esotericsoftware.reflectasm.MethodAccess;
import com.github.pagehelper.PageInfo;
import com.hui.zhang.common.datasource.mybatis.db.ds.DataSourceSupport;
import com.hui.zhang.common.datasource.mybatis.db.ds.MybatisUtil;
import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;
import com.hui.zhang.common.spring.SpringBeanUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public  class TableSplitMapper implements ITableMapper {
    protected DataSourceSupport dataSourceSupport;
    private Long key=null;

    /**
     * 构建分表mapper
     * @param key 分表key
     */
    public TableSplitMapper(long key){
        this.dataSourceSupport= SpringBeanUtil.getBeanByType(DataSourceSupport.class);
        this.key=key;
    }

    /**
     * 通过expample查询列表
     * @param example
     * @param <E>
     * @return
     */
    public <E> List<E> selectByExample(Object example){
        this.initModKey(example);
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
        this.initModKey(example);
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
        this.initModKey(example);
        String selectId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.selectByExample;
        List<M> ls=this.dataSourceSupport.selectList(selectId, example);
        if(ls.size()>0){
            return  ls.get(0);
        }
        return null;
    }

    @Override
    public <E, M> List<E> selectByExampleForPage(M example, int page, int size) {
        this.initModKey(example);
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
        this.initModKey(example);
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
     * 通过example查询总数
     * @param example
     * @return
     */
    public int countByExample(Object example){
        this.initModKey(example);
        String countId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.countByExample;
        int count=this.dataSourceSupport.selectCount(countId, example);
        return count;
    }

    /**
     * 通过example删除
     * @param example
     * @return
     */
    public int deleteByExample(Object example){
        this.initModKey(example);
        String deleteId= MybatisUtil.getNamespace(example.getClass())+ExampleEnum.deleteByExample;
        int count=this.dataSourceSupport.delete(deleteId, example);
        return count;
    }

    /**
     * 插入数据
     * @param parameter
     * @return
     */
    public int  insert(Object parameter){
        this.initModKey(parameter);
        String insertId= MybatisUtil.getNamespace(parameter.getClass())+ExampleEnum.insert;
        int count=this.dataSourceSupport.insert(insertId, parameter);
        return count;
    }

    /**
     * 有选择性插入数据
     * @param parameter
     * @return
     */
    public int insertSelective(Object parameter){
        this.initModKey(parameter);
        String insertId= MybatisUtil.getNamespace(parameter.getClass())+ExampleEnum.insertSelective;
        int count=this.dataSourceSupport.insert(insertId, parameter);
        return count;
    }

    /**
     * 通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExample(Object record, Object example) {
        this.initModKey(example);
        MapperMethod.ParamMap<Object> pm=new MapperMethod.ParamMap<>();
        pm.put("record", record);
        pm.put("example", example);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByExample;
        int obj=this.dataSourceSupport.update(updateId, pm);
        return obj;
    }

    /**
     * 有选择性的通过example更新
     * @param record
     * @param example
     * @return
     */
    public int updateByExampleSelective(Object record,Object example){
        this.initModKey(example);
        MapperMethod.ParamMap<Object> pm=new MapperMethod.ParamMap<>();
        pm.put("record", record);
        pm.put("example", example);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByExampleSelective;
        int obj=this.dataSourceSupport.update(updateId, pm);
        return obj;
    }

    /**
     * 有选择性的通过主键更新
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective(Object record) {
        this.initModKey(record);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByPrimaryKeySelective;
        int obj=this.dataSourceSupport.update(updateId, record);
        return obj;
    }

    /**
     * 通过主键更新
     * @param record
     * @param <M>
     * @return
     */
    public <M> int updateByPrimaryKey(Object record) {
        this.initModKey(record);
        String updateId= MybatisUtil.getNamespace(record.getClass())+ExampleEnum.updateByPrimaryKey;
        int obj=this.dataSourceSupport.update(updateId, record);
        return obj;
    }

    /**
     * 初始化分表  className
     * @param obj
     */
    private void initModKey(Object obj){
        if (null!=key){
            MethodAccess methodAccess= MethodAccess.get(obj.getClass());
            methodAccess.invoke(obj, "generatorTableName", key);
        }else{
            String tableName= MybatisUtil.getTableName(obj.getClass());
            MethodAccess methodAccess=MethodAccess.get(obj.getClass());
            methodAccess.invoke(obj, "setTableName", tableName);
        }
    }
}
