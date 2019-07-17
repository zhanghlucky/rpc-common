/**   
* TODO 
* @author zhanghui   
* @date 2016年4月14日 下午4:11:36  
*/
package com.hui.zhang.common.util;

import java.net.*;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/** 
* TODO
* @author zhanghui
* @date 2016年4月14日 下午4:11:36  
*/
public class IpUtils {
	public static String getIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
            //这里主要是获取本机的ip,可有可无  
	         if (ip.equals("127.0.0.1")|| ip.endsWith("0:0:0:0:0:0:1")) {  
	             // 根据网卡取本机配置的IP  
	             InetAddress inet = null;  
	             try {  
	                 inet = InetAddress.getLocalHost();  
	             } catch (Exception e) {  
	                 e.printStackTrace();  
	             }  
	             ip = inet.getHostAddress();  
	         }
        }  
        return ip;  
    }
    public static String getLoalhostIP(){
        String localhostIp = null;
        try {
            Enumeration<?> enumeration=NetworkInterface.getNetworkInterfaces();
            InetAddress ip=null;
            while(enumeration.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) enumeration.nextElement();
                Enumeration<?> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    //System.out.println("服务地址:" + ip.getHostName());
                    if (ip != null && ip instanceof Inet4Address){
                        String ip1=ip.getHostAddress();
                        //System.out.println("本机所有的IP地址:"+ip1);
                        if (!ip1.equals("127.0.0.1")){
                            localhostIp =ip1;
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return localhostIp;
    }

    public static void main(String[] args) {
        System.out.println(getLoalhostIP());
    }

}
