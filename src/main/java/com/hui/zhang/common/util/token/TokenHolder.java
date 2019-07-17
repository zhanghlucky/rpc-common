package com.hui.zhang.common.util.token;

import com.hui.zhang.common.util.AESUtil;
import com.hui.zhang.common.util.etc.AppParamsUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zuti on 2017/12/28.
 * email zuti@centaur.cn
 */
public class TokenHolder {

	public static final String TOKEN_SPLIT_CHAR = ":";

	public static final String TOKEN_DEFAULT_SECRET_KEY = "6dY5z^hz!uB15e9U";

	private static final Integer TERMINAL_DEFAULT = 0;  //默认terminal

	public static String TOKEN_SECRET_KEY;

	private TokenHolder(){}

	/**
	 * 生成token
	 * @param clIp
	 * @param id
	 * @param terminal
	 * @return
	 */
	public static String getToken(String clIp, String id, Integer terminal) {

//		Asserts.notBlank(clIp, "clIp");
//		Asserts.notBlank(id, "id");

		StringBuilder sb = new StringBuilder();
		sb.append(clIp)
				.append(TOKEN_SPLIT_CHAR)
				.append(id)
				.append(TOKEN_SPLIT_CHAR)
				.append(terminal == null ? TERMINAL_DEFAULT : terminal);
		String token= AESUtil.AESEncode(getSecretKey(), sb.toString());

		return token;
	}

	/**
	 * 获取ip
	 * @param token
	 * @return
	 */
	public static String getIp(String token) {

		if(StringUtils.isNotEmpty(token)){
			String dtoken=AESUtil.AESDncode(getSecretKey(), token);
			if (StringUtils.isNotEmpty(dtoken)){
				return dtoken.split(TOKEN_SPLIT_CHAR)[0];
			}else{
				return null;
			}
		}
		return null;
	}

	/**
	 * 获取id
	 * @param token
	 * @return
	 */
	public static String getId(String token) {
		if(StringUtils.isNotEmpty(token)){
			String dtoken=AESUtil.AESDncode(getSecretKey(), token);
			if (StringUtils.isNotEmpty(dtoken)){
				return dtoken.split(TOKEN_SPLIT_CHAR)[1];
			}else{
				return null;
			}
		}
		return null;


	}

	/**
	 * 获取terminal
	 * @param token
	 * @return
	 */
	public static Integer getTerminal(String token) {
		if(StringUtils.isNotEmpty(token)){
			String dtoken=AESUtil.AESDncode(getSecretKey(), token);
			if (StringUtils.isNotEmpty(dtoken)){
				return Integer.valueOf(dtoken.split(TOKEN_SPLIT_CHAR)[2]);
			}else{
				return -1;
			}
		}
		return -1;
	}

	public static String[] splitToken(String token) {
		return AESUtil.AESDncode(getSecretKey(), token).split(TOKEN_SPLIT_CHAR);
	}

	private static String getSecretKey() {
		if (null==TOKEN_SECRET_KEY){
			TOKEN_SECRET_KEY=	AppParamsUtil.getParamValue("tokenSecretKey", TOKEN_DEFAULT_SECRET_KEY);
		}
		return TOKEN_SECRET_KEY;
	}

	public static void main(String[] args) {
		String token = AESUtil.AESEncode(TOKEN_DEFAULT_SECRET_KEY, "192.168.1.2:7788945:1");
		System.out.println(token);
	}

}
