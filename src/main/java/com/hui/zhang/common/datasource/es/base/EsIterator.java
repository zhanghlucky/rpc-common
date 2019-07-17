package com.hui.zhang.common.datasource.es.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.datasource.es.ds.EsClient;
import com.hui.zhang.common.util.JsonEncoder;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhanghui on 2019-04-24.
 */
public class EsIterator<T> implements Iterator<T> {
    private  static final Logger logger = LoggerFactory.getLogger(EsIterator.class);

    private String scroll;
    private String scrollId;
    private long totalSize;
    private long totalIndex;
    private int from=0;
    private List<T> rows=new ArrayList<T>();
    private RestClient restClient;
    private T t;
    private Class<T> cls;
    private EsClient esClient;

    public EsIterator(String scroll,String scrollId,EsClient esClient,long totalSize,List<T> rows,Class<T> cls){
        this.scroll=scroll;
        this.scrollId=scrollId;
        //this.esDsCode=esDsCode;
        this.totalSize=totalSize;
        this.rows=rows;
        this.totalIndex=0;
        this.from=0;
        this.cls=cls;
        this.esClient=esClient;
    }

    @Override
    public boolean hasNext() {
        if (rows.size()>0&&from<rows.size()){//还有未取完的值
            return true;
        }else if(rows.size()>0&&from==rows.size()){
            rows=esClient.selectScroll(scroll,scrollId,cls);//重新取值
            if (rows.size()>0){
                from=0;
                return true;
            }
        }else{//rows 为0
            return false;
        }
        return false;
    }

    @Override
    public T next() {
        totalIndex++;
        //logger.info("totalIndex:{},totalSize:{}",totalIndex,totalSize);
        T t= rows.get(from);
        from++;
        return t;
    }
}
