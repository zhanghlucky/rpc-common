package com.hui.zhang.common.util.http;

import com.hui.zhang.common.util.HessianEncoder;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.etc.AppParamsUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	private final static  RequestConfig requestConfig;

	private final static int CONNECT_TIMEOUT=10*1000;//链接超时时间10s
	private final static int SOCKET_TIMEOUT=30*1000;//socket超时时间30s
	private final static int CONNECT_REQUEST_TIMEOUT=30*1000;//等待响应超时时间30s

	private final static Map<String,String> HTTP_PROXY_MAP=new HashedMap();

	static{
		/*String httpProxyStr= AppParamsUtil.getParamValue("http.proxy");
		RequestConfig.Builder builder=RequestConfig.custom();
		builder.setSocketTimeout(SOCKET_TIMEOUT)
				.setConnectTimeout(CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT);

		if (StringUtils.isNotEmpty(httpProxyStr)){
			HTTP_PROXY_MAP= JsonEncoder.DEFAULT.decode(httpProxyStr,Map.class);
			boolean proxy=Boolean.valueOf(HTTP_PROXY_MAP.get("proxy"));
			if (proxy){
				HttpHost proxyHttpHost = new HttpHost(HTTP_PROXY_MAP.get("host"), Integer.valueOf(HTTP_PROXY_MAP.get("port")));
				requestConfig =builder.setProxy(proxyHttpHost).build();
				logger.info("使用HTTP代理 Host：{}，Port：{}",HTTP_PROXY_MAP.get("host"),HTTP_PROXY_MAP.get("port"));
			}else {
				requestConfig= builder.build();
			}
		}else {
			HTTP_PROXY_MAP=new HashedMap();
			requestConfig= builder.build();
		}*/

		RequestConfig.Builder builder=RequestConfig.custom();
		builder.setSocketTimeout(SOCKET_TIMEOUT)
				.setConnectTimeout(CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT);

		requestConfig= builder.build();
	}
	

	public static String doPost(String url,Map<String,String> map){
		logger.info("query:{}",url);
		CloseableHttpClient httpClient= Client.SSLClient(url);
		HttpPost  httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);


        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();  
        for (Map.Entry<String, String> entry : map.entrySet()) {  
        	 list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));  
        }
        if(list.size() > 0){  
            UrlEncodedFormEntity entity=null;
			try {
				entity = new UrlEncodedFormEntity(list,"utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("不支持的编码异常：{}",e);
				return  null;
			}
            httpPost.setEntity(entity);  
        }
		ResponseHandler<String> responseHandler=getPostResponseHandler(url,map);
		try {
			String  str=httpClient.execute(httpPost,responseHandler);
			return  str;
		} catch (IOException e) {
			logger.error("HTTP请求返回IO异常：{}",e);
			return null;
		}
    }



	private  static  ResponseHandler<String> getPostResponseHandler(String url,Map<String,String> map){
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(final HttpResponse response){
				if(response != null){
					HttpEntity resEntity = response.getEntity();
					if(resEntity != null){
						String result=null;
						try {
							result = EntityUtils.toString(resEntity,"utf-8");
						} catch (ParseException e) {
							logger.error("解析响应结果异常：{}",e);
							return null;
						} catch (IOException e) {
							logger.error("解析结果IO异常：{}",e);
							return null;
						}
						return  result;
					}
				}
				logger.error("返回结果为空");
				return  null;
			}
		};
		return  responseHandler;
	}

}
