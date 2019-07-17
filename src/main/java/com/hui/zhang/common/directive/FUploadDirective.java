package com.hui.zhang.common.directive;

import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.UUIDGenerator;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

public class FUploadDirective implements TemplateDirectiveModel{

	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		
		StringBuffer htmlBuffer=new StringBuffer();
		String id= UUIDGenerator.random32UUID();
		String butname="上传文件";
		String objectId="";
		String batch="";
		String pattern="win";
		String types="*";
		String group="";
		String size="102400";
		String isShowFiles="true";
		String limit="10";
		String uploadCompleteFun="";
		String cover="false";
		String table="default";
		String project= AppConfigUtil.getCfgAppPO().getAppName();//取项目名
		String priority="0";
		String butclass="btn-default";
		String buticon="fa-upload";
		if(null!=params){
			if(null!=params.get("id")){
				id=((SimpleScalar)params.get("id")).toString();
			}
			if(null!=params.get("butname")){
				butname=((SimpleScalar)params.get("butname")).toString();
			}
			if(null!=params.get("types")){
				types=((SimpleScalar)params.get("types")).toString();
			}

			if(null!=params.get("size")){
				size=((SimpleScalar)params.get("size")).toString();
			}
			if(null!=params.get("isShowFiles")){
				isShowFiles=((SimpleScalar)params.get("isShowFiles")).toString();
			}
			if(null!=params.get("limit")){
				limit=((SimpleScalar)params.get("limit")).toString();
			}
			if(null!=params.get("uploadCompleteFun")){
				uploadCompleteFun=((SimpleScalar)params.get("uploadCompleteFun")).toString();
			}

			if(null!=params.get("pattern")){
				pattern=((SimpleScalar)params.get("pattern")).toString();
			}


			if(null!=params.get("project")){
				project=((SimpleScalar)params.get("project")).toString();
			}
			if(null!=params.get("table")){
				table=((SimpleScalar)params.get("table")).toString();
			}
			if(null!=params.get("objectId")&& StringUtils.isNotEmpty(params.get("objectId").toString())){
				objectId=((SimpleScalar)params.get("objectId")).toString();
			}else{
				if(null!=params.get("batch")&&StringUtils.isNotEmpty(params.get("batch").toString())){
					batch=((SimpleScalar)params.get("batch")).toString();
				}else{
					batch=UUIDGenerator.random32UUID();
				}
			}
			if(null!=params.get("group")){
				group=((SimpleScalar)params.get("group")).toString();
			}
			if(null!=params.get("priority")){
				priority=((SimpleScalar)params.get("priority")).toString();
			}
			if(null!=params.get("cover")){
				cover=((SimpleScalar)params.get("cover")).toString();
			}
			if(null!=params.get("butclass")){
				butclass=((SimpleScalar)params.get("butclass")).toString();
			}
			if(null!=params.get("buticon")){
				buticon=((SimpleScalar)params.get("buticon")).toString();
			}

		}
		
		if(pattern.equals("direct")){
			//limit="1";
			//htmlBuffer.append("<span id=\""+id+"\"><span id='file_holder_"+id+"' ></span><i class='upmsg'></i></span> \n");
		}else{
			//htmlBuffer.append("<span id=\""+id+"\">").append(butname).append("</span> \n");
		}

		htmlBuffer.append("<span class=\"btn "+butclass+" purple fileinput-button\" ><i class=\"fa "+buticon+"\"></i>");
		htmlBuffer.append("<span>"+butname+"</span>");
		htmlBuffer.append("<input id=\""+id+"\" type=\"file\"  name=\"filedata\" multiple></input>");
		htmlBuffer.append("</span>");

		String jupload_server=AppConfigUtil.getCfgEnvironmentPO().getUploadHost();
		//String jfiles_server=AppPropertyUtil.getEnvironmentConfig().getFileServerHost();
		
		htmlBuffer.append("<script type=\"text/javascript\" >");
		htmlBuffer.append("$(document).ready(function(){");
		htmlBuffer.append("	 $(\"#"+id+"\").jupload({");
		htmlBuffer.append("		id:'"+id+"',");
		htmlBuffer.append("		jupload_server:'"+jupload_server+"',");
		//htmlBuffer.append("		jfiles_server:'"+jfiles_server+"',");
		htmlBuffer.append("		but_name:'"+butname+"',");
		htmlBuffer.append("		file_size:"+size+",");
		htmlBuffer.append("		types:'"+types+"',");
		htmlBuffer.append("		pattern:'"+pattern+"',");
		htmlBuffer.append("		limit:"+limit+",");
		htmlBuffer.append("		upload_complete_fun:"+uploadCompleteFun+",");
		htmlBuffer.append("		params:{'batch':'"+batch+"','project':'"+project+"','table':'"+table+"','objectId':'"+objectId+"','group':'"+group+"','priority':'"+priority+"','cover':"+cover+"}");
		htmlBuffer.append("	});");
		htmlBuffer.append("});");
		htmlBuffer.append("</script>");

		/*htmlBuffer.append("<span class=\"btn btn-default purple fileinput-button\"> <i class=\"fa fa-upload\"></i> \n" +
						 "    <span>"+butname+"</span>\n" +
						 "    <input id=\""+id+"\" type=\"file\"  name=\"filedata\" multiple></input>\n" +
						 "</span>");

		htmlBuffer.append("<script type=\"text/javascript\">\n" +
				"    (function(){\n" +
				"        $('#"+id+"').fileupload({\n" +
				"            url: '"+fupload_url+"',\n" +
				"            forceIframeTransport: true," +
				"            formData: {'batch':'"+batch+"','project':'"+project+"','table':'"+table+"','objectId':'"+objectId+"','group':'"+group+"','priority':'"+priority+"','cover':"+cover+"}," +
				"            add: function (e, data) {\n" +
				"                var uploadErrors = [];\n" +
				"                var maxSize="+size+";\n");
				if(!"*".equals(types)){
					htmlBuffer.append(
							"var acceptFileTypes = /(\\.|\\/)("+types+")$/i"+
							"if(data.originalFiles[0]['name'].length && !acceptFileTypes.test(data.originalFiles[0]['name'])) {" +
				"                    uploadErrors.push('类型错误');\n" +
				"           }\n");
				}

				htmlBuffer.append("if (data.originalFiles[0]['size'] > maxSize) {\n" +
				"                    uploadErrors.push('文件超过大小限制'+(maxSize/1024)+'KB');\n" +
				"                }\n" +
				"                if(uploadErrors.length > 0) {\n" +
				"                    alert(uploadErrors.join(\"\\n\"));\n" +
				"                } else {\n" +
				"                    data.submit();\n" +
				"                }\n" +
				"            },\n" +
				"            done: function (e, data) {\n" );
				if(StringUtils.isNotEmpty(uploadCompleteFun)){
					htmlBuffer.append("console.log(data);\n"
							+uploadCompleteFun+"(fileData) \n");
				}
				htmlBuffer.append("},\n" +
				"            fail: function () {\n" +
				"                alert('文件传输失败！');\n" +
				"            }\n" +
				"        });\n" +
				"    })();" +
				"</script>");*/
	
		env.getOut().write(htmlBuffer.toString());  
	}  
  
}  