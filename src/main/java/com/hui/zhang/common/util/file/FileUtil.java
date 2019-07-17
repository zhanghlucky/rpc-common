/**   
* @Title: FileUtil.java 
* @Package com.nebula.common.utils.file 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhanghui   
* @date 2015年11月23日 下午5:38:01 
* @version V1.0   
*/
package com.hui.zhang.common.util.file;

import com.hui.zhang.common.util.buffer.OsPathBuffer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/** 
* @ClassName: FileUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhanghui
* @date 2015年11月23日 下午5:38:01 
*  
*/
public class FileUtil {
	/**
	 * 
	  * isImage(是否图片)
	  *
	  * @Title: isImage
	  * @Description: TODO
	  * @param @param suffix
	  * @param @return    
	  * @return boolean    
	  * @throws
	 */
	public static  boolean isImage(String suffix){
		if(null==suffix){
			return false;
		}
		
		for (ImageSuffix imageSuffix : ImageSuffix.values()){
			if (imageSuffix.toString().toLowerCase().equals(suffix.toLowerCase())) {
				return true;
			}
		}  
		return false;
	}
	/**
	 * 
	  * isVideo(是否是视频)
	  *
	  * @Title: isVideo
	  * @Description: TODO
	  * @param @param suffix
	  * @param @return    
	  * @return boolean    
	  * @throws
	 */
	public static boolean isVideo(String suffix){
		for (VideoSuffix videoSuffix : VideoSuffix.values()){
			if (videoSuffix.toString().toLowerCase().equals(suffix.toLowerCase())) {
				return true;
			}
		}  
		
		return false;
	}
	
	public static String getFileSuffix(String url){
		int start=url.lastIndexOf(".")+1;
		int end=url.length();
		/**
		if(end-start>4){
			return "jpg";
		}
		**/
		if(start==0){
			return null;
		}
		String suffix="";
		try {
			suffix=url.substring(start,end) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return suffix;
	}
	public static String getFileName(String url){
		int start=url.lastIndexOf("/")+1;
		int end=url.lastIndexOf(".");
		if(start>=end){
			return url.substring(start,url.length()) ;
		}
		String s="";
		try {
			s=url.substring(start,end) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String nextBasePath(){
		String basePath="static";//Comm.CFG_MAP.get("file.path");
		String typePath="images";
		Date date=new Date();
		SimpleDateFormat dfyyyy=new SimpleDateFormat("yyyy"); 
		SimpleDateFormat dfyyyyMMdd=new SimpleDateFormat("yyyyMMdd"); 
		String year=dfyyyy.format(date);
		String yearMonthDay=dfyyyyMMdd.format(date);
		String num="";
		
		String dateDir=new OsPathBuffer().append(typePath).append(year).append(yearMonthDay).toString();
		File dateDirFile = new File(dateDir);   
        if (!dateDirFile.exists()) {//不存在 创建   
        	dateDirFile.mkdirs();   
        	num="1001";//初始
        }else{
        	List<Integer> nameList=new ArrayList<>();
        	File[] listFileArray = dateDirFile.listFiles();
        	for (File listFile : listFileArray) {
        		nameList.add(Integer.valueOf(listFile.getName()));
			}
        	
        	if(nameList.size()>0){
        		num=maxNum(nameList);
        	}else{
        		num="1001";
        	}
        	String fullDir=new OsPathBuffer().append(basePath).append(typePath).append(year).append(yearMonthDay).append(num).toString(); 
        	
        	File fullDirFile = new File(fullDir); 
        	if (!fullDirFile.exists()) {//不存在 创建   
        		fullDirFile.mkdirs();   
            }
        	
        	File[] leafListFile=fullDirFile.listFiles();;
        	if(leafListFile.length>=5000){
        		num=Integer.valueOf(num)+1+"";
        	}
        }  
        
        String filePath=new OsPathBuffer().append(basePath).append(typePath).append(year).append(yearMonthDay).append(num).toString();
		return filePath;
	}
	
	public static String maxNum(List<Integer> nameList){
		for (int i = 0; i < nameList.size(); i++) {
			int nameI=nameList.get(i);
			for (int j = i+1; j < nameList.size(); j++) {
				int nameJ=nameList.get(j);
				int temp=0;
				if(nameI<nameJ){
					//temp=nameI;
					nameList.set(i, nameJ);
					nameList.set(j, nameI);
				}
			}
		}
		int max=nameList.get(0);
		
		return String.valueOf(max);
	}
	
}
