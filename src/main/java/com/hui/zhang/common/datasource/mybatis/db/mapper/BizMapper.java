package com.hui.zhang.common.datasource.mybatis.db.mapper;

import com.github.pagehelper.PageInfo;
import com.hui.zhang.common.datasource.mybatis.db.ds.DataSourceSupport;
import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;
import com.hui.zhang.common.spring.SpringBeanUtil;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public class BizMapper implements  IBizMapper {
	private String namespace;
	private DataSourceSupport dataSourceSupport;
	/**
	 * 构建无分表 自定义sql mapper
	 * @param namespace 命名空间
	 */
	public BizMapper(String namespace){
		this.namespace=namespace;
		this.dataSourceSupport= SpringBeanUtil.getBeanByType(DataSourceSupport.class);
	}
	/**
	 * 插入
	 * @param insertId
	 * @param parameter
	 * @return
	 */
	public int insert(String insertId, Map parameter) {
		insertId=namespace+"."+insertId;
		return dataSourceSupport.insert(insertId, parameter);
	}

	/**
	 * 更新
	 * @param updateId
	 * @param parameter
	 * @return
	 */
	public int update(String updateId, Map parameter) {
		updateId=namespace+"."+updateId;
		return dataSourceSupport.update(updateId, parameter);
	}

	/**
	 * 删除
	 * @param deleteId
	 * @param parameter
	 * @return
	 */
	public int delete(String deleteId, Map parameter) {
		deleteId=namespace+"."+deleteId;
		return dataSourceSupport.delete(deleteId, parameter);
	}

	/**
	 * 查询一个
	 * @param selectId
	 * @param parameter
	 * @param <M>
	 * @return
	 */
	public <M> M selectOne(String selectId, Map parameter) {
		selectId=namespace+"."+selectId;
		return dataSourceSupport.selectOne(selectId, parameter);
	}

	/**
	 * 查询列表
	 * @param selectId
	 * @param parameter
	 * @param <E>
	 * @return
	 */
	public <E> List<E> selectList(String selectId, Map parameter) {
		selectId=namespace+"."+selectId;
		return dataSourceSupport.selectList(selectId, parameter);
	}

	/**
	 * 查询列表的第一个
	 * @param selectId
	 * @param parameter
	 * @param <M>
	 * @return
	 */
	public<M> M  selectFirstFromList(String selectId, Map parameter) {
		selectId=namespace+"."+selectId;
		List<M> ls=dataSourceSupport.selectList(selectId, parameter);
		if(ls.size()>0){
			return ls.get(0);
		}
		return null;
	}


	public <E> List<E> selectListForPage(String selectId, Map parameter, int page, int size) {
		selectId=namespace+"."+selectId;
		RowBounds rowBounds=new RowBounds(page,size);
		return dataSourceSupport.selectLimitedList(selectId, parameter, rowBounds);
	}

	/**
	 * 分页查询
	 * @param selectId
	 * @param parameter
	 * @param rowBounds
	 * @param <E>
	 * @return
	 */
	public <E> List<E> selectLimitedList(String selectId,Map parameter, RowBounds rowBounds) {
		selectId=namespace+"."+selectId;
		return dataSourceSupport.selectLimitedList(selectId, parameter, rowBounds);
	}

	/**
	 * 分页查询 DataPager
	 * @param selectId
	 * @param pager
	 * @param <T>
	 * @return
	 */
	public <T> DataPager<T> selectDataPager (String selectId, Map parameter, Pager pager){
		selectId=namespace+"."+selectId;
		RowBounds rowBounds = new RowBounds(pager.getPage(), pager.getSize());
		List<T> ls=dataSourceSupport.selectLimitedList(selectId, parameter, rowBounds);

		DataPager dataPager=new DataPager(pager);
		dataPager.setRows(ls);
		PageInfo page = new PageInfo(ls);
		dataPager.setTotalPage(page.getPages());
		dataPager.setTotal(page.getTotal());
		return  dataPager;
	}

	/**
	 * 查询总数
	 * @param countId
	 * @param parameter
	 * @return
	 */
	public int selectCount(String countId, Map parameter) {
		countId=namespace+"."+countId;
		return dataSourceSupport.selectOne(countId, parameter);
	}
}
