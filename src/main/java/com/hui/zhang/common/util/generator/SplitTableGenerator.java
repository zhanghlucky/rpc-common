package com.hui.zhang.common.util.generator;

import com.hui.zhang.common.util.generator.dto.SplitTableBean;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by zhanghui on 2018/7/10.
 * 分表sql文件生成类
 */
public class SplitTableGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SplitTableGenerator.class);

    public static Map<String,SplitTableBean> splitConfigMap=null;

    /**
     *
     * @param sqlFilePath
     */
    public static void generatorSqlFile(String sqlFilePath){
        initSplitConfigXml();
        //String filePath="D:\\3项目文档\\3.开发&维护平台项目\\dmp-upms.sql";
        String splitFilePath=sqlFilePath.replace(".sql","_split.sql");
        StringBuilder content = new StringBuilder("");
        try {
            String encoding = "UTF-8";
            File file = new File(sqlFilePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                List<String> tableSqlList=new ArrayList();
                List<String> alertSqlList=new ArrayList();
                List<String> indexSqlList=new ArrayList();
                boolean tableFlag=false;
                String talbeSql="";
                String alertSql="";
                boolean indexFlag=false;
                String indexSql="";
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (!lineTxt.contains("/*")&&!lineTxt.contains("drop index ")){
                        {//create table 语句
                            if (lineTxt.contains("create table")){
                                talbeSql="";
                                tableFlag=true;
                            }
                            if (tableFlag){
                                talbeSql+=lineTxt+"\n";
                            }
                            if (tableFlag&&lineTxt.contains(");")){
                                if(StringUtils.isNotEmpty(talbeSql)){
                                    tableSqlList.add(talbeSql);
                                }
                                tableFlag=false;
                            }
                        }
                        {//alter table 语句
                            if (lineTxt.contains("alter table")){
                                alertSql=lineTxt+"\n";
                                alertSqlList.add(alertSql);
                            }
                        }
                        {//create index 语句
                            if (lineTxt.contains("create index")){
                                indexSql="";
                                indexFlag=true;
                            }
                            if (indexFlag){
                                indexSql+=lineTxt+"\n";
                            }
                            if (indexFlag&&lineTxt.contains(");")){
                                if(StringUtils.isNotEmpty(indexSql)){
                                    indexSqlList.add(indexSql);
                                }
                                indexFlag=false;
                            }
                        }

                    }
                }

                read.close();

                String sqls="";
                tableSqlList.addAll(alertSqlList);
                tableSqlList.addAll(indexSqlList);
                for (String tableSql: tableSqlList) {
                    //System.out.println(tableSql+"\n *********");
                    boolean flag=false;
                    SplitTableBean splitTableBean=null;
                    for (Map.Entry<String, SplitTableBean> entry : splitConfigMap.entrySet()) {
                        //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                        if (tableSql.contains(entry.getKey()+"\n")){
                            flag=true;
                            splitTableBean=entry.getValue();
                            break;
                        }
                    }
                    if (flag&&splitTableBean.getSplitType().equals("MODULO")){
                        String tSqls=tableSql;
                        int size=splitTableBean.getTotalSize();
                        for(int i=0;i<size;i++){
                            String sql= tableSql.replace(splitTableBean.getTableName(),splitTableBean.getTableName()+"_"+i);
                            tSqls+=sql;
                        }
                        tableSql=tSqls;
                    }
                    System.out.println(tableSql);
                    sqls+=tableSql;
                }

                //写文件
                try{
                    FileWriter writer=new FileWriter(splitFilePath,false);
                    writer.write(sqls);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                logger.error("找不到指定的文件");
            }
        } catch (Exception e) {
           logger.error("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static void   initSplitConfigXml(){
        if (null==splitConfigMap){
            splitConfigMap=new HashMap();
            //String path=  System.getProperty("user.dir")+"/src/main/resources/"+defaultSplitTableConfigName;
            String path=  Class.class.getClass().getResource("/").getPath()+"/generator/splitTableConfig.xml";
            logger.info(path);
            try {
                File f = new File(path);
                if (f.exists()){
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(f);
                    org.dom4j.Element root = doc.getRootElement();
                    String splitType=root.attributeValue("type");
                    for(Iterator it = root.elementIterator(); it.hasNext();){
                        org.dom4j.Element element = (org.dom4j.Element) it.next();
                        String name=element.attributeValue("name");
                        String size=element.attributeValue("size");
                        int _size=0;
                        if (StringUtils.isNotEmpty(size)){
                            _size=Integer.valueOf(size);
                        }

                        String field=element.attributeValue("field");
                        SplitTableBean splitTableBean=new SplitTableBean();
                        splitTableBean.setTableName(name);
                        //field= MybatisUtil.underline2Camel(field,true);
                        splitTableBean.setKeyFieldName(field);
                        splitTableBean.setSplitType(splitType);
                        splitTableBean.setTotalSize(_size);

                        //System.out.println(name+" "+mod);
                        splitConfigMap.put(name,splitTableBean);
                    }
                }
            } catch (Exception e) {
                logger.error("读取{}异常，异常描述：{}","/generator/splitTableConfig.xml",e.getMessage());
            }
        }
    }

}
