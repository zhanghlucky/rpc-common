/**   
* @Title: VideoUtil.java 
* @Package com.nebula.common.utils.file 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhanghui   
* @date 2015年11月26日 下午1:51:15 
* @version V1.0   
*/
package com.hui.zhang.common.util.file;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/** 
* @ClassName: VideoUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhanghui
* @date 2015年11月26日 下午1:51:15 
*  
*/
public class VideoUtil {
    public static String processFLV(String veido_path, String ffmpeg_path) {  
        List<String> commend=new java.util.ArrayList<String>();  
        commend.add(ffmpeg_path);
        commend.add("ffmpeg");  
        commend.add("-i");  
        commend.add(veido_path);  
        try {  
            ProcessBuilder builder = new ProcessBuilder();  
            builder.command(commend);  
            builder.redirectErrorStream(true);  
            Process p= builder.start();  
            BufferedReader buf = null; // 保存ffmpeg的输出结果流  
            String line = null;  
            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            StringBuffer sb= new StringBuffer();  
            while ((line = buf.readLine()) != null) {  
            	sb.append(line);  
            	continue;  
            }  
            int ret = p.waitFor();//这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行  
            String result=sb.toString();
            result=result.replaceAll("tv,", "");
            return result;  
        } catch (Exception e) {  
            return null;  
        }  
    }
}
