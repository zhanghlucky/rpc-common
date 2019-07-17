package com.hui.zhang.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.MD5Util;
import com.hui.zhang.common.util.UUIDGenerator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Random;

/**
 * Created by zhanghui on 2019-06-06.
 */
public class TraceTool {

    private static volatile TraceTool instance = null;
    /**
     * 默认请求
     */
    public static  final String DEFAULT_REQUEST="0";

    /**
     * 接口请求
     */
    public static  final String ACTION_REQUEST="1";
    /**
     * 消息触发
     */
    public static  final String MQ_HANDLER_REQUEST="2";

    /**
     * 定时触发
     */
    public static final String TASK_REQUEST="3";


    private static final ThreadLocal<Map<String,Object>> threadLocal=new ThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String,Object>> parentThread = new TransmittableThreadLocal<Map<String,Object>>();

    //private String tradeHead= TraceTool.NO_REQUEST;

    private TraceTool(){}

    public static TraceTool getInstance(){
        if(instance == null){
            synchronized(TraceTool.class){
                if(instance == null){
                    instance = new TraceTool();
                }
            }
        }
        initThreadLocal();
        return instance;
    }

    private static void initThreadLocal(){
        if (null==threadLocal.get()){
            String spanId = UUIDGenerator.random16UUID().toLowerCase();//MD5Util.MD5(UUIDGenerator.random32UUID()+new Random().nextInt()); //new Random().nextInt(max)%(max-min+1) + min;//new Random().nextInt();
            Map<String,Object> map=new HashedMap();
            map.put("spanId",spanId);
            String parentId=null;
            String traceId=TraceTool.instance.randomTraceId(DEFAULT_REQUEST);
            String zipkinTraceId=null;
            String parentTraceId=null;
            if (null!=parentThread.get()){
                parentId=parentThread.get().get("spanId").toString();
                parentTraceId=parentThread.get().get("traceId").toString();
            }else{
                map.put("parentId",null);
                map.put("traceId",traceId);
                map.put("zipkinTraceId",zipkinTraceId);
                parentThread.set(map);
            }
            map.put("parentId",parentId);
            map.put("zipkinTraceId",zipkinTraceId);

            if(StringUtils.isNotEmpty(parentTraceId)){
                map.put("traceId",parentTraceId);
            }else{
                map.put("traceId",traceId);
                //map.put("spanId",traceId);
            }

            threadLocal.set(map);
        }
    }


    public void setTraceId(String traceId){
        //initThreadLocal();
        Map<String,Object> map=this.threadLocal.get();
        map.put("traceId",traceId);
        threadLocal.set(map);

        Map<String,Object> pmap=this.parentThread.get();
        pmap.put("traceId",traceId);
        parentThread.set(map);
    }

    public void setZipkinTraceId(String zipkinTraceId){
        //initThreadLocal();
        Map<String,Object> map=this.threadLocal.get();
        map.put("zipkinTraceId",zipkinTraceId);
        threadLocal.set(map);

    }


    public void setRandomTraceId(String head){
        //initThreadLocal();
        Map<String,Object> map=this.threadLocal.get();
        map.put("traceId",randomTraceId(head));
        threadLocal.set(map);
    }



    public String getTraceId(){
        //initThreadLocal();
        Map<String,Object> map=this.threadLocal.get();
        if (null!=map.get("traceId")){
            return (String)map.get("traceId");
        }
        return "";
    }

    public String getZipkinTraceId(){
        Map<String,Object> map=this.threadLocal.get();
        if (null!=map.get("zipkinTraceId")){
            return (String)map.get("zipkinTraceId");
        }
        return "";
    }

    public String getSpanId(){
        //initThreadLocal();
        Map<String,Object> map=this.threadLocal.get();
        if (null!=map.get("spanId")){
            return (String)map.get("spanId");
        }
        return "";
    }
    public String getParentId(){
        initThreadLocal();
        Map<String,Object> map=this.threadLocal.get();
        if (null!=map.get("parentId")){
            return (String)map.get("parentId");
        }
        return "";
    }


    private String randomTraceId(String head){
        if (StringUtils.isEmpty(head)){
            head=DEFAULT_REQUEST;
        }
        return  head+"_"+UUIDGenerator.random16UUID().toLowerCase();
    }
}
