package com.hui.zhang.common.rocketmq.config;

import com.aliyun.oss.OSSClient;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2019-03-25 14:10
 **/
public class RocketMqConfigBean {
    public static final Map<String,String> CLIENT_MAP=new ConcurrentHashMap<>(); // 使用线程安全map ，不必加线程安全锁，提升性能
    private  static String NAMESRV_ADDR;
    private  static String accessKeyId;
    private  static String accessKeySecret;
    static {
            accessKeyId = AppConfigUtil.getCfgEnvironmentPO().getAccessKeyId().trim();//AppParamsUtil.getParamValue("accessKeyId","LTAIHEjHbRJW5gwO");
            accessKeySecret = AppConfigUtil.getCfgEnvironmentPO().getAccessKeySecret().trim();//AppParamsUtil.getParamValue("accessKeySecret","U9N7kA6b9Ty9Hw9l1tQ7h0sxqiSYIV");
            NAMESRV_ADDR  = AppConfigUtil.getCfgEnvironmentPO().getMqAddress().trim();
            String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
            if (env.contains("PRD") || env.contains("prd")){
                CLIENT_MAP.put("AccessKey",accessKeyId);
                CLIENT_MAP.put("Access",accessKeySecret);
            }
            else{
                CLIENT_MAP.put("AccessKey","LTAIHEjHbRJW5gwO");
                CLIENT_MAP.put("Access","U9N7kA6b9Ty9Hw9l1tQ7h0sxqiSYIV");
            }
            CLIENT_MAP.put("NAMESRV_ADDR", NAMESRV_ADDR);
            //CLIENT_MAP.put("NAMESRV_ADDR", "ERRO:90");

    }
}
