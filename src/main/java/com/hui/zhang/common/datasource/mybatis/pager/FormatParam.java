package com.hui.zhang.common.datasource.mybatis.pager;

public class FormatParam {
	private String field;
	private Object pattern;
	private String formatType;
	
	public FormatParam(){}
	public FormatParam(String field, Object pattern, String formatType){
		this.field=field;
		this.pattern=pattern;
		this.formatType=formatType;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Object getPattern() {
		return pattern;
	}
	public void setPattern(Object pattern) {
		this.pattern = pattern;
	}
	public String getFormatType() {
		return formatType;
	}
	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	
	
}
