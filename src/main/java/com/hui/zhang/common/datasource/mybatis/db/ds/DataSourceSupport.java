package com.hui.zhang.common.datasource.mybatis.db.ds;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.List;

public class DataSourceSupport extends SqlSessionDaoSupport {
	
	public int insert(String insertId, Object parameter) {
		return getSqlSession().insert(insertId, parameter);
	}

	public int update(String updateId, Object parameter) {
		return getSqlSession().update(updateId, parameter);
	}
	
	public int delete(String deleteId, Object parameter) {
		return getSqlSession().delete(deleteId, parameter);
	}

	public <M> M selectOne(String selectId, Object parameter) {
		return getSqlSession().selectOne(selectId, parameter);
	}

	public <E> List<E> selectList(String selectId, Object parameter) {
		return getSqlSession().selectList(selectId, parameter);
	}

	public <E> List<E> selectLimitedList(String selectId,Object parameter, RowBounds rowBounds) {
		return getSqlSession().selectList(selectId, parameter,rowBounds);
	}
	public int selectCount(String countId, Object parameter) {
		return getSqlSession().selectOne(countId, parameter);
	}

	public long selectCountLong(String countId, Object parameter) {
		return getSqlSession().selectOne(countId, parameter);
	}
}
