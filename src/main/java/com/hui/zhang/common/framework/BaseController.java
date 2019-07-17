package com.hui.zhang.common.framework;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseController {
	protected String SUCCESS = "success";
	protected Map<String, Object> cookies2Map(HttpServletRequest request) {
		Map<String, Object> cookieMap =new HashMap<>();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie c : cookies) {
				cookieMap.put(c.getName(), c.getValue());
			}
		}

		return cookieMap;
	}

	protected Map<String, Object> paramsToMap(HttpServletRequest request) {
		Map<String, Object> param = new HashMap<>();
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (StringUtils.isNotEmpty(name)&&StringUtils.isNotEmpty(request.getParameter(name))){
				try {
					if (name.contains("[]")){
						String[] valueArray=request.getParameterValues(name);
						name=name.replace("[]","");
						param.put(name,valueArray);
					}else{
						String value=java.net.URLDecoder.decode(request.getParameter(name), "UTF-8").trim();
						param.put(name,value);
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		return param;
	}
	
	protected void paramsToModel(HttpServletRequest request, Model model, String[] paramsKey) {
		Map<String, Object> params = paramsToMap(request);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			boolean flag = false;
			if (null != paramsKey) {
				for (String key : paramsKey) {
					if (key.equals(entry.getKey())) {
						flag = true;
						break;
					}
				}
			} else {
				flag = true;
			}

			if (flag) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * 创建数据分页类
	 * 
	 * @param params
	 * @return
	 */
	protected static DataPager createDataPager(Map params) {
		DataPager dataPager = new DataPager();
		if (null != params.get("page") && StringUtils.isNotEmpty((String) params.get("page"))) {
			dataPager.setPage(Integer.parseInt(params.get("page").toString()));
		}
		if (null != params.get("size") && StringUtils.isNotEmpty((String) params.get("size"))) {
			dataPager.setSize(Integer.parseInt(params.get("size").toString()));
		}
		return dataPager;
	}

	/**
	 * 
	 * ajaxSuccess(异步操作完成)
	 *
	 * @Title: ajaxSuccess @Description: TODO @param @param param @param @param
	 *         successMsg @param @param errorMsg @param @return @return
	 *         String @throws
	 */
	protected String ajaxSuccess(Object param, String successMsg, String errorMsg) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();

		if (param instanceof Integer) {
			int value = ((Integer) param).intValue();
			if (value > 0) {
				node.put("success", true);
				node.put("count", value);
				node.put("msg", successMsg);
			} else {
				node.put("success", false);
				node.put("count", value);
				node.put("msg", errorMsg);
			}

		} else if (param instanceof String) {
			String s = (String) param;
		} else if (param instanceof Double) {
			double d = ((Double) param).doubleValue();
		} else if (param instanceof Float) {
			float f = ((Float) param).floatValue();
		} else if (param instanceof Long) {
			long l = ((Long) param).longValue();
		} else if (param instanceof Boolean) {
			boolean b = ((Boolean) param).booleanValue();
			if (b) {
				node.put("success", true);
				node.put("msg", successMsg);
			} else {
				node.put("success", false);
				node.put("msg", errorMsg);
			}
		} else if (param instanceof Date) {
			Date d = (Date) param;
		}

		String json = "";
		try {
			json = mapper.writeValueAsString(node);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 
	 * ajaxSuccess(异步操作完成)
	 *
	 * @Title: ajaxSuccess @Description: TODO @param @param
	 *         param @param @return @return String @throws
	 */
	protected String ajaxSuccess(Object param) {
		return this.ajaxSuccess(param, "操作成功！", "操作失败！");
	}
}
