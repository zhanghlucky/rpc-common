package com.hui.zhang.common.datasource.es.mapper;

import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Element;

import java.util.Map;

/**
 * Created by zhanghui on 2019-04-18.
 */
public class EsMappperBean {

    private  static  final Map<String ,Element>  QUERY_MAP=new HashedMap();

    public Element getQueryElement(String keyId){
        return  QUERY_MAP.get(keyId);
    }
    public Element putQueryElement(String keyId,Element element){
        return  QUERY_MAP.put(keyId,element);
    }

}
