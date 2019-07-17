package com.hui.zhang.common.datasource.es.mapper;

import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.spring.SpringBeanUtil;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.MD5Util;
import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhanghui on 2019-04-18.
 * xml query 解析器
 */
public class EsMapperParser {
    private static final Logger logger = LoggerFactory.getLogger(EsMapperParser.class);
    public static final EsMapperParser DEFAULT = new EsMapperParser();
    private static EsMappperBean esMappperBean;
    static {
        try {
            esMappperBean= SpringBeanUtil.getBean("esMappperBean",EsMappperBean.class);
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    public EsMapperParser() {

    }

    /**
     * 获取的请求json串
     * @param keyId
     * @param params
     * @return
     */
    public String getQuery(String keyId, Map<String,Object> params){
        Element element=esMappperBean.getQueryElement(keyId);
        if (null!=element){
            String query=this.buildQury(element,params);
            return query;
        }else{
            throw  new RuntimeException("no query mapper find keyId:"+keyId+"");
        }
    }

    /**
     * 创建请求串
     * @param element
     * @param params
     * @return
     */
    public   String buildQury(Element element, Map<String,Object> params){
        //1.拼接请求串
        List<Element> childElmList= element.elements();//获得子请求对象
        Map<String ,String > result=new HashedMap();//结果存储对象
        String parentFullQuery=element.getStringValue();
        this.initQuery(childElmList,parentFullQuery,params,result);//递归构建结果
        String resultQuery=result.get("result");
        //2.值替换
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String mkey=entry.getKey();
            Object mvalue=entry.getValue();

            String replacement=null;
            if (mvalue instanceof  String){
                replacement="\""+mvalue.toString()+"\"";
            }else if(mvalue instanceof  Integer){
                replacement=mvalue.toString();
            }else if(mvalue instanceof  Double){
                replacement=mvalue.toString();
            }else if(mvalue instanceof  Long){
                replacement=mvalue.toString();
            }else if (mvalue instanceof  List){
                replacement="";
                List<Object> ls=(List)mvalue;
                for (Object obj:ls) {
                    if (obj instanceof String){
                        replacement=replacement+"\""+obj.toString()+ "\",";
                    }else{
                        replacement=replacement+""+obj.toString()+ ",";
                    }
                }
            }else if (mvalue ==null){
                replacement="null";
            }else{
                replacement=mvalue.toString();
            }
            resultQuery =  Pattern.compile(Pattern.quote("#{"+mkey+"}")).matcher(resultQuery).replaceAll(replacement);
        }

        //json格式化
        Matcher formatMatcher = Pattern.compile(Pattern.quote(",}")).matcher(resultQuery);
        resultQuery=formatMatcher.replaceAll("}");
        formatMatcher = Pattern.compile(Pattern.quote(",]")).matcher(resultQuery);
        resultQuery=formatMatcher.replaceAll("]");
        //格式美化
        JSONObject jsonObject= JSONObject.parseObject(resultQuery);
        String queryJson= JsonEncoder.DEFAULT.encode(jsonObject);
        return queryJson;
    }

    /**
     * 递归创建query串
     * @param elementList 子条件dom对象
     * @param parentFullQuery 父请求串
     * @param params 参数
     * @param result 结果对象
     */
    private  void initQuery(List<Element> elementList,String parentFullQuery,Map<String,Object> params,Map<String ,String > result){
        //格式化
        parentFullQuery = formatStr(parentFullQuery);
        String parentFullQueryRpl=result.get(MD5Util.MD5(parentFullQuery));//父级请求串
        if (null==parentFullQueryRpl){
            parentFullQueryRpl=parentFullQuery;
        }
        for (Element conditionElm:elementList) {
            //条件公式
            String test=formatSpaceStr(conditionElm.attributeValue("test"));
            /**
             * 条件计算
             * 不支持 and 与 or 链接的查询，后续补上
             */
            int okIndex=0;
            /**
             * 针对and 与普通查询
             */
            String[] andTestArray=test.split("and");
            for (String andTest:andTestArray) {
                String column=null;
                String condition=null;
                String value=null;
                if (andTest.contains("!=")){
                    condition="NOT_EQUAIL";
                    column=andTest.split(Pattern.quote("!="))[0].trim();
                    value=andTest.split(Pattern.quote("!="))[1].trim();

                }else if (andTest.contains("==")){
                    condition="EQUAIL";
                    column=andTest.split(Pattern.quote("=="))[0].trim();
                    value=andTest.split(Pattern.quote("=="))[1].trim();
                }

                value=formatValue(value);

                Object p_value=params.get(column);
                if (condition.equals("NOT_EQUAIL")){
                    if (null!=p_value){
                        if (!p_value.toString().equals(value)){
                            okIndex++;
                        }
                    }else if(value!=null){
                    }
                }else if (condition.equals("EQUAIL")){
                    if (null!=p_value){
                        if (p_value.toString().equals(value)){
                            okIndex++;
                        }
                    }else if(null==value){
                        okIndex++;
                    }
                }
            }

            /**
             * 针对or查询
             */
            String[] orTestArray=test.split(Pattern.quote("||"));
            if (orTestArray.length>1) {//有或者的时候
                for (String orTest : orTestArray) {
                    String column=null;
                    String condition=null;
                    String value=null;
                    if (orTest.contains("!=")){
                        condition="NOT_EQUAIL";
                        column=orTest.split(Pattern.quote("!="))[0].trim();
                        value=orTest.split(Pattern.quote("!="))[1].trim();
                    }else if (orTest.contains("==")){
                        condition="EQUAIL";
                        column=orTest.split(Pattern.quote("=="))[0].trim();
                        value=orTest.split(Pattern.quote("=="))[1].trim();
                    }
                    value=formatValue(value);
                    Object p_value=params.get(column);
                    if (condition.equals("NOT_EQUAIL")){
                        if (null!=p_value){
                            if (!p_value.toString().equals(value)){
                                okIndex++;
                            }
                        }else if(value!=null){
                        }
                    }else if (condition.equals("EQUAIL")){
                        if (null!=p_value){
                            if (p_value.toString().equals(value)){
                                okIndex++;
                            }
                        }else if(null==value){
                            okIndex++;
                        }
                    }
                }
            }

            String conditionFullQuery=formatStr(conditionElm.getStringValue());//子请求串
            String conditionFullQueryReplacement="";//待替换成的串

            //当普通或者and查询都满足或者 or查询有一个满足的时候子查询才能生效
            if((andTestArray.length>0&&andTestArray.length==okIndex)||
                    orTestArray.length>1&&okIndex>0){

                List<Element> childElmList= conditionElm.elements();
                if (childElmList.size()>0){
                    this.initQuery(childElmList,conditionFullQuery,params,result);//递归调用
                }

                String brotherFullQuery=result.get(MD5Util.MD5(conditionFullQuery));//获得已经处理好的兄弟串
                if (null==brotherFullQuery){
                    brotherFullQuery=conditionFullQuery;
                }
                conditionFullQueryReplacement=brotherFullQuery;//待替换的串
                if (conditionFullQuery.lastIndexOf(",")!=(conditionFullQuery.length()-1)){
                    conditionFullQueryReplacement+=",";
                }//补齐格式
            }
            parentFullQueryRpl = Pattern.compile(Pattern.quote(conditionFullQuery)).matcher(parentFullQueryRpl).replaceAll(conditionFullQueryReplacement);
        }
        result.put("result",parentFullQueryRpl);//记录结果
        result.put(MD5Util.MD5(parentFullQuery),parentFullQueryRpl);//替换后重设父请求串
    }

    //不保留空格
    private  String formatStr(String str){
        Pattern mainPattern=Pattern.compile("\\s*|\t|\r|\n");
        return mainPattern.matcher(str).replaceAll("");
    }

    //保留空格
    private  String formatSpaceStr(String str){
        Pattern mainPattern=Pattern.compile("\t|\r|\n");
        String nstr= mainPattern.matcher(str).replaceAll("");
        nstr=nstr.replaceAll(" {2,}", " ") ;
        return nstr;
    }
    //格式化值
    private  String  formatValue(String value){
        if (value.toString().equals("null")){
            value=null;
        }else if (value.toString().equals("''")){
            value="";
        }else if (value.startsWith("'")){
            value=value.substring(1,value.length()-1);//去除 ''
        }
        return value;
    }
}
