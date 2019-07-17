package com.hui.zhang.common.util.langs;

import com.hui.zhang.common.util.etc.AppConfigUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhanghui on 2018/1/16.
 */
public class Langs {
    private static final String langs;

    static {
        langs= AppConfigUtil.getCfgEnvironmentPO().getLangs();
    }
    /**
     * 是否中文
     * @return
     */
    public static   boolean isZhCn(){
        if(langs.equals("zh_CN")|| StringUtils.isEmpty(langs)){
            return true;
        }
        return false;
    }

    /**
     * 是否英文
     * @return
     */
    public  static  boolean isEnUs(){
        if(langs.equals("en_US")){
            return true;
        }
        return false;
    }

}
