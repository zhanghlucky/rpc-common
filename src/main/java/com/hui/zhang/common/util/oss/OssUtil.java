package com.hui.zhang.common.util.oss;

import com.aliyun.oss.OSSClient;
import com.hui.zhang.common.util.buffer.UrlPathBuffer;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.AppParamsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-02-06 14:40
 **/
public class OssUtil {
    private static final Logger logger = LoggerFactory.getLogger(OssUtil.class);

    private  static OSSClient ossClient;
    private  static String filesBucket;
    private  static String endpoint;
    private  static String internalEndpoint;
    private  static String accessKeyId;
    private  static String accessKeySecret;
    private  static boolean isInternal=false;

    static {
        endpoint= AppConfigUtil.getCfgEnvironmentPO().getEndPoint().trim();//AppParamsUtil.getParamValue("endpoint","oss-cn-zhangjiakou.aliyuncs.com");
        internalEndpoint=AppConfigUtil.getCfgEnvironmentPO().getInternalEndPoint().trim();//AppParamsUtil.getParamValue("internalEndpoint","oss-cn-zhangjiakou-internal.aliyuncs.com");
        filesBucket= AppConfigUtil.getCfgEnvironmentPO().getFilesBucket().trim();//AppParamsUtil.getParamValue("filesBucket","centaur-files");
        accessKeyId = AppConfigUtil.getCfgEnvironmentPO().getAccessKeyId().trim();//AppParamsUtil.getParamValue("accessKeyId","LTAIHEjHbRJW5gwO");
        accessKeySecret = AppConfigUtil.getCfgEnvironmentPO().getAccessKeySecret().trim();//AppParamsUtil.getParamValue("accessKeySecret","U9N7kA6b9Ty9Hw9l1tQ7h0sxqiSYIV");
        String env= AppConfigUtil.getCfgEnvironmentPO().getEnvId().trim();
        //logger.info("env:{}",env);
        if (env.equals("prd")||env.equals("pre")){
            isInternal=true;
        }else {
            isInternal=false;
        }

    }

    public static String getEndpoint(){
        return endpoint;
    }

    public static String getInternalEndpoint(){
        return internalEndpoint;
    }

    public static OSSClient getOSSClient(){
        if (null!=ossClient){
            return ossClient;
        }
        if (isInternal){
            ossClient = new OSSClient("http://"+internalEndpoint, accessKeyId, accessKeySecret);
        }else{
            ossClient = new OSSClient("http://"+endpoint, accessKeyId, accessKeySecret);
        }
        return  ossClient;
    }

    public static String getFilesBucket(){
        return  filesBucket;
    }
    public static String getAccessKeyId(){return  accessKeyId;}
    public static String getAccessKeySecret(){return  accessKeySecret;}

    public static String getFileOssUrl(String ossKey){
        String url=new UrlPathBuffer("http://").append(filesBucket+"."+endpoint).append(ossKey).toString(); ;
        return url;

    }
    public static String getFileOssUrl(String bucket, String ossKey){
        String url=new UrlPathBuffer("http://").append(bucket +"."+endpoint).append(ossKey).toString(); ;
        return url;

    }
    public static String getFileOssInternalUrl(String ossKey){
        String url=new UrlPathBuffer("http://").append(filesBucket+"."+internalEndpoint).append(ossKey).toString(); ;
        return url;

    }
}
