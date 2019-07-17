package com.hui.zhang.common.datasource.mongo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.datasource.mongo.annotation.FieldMeta;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.hui.zhang.common.util.JsonEncoder;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MgoUtil {
    /**
     * Object 转成 Document
     * @param <T>
     * @return
     */
    public synchronized static   <T> Document toDocument(T t){
        Document document=new Document();
        //synchronized (t){
            MethodAccess methodAccess=MethodAccess.get(t.getClass());
            Field[] fs = t.getClass().getDeclaredFields();
            for(int i = 0 ; i < fs.length; i++){
                Field f = fs[i];
                boolean save=true;
                String fName=f.getName();
                String docName=fName;
                FieldMeta meta = f.getAnnotation(FieldMeta.class);
                if(meta!=null){
                    save= meta.save();
                    if (StringUtils.isNotEmpty(meta.name())){
                        docName=meta.name();
                    }
                }
                if(save){
                    String methodName = "get"+fName.substring(0, 1).toUpperCase() + fName.substring(1);
                    String type = f.getGenericType().toString(); // 获取属性的类型
                    if (type.equals("boolean")) {
                        if(fName.indexOf("is")==0){
                            methodName=fName;
                        }else{
                            methodName="is"+fName.substring(0, 1).toUpperCase() + fName.substring(1);
                        }
                    }
                    Object value=methodAccess.invoke(t, methodName, null);
                    document.put(docName, value);
                }
            }
        //}
        return document;
    }
    /**
     * Document 转成  Object
     * @param doc
     * @param cls
     * @return
     */
    public static <T> T toObject(Document doc,Class<T> cls){
        String json=doc.toJson();

        Map<String, Object> jsonMap=new HashMap<>();
        JSONObject jsonObj = JSON.parseObject(json);
        for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
            String key=entry.getKey();
            Object value=entry.getValue();
            if(String.valueOf(value).contains("{\"$numberLong\"")||String.valueOf(value).contains("\"$oid\"")){
                JSONObject vNode=	 JSON.parseObject(String.valueOf(value));
                for (Map.Entry<String, Object> nodeEntry : vNode.entrySet()) {
                    value=nodeEntry.getValue();
                    break;
                }
            }
            jsonMap.put(key, value);
        }

        json= JsonEncoder.DEFAULT.encode(jsonMap);

        T t=JsonEncoder.DEFAULT.decode(json, cls);
        return t;
    }
}
