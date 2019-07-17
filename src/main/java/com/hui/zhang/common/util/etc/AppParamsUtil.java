package com.hui.zhang.common.util.etc;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;
import com.hui.zhang.common.datasource.mongo.util.MgoUtil;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.etc.bean.AppBean;
import com.hui.zhang.common.util.etc.bean.AppParamBean;
import com.hui.zhang.common.util.etc.po.CfgAppPO;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhanghui on 2017/10/13.
 */
public class AppParamsUtil extends BaseEtc {
    private static final Logger logger = LoggerFactory.getLogger(AppParamsUtil.class);

    static {

    }
    /**
     * 获得程序参数值
     * @param paramKey
     * @return
     */
    public static String getParamValue(String paramKey){
      return AppConfigUtil.getCfgAppParamValue(paramKey);
    }

    public static String getParamValue(String paramKey,String defaultValue){
        String value=getParamValue(paramKey);
        if(StringUtils.isNotEmpty(value)){
            return value;
        }
        return defaultValue;
    }

}
