package com.hui.zhang.common.datasource.mybatis.db.ds;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MybatisUtil {

    /**
     * 获得标准命名空间
     * @param cls model or example
     * @return
     */
    public static String getNamespace(Class cls){
        String name=cls.getName();
        String head=name.split(".entity.")[0];
        String className=cls.getSimpleName();
        if(className.length()>=3&&className.subSequence(className.length()-3, className.length()).equals("Key")){//xxxPOKey
            className=className.substring(0, className.length()-3);
        }
        if(className.length()>=7&&className.subSequence(className.length()-7, className.length()).equals("Example")){//xxxPOExample
            className=className.substring(0, className.length()-7);
        }
        if(className.length()>=9&&className.subSequence(className.length()-9, className.length()).equals("WithBLOBs")){//xxxWithBLOBs
            className=className.substring(0, className.length()-9);
        }
        //String namespace=head+".dao.dbxml"+className+"Mapper.";
        String namespace="mapper.db."+className+"Mapper.";
        return namespace;
    }

    /**
     * 获得表名
     * @param cls
     * @return
     */
    public  static  String getTableName(Class cls){
        String className=cls.getSimpleName();
        return  getTableName(className);
    }

    /**
     * 获得表名
     * @param className
     * @return
     */
    public  static  String getTableName(String className){
        if(className.length()>=3&&className.subSequence(className.length()-3, className.length()).equals("Key")){//xxxPOKey
            className=className.substring(0, className.length()-3);
        }
        if(className.length()>=7&&className.subSequence(className.length()-7, className.length()).equals("Example")){//xxxPOExample
            className=className.substring(0, className.length()-7);
        }
        if(className.length()>=9&&className.subSequence(className.length()-9, className.length()).equals("WithBLOBs")){//xxxWithBLOBs
            className=className.substring(0, className.length()-9);
        }
        className=className.substring(0,className.length()-2);
        String tableName=camel2Underline(className);
        return  tableName;
    }

    /**
     * 下划线转驼峰法
     * @param line 源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line,boolean smallCamel){
        if(line==null||"".equals(line)){
            return "";
        }
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(smallCamel&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
            int index=word.lastIndexOf('_');
            if(index>0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
    /**
     * 驼峰法转下划线
     * @param line 源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line){
        if(line==null||"".equals(line)){
            return "";
        }
        line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString().toLowerCase();
    }
}
