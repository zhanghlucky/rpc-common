package com.hui.zhang.common.datasource.mybatis.pager;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DataFormat {
	private final static String FORMAT_TYPE_DATE="FORMAT_TYPE_DATE";
	private final static String FORMAT_TYPE_MONEY="FORMAT_TYPE_MONEY";
	private final static String FORMAT_TYPE_MAP_VALUE="FORMAT_TYPE_MAP_VALUE";

	public Map<String ,FormatParam> paramMap=new HashMap<String, FormatParam>();
	
	public void formatDate(String field,String dateFormatter){
		paramMap.put(field,new FormatParam(field, dateFormatter, FORMAT_TYPE_DATE));
	}
	public void formatMoney(String field){
		paramMap.put(field,new FormatParam(field, null, FORMAT_TYPE_MONEY));
	}

	public void formatMapValue(String field,Map<String,String> map){
		paramMap.put(field,new FormatParam(field, map, FORMAT_TYPE_MAP_VALUE));
	}
	
	/**
	 * 匹配所有数据
	 */
	public String dataFormat(FormatParam formatParam, String value){
			String val="";
			if(formatParam.getFormatType().equals(FORMAT_TYPE_DATE)){//匹配日期
				val=this.foramtDate(formatParam,value);
			}
			if(formatParam.getFormatType().equals(FORMAT_TYPE_MONEY)){//匹配金钱
				val= this.foramtMoney(formatParam,value);
			}
			if(formatParam.getFormatType().equals(FORMAT_TYPE_MAP_VALUE)){//匹配状态
				val= this.foramtMapValue(formatParam,value);
			}
			return val;
	}
	private String foramtDate(FormatParam formatParam, String value){
		if (StringUtils.isNotEmpty(value)&&!"0".equals(value)){
			Date date=new Date();
			date.setTime(Long.valueOf(value));
			SimpleDateFormat df=new SimpleDateFormat((String)formatParam.getPattern());
			return df.format(date);
		}
		return "";

	}
	private String foramtMoney(FormatParam formatParam, String value){
		//TODO 后期待实现
		String result="";
		return result;
	}
	private String foramtMapValue(FormatParam formatParam, String value) {
		String result = "";
		Map<String, String> pattern = (Map<String, String>) formatParam.getPattern();
		result=pattern.get(value);
		return result;
	}

}


