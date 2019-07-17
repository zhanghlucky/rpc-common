package com.hui.zhang.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class JsonEncoder implements StringEncoder {
    public static final JsonEncoder DEFAULT = new JsonEncoder();
    private static  final ObjectMapper mapper ;
    private static final Logger logger = LoggerFactory.getLogger(JsonEncoder.class);
    static {
        mapper = new ObjectMapper();
    }
    public JsonEncoder() {

    }

    public<T> String encode(T t) {
        String json= null;
        try {
            json= JSON.toJSONString(t);
            return  json;
        } catch (Exception e) {
            logger.error("对象转json异常：{}",e);
        }
        return  json;
    }
    public<T> String prettyEncode(T t) {
        String json= null;
        try {
            json =JSON.toJSONString(t,true);

        } catch (Exception e) {
            logger.error("对象转json异常：{}",e);
        }
        return json;
    }

    public <T> T decode(String value, Class<T> clazz) {
        try {
            //System.out.println(value);
            T t=JSON.parseObject(value,clazz);
            return t;
        } catch (Exception e) {
            logger.error("json转对象异常：{}",e);
        }
        return null;
    }

    public <T> T decode(String value, Class<T> clazz, Class... constructClazzs) {
        JavaType javaType=null;
        if (!clazz.getSimpleName().equals(Map.class.getSimpleName())
                &&constructClazzs.length==2){
            JavaType dataType = mapper.getTypeFactory().constructParametricType(constructClazzs[0],constructClazzs[1]);
            javaType =mapper.getTypeFactory().constructParametricType(clazz, dataType);
        }else{
            javaType =mapper.getTypeFactory().constructParametricType(clazz, constructClazzs);
        }
        try {
            T t=mapper.readValue(value, javaType);
            return t;
        } catch (IOException e) {
            logger.error("json转对象异常：{}",e.getMessage());
            e.printStackTrace();
        }
        return  null;
    }

}