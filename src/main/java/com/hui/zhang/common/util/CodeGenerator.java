package com.hui.zhang.common.util;

import java.util.Random;


public class CodeGenerator {

	/**
	 *  生成一个 N位数的验证码
	 * @param n
	 * @return
	 */
	public static int randomCode(int n){
		//return (int)((Math.random()*9+1)*Math.pow(10, n));
		int num=new Random().nextInt((int)Math.pow(10, n));
		//强制N 位验证码
		if(String.valueOf(num).trim().length() != n){
			return randomCode(n);
		}
		String str = String.format("%0"+n+"d", num);
		return Integer.valueOf(str);
	} 
	
	public static void main(String [] arugs){
		for (int i = 0; i < 1000; i++) {
			System.out.println(randomCode(6));
		}
	}
	
}
