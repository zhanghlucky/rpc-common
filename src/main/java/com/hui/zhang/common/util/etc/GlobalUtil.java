package com.hui.zhang.common.util.etc;

import com.hui.zhang.common.util.DESUtil;
import com.hui.zhang.common.util.EnvPropertyUtil;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.buffer.UrlPathBuffer;
import com.hui.zhang.common.util.http.HttpClientUtil;
import com.hui.zhang.common.util.ip.IpGetter;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhanghui on 2017/10/12.
 */
public class GlobalUtil {
    private static final Logger logger = LoggerFactory.getLogger(AppParamsUtil.class);

    private static Map<String, String> globalMap = new HashMap<>();

    static {
        loadGlobalConfig();
    }
    private static void loadGlobalConfig(){
        try {
            String filePath="/etc/global.config";
            String os = System.getProperty("os.name");
            boolean isWin=false;
            if(os.toLowerCase().startsWith("win")){
                filePath="C:\\etc\\global.config";
                isWin=true;
            }
            File file=new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.contains("=")){
                    String keyvalue[]=line.split("=");
                    String key=keyvalue[0].trim();
                    String value=keyvalue[1].trim();
                    globalMap.put(key,value);
                }
            }
            br.close();

            String pattern= PropertyUtil.getProperty("config.pattern");
            //控制环境ID是否随着系统环境切换而改变
            if (isWin&&(null==pattern||pattern.equals("server"))){
                if(null!=globalMap.get("env.auto")){
                    String cfgPattern=globalMap.get("env.auto");
                    if (cfgPattern.equals("true")){
                        //System.out.println(IpGetter.getLocalIP());
                        String host=globalMap.get("cfg.host");
                        String ip=IpGetter.getLocalIP();
                        String key=globalMap.get("cfg.key");
                        String ipCiphertext = DESUtil.getInstance().encode(ip, key);
                        host = new UrlPathBuffer(host).append("/cfg/api/local-env").append(ipCiphertext).toString();
                        Map<String, String> params = new HashedMap();
                        String envIdCiphertext = HttpClientUtil.doPost(host, params);
                        String envId=DESUtil.getInstance().decode(envIdCiphertext,key);
                        if(StringUtils.isNotEmpty(envId)){
                            globalMap.put("env.id",envId);
                            logger.info("环境ID跟随环境切换改变为：{}",envId);
                        }else {
                            logger.warn("环境ID在配置管理中未读取到，使用配置文件的环境ID");
                        }
                    }
                }else {
                    logger.info("使用配置文件的环境ID");
                }
            }else {
                logger.info("使用配置文件的环境ID");
            }
            logger.info("global.config 配置为 {}",globalMap.toString());
        } catch (Exception e) {
            logger.error("请检查C:\\etc\\global.config 或者 /etc/global.config 文件是否存在，错误：{}",e);
        }
    }
    public static String getConfig(String key) {
        if (null!=globalMap.get(key)){
            return globalMap.get(key).toString();
        }
        return null;
    }
}
