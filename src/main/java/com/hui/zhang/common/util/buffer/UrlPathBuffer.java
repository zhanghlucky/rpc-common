package com.hui.zhang.common.util.buffer;

public class UrlPathBuffer {
private String value="";
	
	public UrlPathBuffer(){
		
	}
	public UrlPathBuffer(String value){
		this.value=toWebPath(value);
	}
	
	/**
	 * 拼接HTTP URL
	 * @return
	 */
	public  UrlPathBuffer append(String path){
		String npath=this.value+"/"+toWebPath(path);
		UrlPathBuffer buf=new UrlPathBuffer(npath);
		return buf;
	}
	public String toString(){
		if(this.value.contains("?")){
			return this.value.substring(0, this.value.length());
		}
		return this.value;
	}
	
	/**
	 * 路径转换为web路径
	 * @param path
	 * @return
	 */
	public static String toWebPath(String path){
		String regex="/{1,}|\\\\{1,}";
		String separator="/";
		String replacement="/";
		path=path.replaceAll(regex, replacement);
		//if((path.lastIndexOf(separator)+1)!=path.length()){
			//path=path+separator;
		//}
		if(path.indexOf(separator)==0){
			path=path.substring(1, path.length());
		}
		path=path.replace("http:/", "http://");
		path=path.replace("https:/", "https://");
		return path;
	}

}
