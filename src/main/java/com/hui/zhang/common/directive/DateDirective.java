package com.hui.zhang.common.directive;

import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DateDirective implements TemplateDirectiveModel{

	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {

		String dateString="";
		Long date=0L;
		String df="yyyy-MM-dd HH:mm:ss";

		if(null!=params){
			if(null!=params.get("date")){
				if (StringUtils.isNotEmpty(params.get("date").toString())){
					date=Long.valueOf(params.get("date").toString());
				}
			}
			if(null!=params.get("df")){
				df=((SimpleScalar)params.get("df")).toString();
			}
		}
		SimpleDateFormat formatter = new SimpleDateFormat(df);
		if (date>0){
			dateString = formatter.format(new Date(date));
		}
		env.getOut().write(dateString);
	}  
  
}  