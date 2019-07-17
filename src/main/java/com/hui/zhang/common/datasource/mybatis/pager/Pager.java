package com.hui.zhang.common.datasource.mybatis.pager;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

public class Pager implements Serializable {
	private Integer page = 1; // 页号
	private Integer size = 20;// 每页记录数
	private Integer dsize = 100;// 默认参数
	public Pager(){

	}
	public Pager(Map params){
		if (null!=params.get("page")&& StringUtils.isNotEmpty((String)params.get("page"))) {
			this.page=Integer.parseInt(params.get("page").toString());
		}
		if (null!=params.get("rows")&&StringUtils.isNotEmpty((String)params.get("rows"))) {
			this.size=Integer.parseInt(params.get("rows").toString());
		}
	}

	public Pager(HttpServletRequest request){
		if (StringUtils.isNotEmpty(request.getParameter("page"))) {
			this.page=Integer.parseInt(request.getParameter("page"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("rows"))) {
			this.size=Integer.parseInt(request.getParameter("rows"));
		}
	}
	public Pager(Map params, int size){
		if (null!=params.get("page")&&StringUtils.isNotEmpty((String)params.get("page"))) {
			this.page=Integer.parseInt(params.get("page").toString());
		}
		this.size=size;
	}
	public Pager(int page, int size){
		this.size=size;
		this.page=page;
	}
	public Pager(int page){
		this.size=dsize * page;
		this.page=(page - 1) * this.dsize;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public static void main(String [] agrs){
		Pager pager = new Pager(1);
		System.out.println(JSON.toJSONString(pager));
	}
}
