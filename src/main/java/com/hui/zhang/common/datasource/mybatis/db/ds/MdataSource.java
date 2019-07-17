package com.hui.zhang.common.datasource.mybatis.db.ds;


import com.hui.zhang.common.datasource.mybatis.db.mapper.BizMapper;
import com.hui.zhang.common.datasource.mybatis.db.mapper.BizSplitMapper;
import com.hui.zhang.common.datasource.mybatis.db.mapper.TableMapper;
import com.hui.zhang.common.datasource.mybatis.db.mapper.TableSplitMapper;

/**
 * mybatis 数据源
 */
public class MdataSource {
	private String dsName;

	public MdataSource(String dsName){
		this.dsName=dsName;
	}


	/**
	 * 构建无分表自定义sql mapper
	 * @param daoName  dao名称（除去namespace的名称）
	 * @return
	 */
	public BizMapper mapper(String daoName){
		String namespace= "mapper.biz."+daoName;//SysConstances.CFG_MAP.get("business.namespace")+"."+daoName;
		BizMapper dao=new BizMapper(namespace);
		return dao;
	}

	/**
	 * 构建 mybatis标准mapper
	 * @return
	 */
	public  TableMapper mapper(){
		TableMapper dao=new TableMapper(dsName);
		return dao;
	}

	/**
	 * 构建分表 mybatis标准mapper
	 * @param key
	 * @return
	 */
	public TableSplitMapper mapper(long key){
		TableSplitMapper dao=new TableSplitMapper(key);
		return dao;
	}

	/**
	 * 构建含有分表自定义sql mapper
	 * @param key 分表key
	 * @param daoName dao名称（除去namespace的名称）
	 * @return
	 */
	public BizSplitMapper mapper(long key, String daoName){
		String namespace="mapper.biz."+daoName;//SysConstances.CFG_MAP.get("business.namespace")+"."+daoName;
		BizSplitMapper dao=new BizSplitMapper(namespace,key);
		return dao;
	}

}

