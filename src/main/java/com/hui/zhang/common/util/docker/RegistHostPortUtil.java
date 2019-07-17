package com.hui.zhang.common.util.docker;

import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.ip.IpGetter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhanghui on 2017/11/30.
 */
public class RegistHostPortUtil {
    private static final Logger logger = LoggerFactory.getLogger(RegistHostPortUtil.class);

    private  final  static  String DOCKER_PROTOCOL_PORT="DOCKER_PROTOCOL_PORT";
    private  final  static  String DOCKER_WEB_PORT="DOCKER_WEB_PORT";

    private  static int PROTOCOL_PORT=0;
    private  static int WEB_PORT=0;

    static {
        //int port= AppPropertyUtil.getAppConfig(appName).getProtocolPort();
        int port= AppConfigUtil.getCfgAppPO().getDubboProtocolPort();
        PROTOCOL_PORT=port;
        //int webPort=AppPropertyUtil.getAppConfig(appName).getWebPort();
        int webPort=AppConfigUtil.getCfgAppPO().getWebPort();
        WEB_PORT=webPort;
    }

    /**
     * 获得注册协议服务端口
     * @return
     */
    public static int getRegistProtocolPort(){
        String port=System.getenv(DOCKER_PROTOCOL_PORT);//docker 获得环境变量PORT
        if (StringUtils.isEmpty(port)){
            return  PROTOCOL_PORT;
        }
        return Integer.valueOf(port.trim());
    }

    /**
     * 获得注册服务WEB端口
     * @return
     */
    public static int getRegistWebPort(){
        String port=System.getenv(DOCKER_WEB_PORT);//docker 获得环境变量PORT
        if (StringUtils.isEmpty(port)){
            return  WEB_PORT;
        }
        return Integer.valueOf(port);
    }

    /**
     * 获得注册HOST
     * @return
     */
    public static String getRegistHost(){
        String ip="0.0.0.0";
        try {
            ip=IpGetter.getLocalIP();
        } catch (Exception e) {
            logger.error("获取IP异常：{}",e);
        }
        return ip;
    }

}
