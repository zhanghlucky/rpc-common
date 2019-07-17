package com.hui.zhang.common.util.buffer;

public class OsPathBuffer {
	private String value="";
	
	public OsPathBuffer(){
		
	}
	public OsPathBuffer(String value){
		this.value=this.toFilePath(value);
	}
	/**
	 * 拼接系统路径
	 * @return
	 */
	public  OsPathBuffer append(String path){
		String npath=this.value+this.toFilePath(path);
		OsPathBuffer buf=new OsPathBuffer(npath);
		return buf;
	}
	public String toString(){
		String separator=System.getProperty("file.separator");
		if(value.indexOf(separator)==0&&!separator.equals("/")){
			value=value.substring(1, value.length());
		}
		return this.value;
	}
	
	
	/**
	 * 路径转换为当前系统路径
	 * @param path
	 * @return
	 */
	private  String toFilePath(String path){
		String regex="/{1,}|\\\\{1,}";
		String separator=System.getProperty("file.separator");
		String replacement="";
		if(separator.equals("/")){
			replacement="/";
		}else{
			replacement="\\\\";
		}
		path=path.replaceAll(regex, replacement);
		if(path.indexOf(separator)!=0){
			path=separator+path;
		}
		return path;
	}
	/**
	 * 
	  * toLinuxPath(转成linux路径)
	  *
	  * @Title: toLinuxPath
	  * @Description: TODO
	  * @param @param path
	  * @param @return    
	  * @return String    
	  * @throws
	 */
	public String toLinuxPathString(){
		String regex="/{1,}|\\\\{1,}";
		String separator="/";
		String replacement="";
		replacement="/";
		value=value.replaceAll(regex, replacement);
		if(value.indexOf(separator)==0){
			value=value.substring(1, value.length());
		}
		
		return value;
	}
	public static void main(String argus[]){
		System.out.println(new OsPathBuffer("/1/\\/2.2/3/4.jpg").toLinuxPathString());
	}
	
}
