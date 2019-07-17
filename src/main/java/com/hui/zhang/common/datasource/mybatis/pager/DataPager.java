package com.hui.zhang.common.datasource.mybatis.pager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataPager<T> implements Serializable{
	private List<T> rows; // 当页记录
	private long page = 1l; // 页号
	private long size = 20l;// 每页记录数
	private long totalPage = 0l; //总页数
	private long total = 0l; // 总记录数
	private Pager pager;
	public DataPager() {

	}
	public DataPager(Pager pager) {
		this.page = pager.getPage();
		this.size = pager.getSize();
		this.pager=pager;
	}
	public DataPager(List<T> rows,long totalSize,Pager pager) {
		this.rows=rows;
		this.page = pager.getPage();
		this.size = pager.getSize();
		this.total = totalSize;
		if (size==0){
			this.totalPage=0;
		}else{
			if (this.total%this.size!=0){
				this.totalPage=this.total/this.size+1;

			}else{
				this.totalPage=this.total/this.size;
			}
		}
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	/**
	 * 转成json数据
	 *
	 * @return
	 */
	public String toJson(DataFormat dataFormat) {
		if (null == dataFormat) {
			dataFormat = new DataFormat();
		}
		dataFormat.formatDate("createTime", "yyyy-MM-dd HH:mm:ss");
		dataFormat.formatDate("updateTime", "yyyy-MM-dd HH:mm:ss");

		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = JSON.toJSONString(this);
			JSONObject root = JSON.parseObject(json);
			JSONArray rows = root.getJSONArray("rows");
			JSONArray jsonArray = new JSONArray();
			if (!CollectionUtils.isEmpty(rows)) {
				for (int i = 0; i < rows.size(); i++) {
					JSONObject rowNode = rows.getJSONObject(i);
					for (Map.Entry<String, FormatParam> entry : dataFormat.paramMap.entrySet()) {
						String field = entry.getKey();
						if (null != rowNode && null != rowNode.get(field)) {
							String value = dataFormat.dataFormat(entry.getValue(), rowNode.get(field).toString());
							rowNode.put(field + "_name", value);
						}
					}
					jsonArray.add(rowNode);
				}
			}

			JSONObject rootObj = new JSONObject();
			rootObj.put("rows", jsonArray);
			rootObj.put("page", root.getInteger("page"));
			rootObj.put("size", root.getInteger("size"));
			rootObj.put("total", root.getInteger("total"));
			json = rootObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static  void main(String [] args){
		Pager pager = new Pager(2,100);
		DataPager d = new DataPager(pager);
		d.setTotal(1);

	}


}
