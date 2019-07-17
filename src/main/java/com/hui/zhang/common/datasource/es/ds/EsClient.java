package com.hui.zhang.common.datasource.es.ds;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.datasource.es.annotation.IndexId;
import com.hui.zhang.common.datasource.es.annotation.IndexMapper;
import com.hui.zhang.common.datasource.es.annotation.IndexType;
import com.hui.zhang.common.datasource.es.base.EsIterator;
import com.hui.zhang.common.datasource.es.mapper.EsMapperParser;
import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.MD5Util;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.GlobalUtil;
import com.hui.zhang.common.util.etc.po.CfgElasticsearchPO;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by zhanghui on 2019-04-17.
 */
public class EsClient {
    private  final static  Logger logger = LoggerFactory.getLogger(EsClient.class);
    private  final static Map<String ,RestClient> CLIENT_MAP=new HashMap();
    private  static long  MAX_RESULT_WINDOW=0L;
    private  RestClient restClient;
    //private  CfgElasticsearchPO cfgElasticsearchPO;
    private String index;
    private String esDsCode;
    private String envId;


    public EsClient(String esDsCode){
        CfgElasticsearchPO cfgElasticsearchPO=AppConfigUtil.getCfgElasticsearchPO(esDsCode);
        RestClient client=CLIENT_MAP.get(MD5Util.MD5(cfgElasticsearchPO.getDsHost()));
        index=cfgElasticsearchPO.getIndexName();
        if (null!=client){
            //this.restClient=client;
        }else {
            client = buildRestClient(esDsCode, AppConfigUtil.getCfgEnvironmentPO().getEnvId());
        }
        this.restClient=client;
        this.esDsCode=esDsCode;

    }

    public EsClient(String esDsCode,String envId){
        CfgElasticsearchPO cfgElasticsearchPO=AppConfigUtil.getCfgElasticsearchPO(esDsCode,envId);
        if (null==cfgElasticsearchPO){
            throw new RuntimeException("环境"+envId+"无"+esDsCode+"的配置");
        }
        RestClient client=CLIENT_MAP.get(MD5Util.MD5(cfgElasticsearchPO.getDsHost()));
        index=cfgElasticsearchPO.getIndexName();
        if (null!=client){
            //this.restClient=client;
        }else {
            client = buildRestClient(esDsCode,envId);
        }
        this.restClient=client;
        this.esDsCode=esDsCode;
        this.envId=envId;


    }
    private RestClient buildRestClient(String esDsCode,String envId){
        RestClient client=null;
        CfgElasticsearchPO cfgElasticsearchPO= AppConfigUtil.getCfgElasticsearchPO(esDsCode,envId);
        if (null!=cfgElasticsearchPO){
            String esHost = cfgElasticsearchPO.getDsHost();
            int esPort=cfgElasticsearchPO.getDsPort();
            String esUserName=cfgElasticsearchPO.getUsername();
            String esPassword=cfgElasticsearchPO.getPassword();
            //有账号密码
            if(StringUtils.isNotEmpty(esUserName)&&StringUtils.isNotEmpty(esPassword)){
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUserName, esPassword));
                client = RestClient.builder(new HttpHost(esHost,esPort,"http"))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }
                        }).build();
            }else{
                client = RestClient.builder(new HttpHost(esHost, esPort, "http")).build();
            }
            CLIENT_MAP.put(MD5Util.MD5(cfgElasticsearchPO.getDsHost()),client);
        }else {
            logger.error("配置管理中心无cfgElasticsearchPO 配置");
        }
        return client;
    }

    /**
     * 查询获得es的结果集
     * @param query
     * @param cls
     * @param <M>
     * @return
     */
    public <M> String selectResponse(String query,Class<M> cls){
       logger.debug(query);

        String type = getType(cls);
        HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
        try {
            Response response = restClient.performRequest("GET", "/" + index + "/" + type + "/_search", Collections.<String, String>emptyMap(), entity);
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        }catch (Exception e){
            logger.error("query es error:{}",e.getMessage());
        }
        return null;
    }

    /**
     * 查询结果
     * @param queryId 请求ID
     * @param params 参数
     * @param cls 对象class
     * @param <M> 对象类型
     * @return
     */
    public <M> String selectResponse(String queryId,Map<String,Object> params,Class<M> cls){
        String query=buildQuery(queryId,params,cls);
       logger.debug(query);

        String result= selectResponse( query,cls);
        return result;
    }


    /**
     * 查询列表
     * @param query
     * @param cls
     * @param <M>
     * @return
     */
    public<M> List<M> selectList(String query,Class<M> cls){

        String responseBody=selectResponse(query,cls);
        if (null!=responseBody){
            JSONObject responseObject = JSON.parseObject(responseBody);
            JSONObject hitsObject = JSON.parseObject(responseObject.get("hits").toString());//外层hits
            int totalSize=hitsObject.getInteger("total");
            if (totalSize>0){
                JSONArray hitsArray=JSON.parseArray(hitsObject.get("hits").toString());//内部hits
                if (hitsArray.size()>0){
                    List<M> rows=new ArrayList<>();
                    for (int i = 0; i < hitsArray.size(); i++) {
                        JSONObject row = hitsArray.getJSONObject(i);
                        String _source = row.get("_source").toString();
                        String _id=row.get("_id").toString();
                        _source=mapperJson(_id,_source,cls,true);
                        M m= JsonEncoder.DEFAULT.decode(_source,cls);
                        rows.add(m);
                    }
                    return rows;
                }
            }
        }

        return new ArrayList<M>();
    }

    /**
     * 通过查询ID查询列表
     * @param queryId
     * @param params
     * @param cls
     * @param <M>
     * @return
     */
    public<M> List<M> selectList(String queryId,Map<String,Object> params,Class<M> cls){
        String query=buildQuery(queryId,params,cls);

        return selectList(query,cls);
    }

    /**
     * 获得第一个数据
     * @param query
     * @param cls
     * @param <M>
     * @return
     */
    public<M> M selectFirst(String query,Class<M> cls,boolean needPager){

        if (needPager){
            Pager pager=new Pager(1,1);
            query=queryAddPager(query,pager);
        }
        List<M> list=selectList(query,cls);
        if (null!=list){
            return list.get(0);
        }
        return null;
    }

    /**
     * 通过查询ID获得第一个结果
     * @param queryId
     * @param params
     * @param cls
     * @param <M>
     * @return
     */
    public<M> M selectFirst(String queryId,Map<String,Object> params,Class<M> cls){
        String query=buildQuery(queryId,params,cls);

        return selectFirst(query,cls,true);
    }

    /**
     * 通过主键查询对象
     * @param key
     * @param cls
     * @param <M>
     * @return
     */
    public <M> M selectByPrimaryKey(String key,Class<M> cls){
        JSONObject queryJSON=new JSONObject();
        JSONObject constant_scoreJSON=new JSONObject();
        JSONObject filterJSON=new JSONObject();
        JSONObject termJSON=new JSONObject();
        JSONObject keyJSON=new JSONObject();
        keyJSON.put("_id",key);

        termJSON.put("term",keyJSON);
        filterJSON.put("filter",termJSON);
        constant_scoreJSON.put("constant_score",filterJSON);
        queryJSON.put("query",constant_scoreJSON);
        String query=queryJSON.toJSONString();

        M m=this.selectFirst(query,cls,false);
        return m;
    }

    /**
     * 查询分页
     * @param query
     * @param pager
     * @param cls
     * @param <M>
     * @return
     */
    public <M> DataPager<M> selectDataPager (String query,Pager pager,Class<M> cls){
        query=queryAddPager(query,pager);
        pager=resetPager(pager);

        String responseBody=selectResponse(query,cls);
        if (null!=responseBody){
            JSONObject responseObject = JSON.parseObject(responseBody);
            JSONObject hitsObject = JSON.parseObject(responseObject.get("hits").toString());//外层hits
            int totalSize=hitsObject.getInteger("total");
            if (totalSize>0){
                JSONArray hitsArray=JSON.parseArray(hitsObject.get("hits").toString());//内部hits
                if (hitsArray.size()>0){
                    List<M> rows=new ArrayList<>();
                    for (int i = 0; i < hitsArray.size(); i++) {
                        JSONObject row = hitsArray.getJSONObject(i);
                        String _source = row.get("_source").toString();
                        String _id = row.get("_id").toString();
                        _source=mapperJson(_id,_source,cls,true);
                        M m= JsonEncoder.DEFAULT.decode(_source,cls);
                        rows.add(m);
                    }
                    return new DataPager<M>(rows,totalSize,pager);
                }
            }
        }
        return new DataPager<M>(new ArrayList<M>(),0,pager);
    }

    /**
     * 通过查询ID获得分页对象
     * @param queryId
     * @param params
     * @param pager
     * @param cls
     * @param <M>
     * @return
     */
    public <M> DataPager<M> selectDataPager (String queryId,Map<String,Object> params,Pager pager,Class<M> cls){
        String query=buildQuery(queryId,params,cls);
        return selectDataPager(query,pager,cls);
    }

    /**
     * 查询迭代对象
     * @param queryId
     * @param params
     * @param size
     * @param scroll
     * @param cls
     * @param <M>
     * @return
     */
    public<M> EsIterator<M> selectEsIterator(String queryId,Map<String,Object> params,int size,String scroll,Class<M> cls){
        if (size==0){
            size=10;
        }
        if(StringUtils.isEmpty(scroll)){
            scroll="1m";
        }
        String query=buildQuery(queryId,params,cls);

        JSONObject queryJson = JSON.parseObject(query);
        queryJson.fluentPut("size",size);
        query=queryJson.toJSONString();
       logger.debug(query);


        String type = getType(cls);
        HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
        try {
            Response response = restClient.performRequest("GET", "/" + index + "/" + type + "/_search?scroll="+scroll+"", Collections.<String, String>emptyMap(), entity);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (null!=responseBody){
                JSONObject responseObject = JSON.parseObject(responseBody);
                String scrollId=responseObject.get("_scroll_id").toString();
                JSONObject hitsObject = JSON.parseObject(responseObject.get("hits").toString());//外层hits
                long totalSize=hitsObject.getLong("total");
                List<M> rows=new ArrayList<>();
                if (totalSize>0){
                    JSONArray hitsArray=JSON.parseArray(hitsObject.get("hits").toString());//内部hits
                    if (hitsArray.size()>0){
                        for (int i = 0; i < hitsArray.size(); i++) {
                            JSONObject row = hitsArray.getJSONObject(i);
                            String _source = row.get("_source").toString();
                            String _id = row.get("_id").toString();
                            _source=mapperJson(_id,_source,cls,true);

                            M m= JsonEncoder.DEFAULT.decode(_source,cls);
                            rows.add(m);
                        }
                    }
                }
                EsIterator<M> esIterator=new EsIterator<M>(scroll,scrollId,this,totalSize,rows,cls);
                return esIterator;
            }
        }catch (Exception e){
            logger.error("query es error:{}",e.getMessage());
        }
        return null;
    }

    /**
     * 滚动迭代查询
     * @param scroll
     * @param scrollId
     * @param cls
     * @param <M>
     * @return
     */
    public<M> List<M> selectScroll(String scroll,String scrollId,Class<M> cls){
        JSONObject object=new JSONObject();
        object.put("scroll",scroll);
        object.put("scroll_id",scrollId);
        String query=object.toJSONString();
       logger.debug(query);
        HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
        try {
            Response response = restClient.performRequest("GET", "/_search/scroll", Collections.<String, String>emptyMap(), entity);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (null!=responseBody){
                JSONObject responseObject = JSON.parseObject(responseBody);
                scrollId=responseObject.get("_scroll_id").toString();
                JSONObject hitsObject = JSON.parseObject(responseObject.get("hits").toString());//外层hits
                long totalSize=hitsObject.getLong("total");
                List<M> rows=new ArrayList<>();
                if (totalSize>0){
                    JSONArray hitsArray=JSON.parseArray(hitsObject.get("hits").toString());//内部hits
                    if (hitsArray.size()>0){
                        for (int i = 0; i < hitsArray.size(); i++) {
                            JSONObject row = hitsArray.getJSONObject(i);
                            String _source = row.get("_source").toString();
                            String _id = row.get("_id").toString();
                            _source=mapperJson(_id,_source,cls,true);

                            M m= JsonEncoder.DEFAULT.decode(_source,cls);
                            rows.add(m);
                        }
                    }
                }
               return rows;
            }
        }catch (Exception e){
            logger.error("query es error:{}",e.getMessage());
        }
        return null;
    }


    /**
     * 获取总数
     * @param query
     * @param cls
     * @param <M>
     * @return
     */
    public <M> Long count(String query,Class<M> cls){
        Pager pager=new Pager(1,1);
        query=queryAddPager(query,pager);
        String responseBody=selectResponse(query,cls);
        if (null!=responseBody){
            JSONObject responseObject = JSON.parseObject(responseBody);
            JSONObject hitsObject = JSON.parseObject(responseObject.get("hits").toString());//外层hits
            long totalSize=hitsObject.getInteger("total");
            return totalSize;
        }
        return Long.valueOf(0);
    }

    /**
     * 获得总数
     * @param queryId
     * @param params
     * @param cls
     * @param <M>
     * @return
     */

    public <M> Long count(String queryId,Map<String,Object> params,Class<M> cls){
        String query=buildQuery(queryId,params,cls);
        return count(query,cls);
    }

    /**
     * 插入数据
     * @param m
     * @param <M>
     */
    public <M> void insert(M m){
        String type = getType(m.getClass());

        //反射取得ID注解的值
        String id=getId(m).toString();
        String dataJson=JsonEncoder.DEFAULT.encode(m);
        dataJson=mapperJson(null,dataJson,m.getClass(),false);
        String method = "PUT";
        String endpoint = "/"+index+"/"+type+"";
        if (null!=id){
            endpoint = "/"+index+"/"+type+"/"+id;
        }
        try {
            HttpEntity insertEntity = new NStringEntity(dataJson, ContentType.APPLICATION_JSON);
            Response insertResponse = restClient.performRequest(method,endpoint, Collections.<String, String>emptyMap(),insertEntity);
        }catch (Exception e){
            logger.error("query es error:{}",e.getMessage());
        }
    }

    /**
     * 删除数据
     * @param query
     * @param cls
     * @param <M>
     * @return
     */
    public <M> long deleteByQuery(String query,Class<M> cls){
        String type = getType(cls);
       logger.debug(query);

        String method = "DELETE";
        //String endpoint = "/"+index+"/"+type+"/"+id;  //id的方法待实现  注解反射去做
        String endpoint = "/"+index+"/"+type+"/_delete_by_query";
        try {
            HttpEntity queryEntity = new NStringEntity(query, ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest(method,endpoint, Collections.<String, String>emptyMap(),queryEntity);
        }catch (Exception e){
            logger.error("delete es error:{}",e.getMessage());
        }
        return 1;
    }

    public <M> long deleteByQuery(String queryId,Map<String,Object> params,Class<M> cls){
        String query=buildQuery(queryId,params,cls);
        return deleteByQuery(query,cls);
    }

    /**
     * 通过主键删除
     * @param key
     * @param cls
     * @param <M>
     * @return
     */
    public <M> long deleteByPrimaryKey (String key,Class<M> cls){
        String type = getType(cls);
        String method = "DELETE";
        String endpoint = "/"+index+"/"+type+"/"+key;
        try {
            Response response = restClient.performRequest(method,endpoint);
        }catch (Exception e){
            logger.error("delete es error:{}",e.getMessage());
        }
        return 1;
    }

    /**
     * 通过主键更新
     * @param m
     * @param <M>
     * @return
     */
    public <M> long updateByPrimaryKey (M m){
        String type = getType(m.getClass());

        //反射取得ID注解的值
        String id=getId(m).toString();
        String method = "POST";
        String endpoint = "/"+index+"/"+type+"/"+id+"/_update";
        try {
            //String dataJson=JsonEncoder.DEFAULT.encode(m);
            JSONObject updateObject=new JSONObject();
            updateObject.put("doc",m);
            String json=updateObject.toJSONString();
            json=mapperJson(null,json,m.getClass(),false);

            // //logger.debug(json);

            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest(method,endpoint,Collections.emptyMap(),entity);
        }catch (Exception e){
            logger.error("update es error:{}",e.getMessage());
        }
        return 1;
    }

    /**
     * 通过主键请求更新
     * @param key
     * @param query
     * @param cls
     * @param <M>
     * @return
     */
    public <M> long updateByPrimaryKeyQuery(String key,String query,Class<M> cls){
        String type = getType(cls);

        //反射取得ID注解的值
        //String id=getId(m).toString();
        String method = "POST";
        String endpoint = "/"+index+"/"+type+"/"+key+"/_update";
        try {
            //String dataJson=JsonEncoder.DEFAULT.encode(m);
            /*JSONObject updateObject=new JSONObject();
            updateObject.put("doc",m);
            String json=updateObject.toJSONString();*/

            HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest(method,endpoint,Collections.emptyMap(),entity);
        }catch (Exception e){
            logger.error("update es error:{}",e.getMessage());
        }
        return 1;
    }

    private long getEsMaxResultWindow(){
        if (0==MAX_RESULT_WINDOW){
            String method = "GET";
            String endpoint = "/"+index+"/_settings";
            try {
                HttpEntity entity = new NStringEntity("", ContentType.DEFAULT_TEXT);
                Response response = restClient.performRequest(method,endpoint,Collections.emptyMap(),entity);
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject responseObject = JSON.parseObject(responseBody);
                JSONObject indexObject = JSON.parseObject(responseObject.get(index).toString());//外层hits
                JSONObject settingsObject=JSON.parseObject(indexObject.get("settings").toString());
                JSONObject dataObj=JSON.parseObject(settingsObject.get("index").toString());
                MAX_RESULT_WINDOW= Long.valueOf(dataObj.get("max_result_window").toString());
            }catch (Exception e){
                MAX_RESULT_WINDOW=10000L;
                logger.error("getEsMaxResultWindow error:{}",e.getMessage());
                //e.printStackTrace();
            }
        }
        return MAX_RESULT_WINDOW;
    }

    /**
     * 获得 restClient 实例
     * @return
     */
    public RestClient instance(){
        return restClient;
    }


    /**
     * 将请求参数追加分页参数
     * @param query
     * @param pager
     * @return
     */
    private String queryAddPager(String query,Pager pager){
        pager=resetPager(pager);
        //long max=getEsMaxResultWindow();

        int from=(pager.getPage()-1)*pager.getSize();
        /*if (from>max){
            from=(int)max-pager.getSize();
            logger.error("最大页超过限制，最大索引值 max_result_window：{}",max);
        }*/
        //System.out.println(query);
        JSONObject queryJson = JSON.parseObject(query);

        queryJson.fluentPut("from",from);
        queryJson.fluentPut("size",pager.getSize());

        query=queryJson.toJSONString();
        return query;
    }

    private Pager resetPager(Pager pager){
        long max=getEsMaxResultWindow();
        if (pager.getPage()*pager.getSize()>max){
            long maxPage=0;
            if (pager.getSize()==0){
                maxPage=0;
            }else{
                if (max%pager.getSize()!=0){
                    maxPage=max/pager.getSize()+1;

                }else{
                    maxPage=max/pager.getSize();
                }
            }
            pager.setPage((int)maxPage);
        }
        return pager;
    }

    /**
     * 拼接query
     * @param queryId
     * @param params
     * @param cls
     * @param <T>
     * @return
     */
    private <T> String buildQuery(String queryId,Map<String,Object> params,Class<T> cls){
        String type = getType(cls);
        String keyId="es."+type+"."+queryId;
        String query=EsMapperParser.DEFAULT.getQuery(keyId,params);
        return query;
    }

    /**
     * 获取TYPE
     * @param cls
     * @param <T>
     * @return
     */
    private <T> String getType(Class<T> cls){
        IndexType typeName=cls.getAnnotation(IndexType.class);
        if (null==typeName){
            logger.error("未配置对象的 @TypeName注解");
            return null;
        }
        String type = cls.getAnnotation(IndexType.class).value();
        return type;
    }

    /**
     * 获取ID
     * @param m
     * @param <M>
     * @return
     */
    private <M> Object getId(M m){
        Object id=null;
        MethodAccess methodAccess=MethodAccess.get(m.getClass());
        Field[] fs = m.getClass().getDeclaredFields();
        for(int i = 0 ; i < fs.length; i++){
            Field f = fs[i];
            String fName=f.getName();
            boolean indexIdFlag=false;
            for (Annotation anno : f.getDeclaredAnnotations()) {//获得所有的注解
                if(anno.annotationType().equals(IndexId.class) ) {//找到自己的注解
                    indexIdFlag=true;
                    break;
                }
            }
            if(indexIdFlag){
                String methodName = "get"+fName.substring(0, 1).toUpperCase() + fName.substring(1);
                Object idObj=methodAccess.invoke(m, methodName, null);
                id=idObj;
                break;
            }
        }
        return id;
    }


    /**
     * class 与 json映射
     * @param sourceJson
     * @param cls
     * @param toObjectFlag true 将json映射成 cls可以识别的 json。
     *                false 将json映射成 es可以存储的json。
     * @param <M>
     * @return
     */
    private <M> String mapperJson(String _id,String sourceJson,Class<M> cls,boolean toObjectFlag){
        if (toObjectFlag){
            JSONObject jsonObject=JSONObject.parseObject(sourceJson);
            jsonObject.put("_id",_id);
            sourceJson=jsonObject.toJSONString();
        }else{
            JSONObject jsonObject=JSONObject.parseObject(sourceJson);
            jsonObject.remove("_id");
            sourceJson=jsonObject.toJSONString();
        }

        Map<String,String> mapperMap=new HashedMap();
        Field[] fs = cls.getDeclaredFields();
        for(int i = 0 ; i < fs.length; i++){
            Field f = fs[i];
            String fName=f.getName();
            for (Annotation anno : f.getDeclaredAnnotations()) {//获得所有的注解
                if(anno.annotationType().equals(IndexMapper.class) ) {//找到自己的注解
                    String mapperClm=((IndexMapper)anno).value();
                    mapperMap.put(fName,mapperClm);
                    break;
                }
            }
        }

        for (Map.Entry<String, String> entry : mapperMap.entrySet()) {
            if (toObjectFlag) {
                sourceJson= sourceJson.replaceAll("\"" + entry.getValue() + "\":", "\"" + entry.getKey() + "\":");
            }else{
                sourceJson= sourceJson.replaceAll("\"" + entry.getKey() + "\":", "\"" + entry.getValue() + "\":");
            }
        }
        return sourceJson;
    }
}
