package com.hui.zhang.common.util.generator;


import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhanghui on 2018/1/24.
 */
public  class MybatisTableGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MybatisTableGenerator.class);

    public static final char UNDERLINE='_';

    /**
     *
     * @param projectPath 项目位置  D:\v3projects\erpbiz\erp-data-service-parent\erp-data-service
     * @param classPathEntry mysqljar包位置 C:\Users\wm418\Documents\myRespository\mysql-connector-java-5.1.8.jar
     * @param dbhost 数据库域名 192.168.3.133
     * @param dbport 数据库端口 3306
     * @param dbname 数据库名称 edb_erp
     * @param dbusername 数据库用户名 root
     * @param dbpassword 数据库密码 root123
     * @param javaModelPackage javaModel包 com.edb01.erp.data.model.po
     * @param isCleanXml 是否清理xml文件
     */
    public static void  generatorMybatisFiles(String projectPath,String classPathEntry,String dbhost,String dbport,String dbname,String dbusername,String dbpassword,
                                              String javaModelPackage, boolean isCleanXml,List<String> excludeTables){
       /* logger.info("[generator]>>生成：\\resources\\generator\\generatorConfig.xml文件");*/

        String sqlMapPackage="mapper.db";

        String generatorConfigFilePath=projectPath+"\\src\\main\\resources\\generator\\generatorConfig.xml";
        String javaModelProjectPath=projectPath+"\\src\\main\\java";
        //String sqlMapPackage="mapper.db";
        String sqlMapProjectPath=projectPath+"\\src\\main\\resources";

        StringBuffer sb=new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n");
        sb.append("<!DOCTYPE generatorConfiguration PUBLIC \"-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN\" \"http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd\" > \n");
        sb.append("<generatorConfiguration> \n");
        sb.append("<classPathEntry location=\""+classPathEntry+"\"/> \n");
        sb.append("<context id=\"context1\"> \n");
        sb.append("<plugin type=\"com.hui.zhang.common.util.generator.SplitTablePlugin\"></plugin> \n");
        sb.append("<commentGenerator> \n");
        sb.append("<property name=\"suppressAllComments\" value=\"true\"/> \n");
        sb.append("</commentGenerator> \n");
        sb.append("<jdbcConnection driverClass=\"com.mysql.jdbc.Driver\" connectionURL=\"jdbc:mysql://"+dbhost+":"+dbport+"/"+dbname+"?useSSL=false\" userId=\""+dbusername+"\" password=\""+dbpassword+"\"/> \n");
        sb.append("<javaTypeResolver> \n");
        sb.append("<property name=\"forceBigDecimals\" value=\"false\"/> \n");
        sb.append("</javaTypeResolver> \n");
        sb.append("<javaModelGenerator targetPackage=\""+javaModelPackage+"\" targetProject=\""+javaModelProjectPath+"\"/> \n");
        sb.append("<sqlMapGenerator targetPackage=\""+sqlMapPackage+"\" targetProject=\""+sqlMapProjectPath+"\"/> \n");


        List<String> poNameList=new ArrayList<>();
        try{
            // 先删除所有model 文件
            String modelPath = javaModelPackage.replace(".","\\");
            modelPath = javaModelProjectPath + "\\" + modelPath;
            File dirFile=new File(modelPath);
            File[] files = dirFile.listFiles();
            if (null != files && files.length != 0){
                for(int i=0; i<files.length; i++){
                    files[i].delete();
                    /*logger.info("[generator]删除：pojo对象[{}]",files[i].getPath());*/
                }
            }


            Class.forName("com.mysql.jdbc.Driver") ;
            String url = "jdbc:mysql://"+dbhost+":"+dbport+"/"+dbname+"" ;
            Connection con = DriverManager.getConnection(url , dbusername , dbpassword ) ;
            Statement stmt = con.createStatement() ;
            ResultSet rs = stmt.executeQuery("show tables") ;
            while(rs.next()){
                String tableName=rs.getString(1);
                if (null!=excludeTables&&excludeTables.contains(tableName)) {
                    continue;
                }
                if (!isSplitTable(tableName)){
                    String poName=underlineToCamel(tableName);
                    poNameList.add(poName);
                    sb.append("<table tableName=\""+tableName+"\" domainObjectName=\""+poName+"\"/> \n");
                }
            }
        }catch(Exception se){
            //System.out.println("数据库连接失败！");
           /* logger.info("[generator]数据库连接失败，异常：{}",se);*/
            se.printStackTrace() ;
        }
        sb.append("</context> \n");
        sb.append("</generatorConfiguration> \n");

        try {
            File generatorConfigFile = new File(generatorConfigFilePath);
            PrintStream ps = new PrintStream(new FileOutputStream(generatorConfigFile));
            ps.println(sb.toString());// 往文件里写入字符串
        } catch (FileNotFoundException e) {
          /*  logger.error("[generator]生成[{}]文件异常，异常：{}",generatorConfigFilePath,e);*/
        }
        /*logger.info("[generator]<<已生成：[{}]文件",generatorConfigFilePath);*/



        try {
            File configFile=new File(generatorConfigFilePath);
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

            String xmlModelPath = sqlMapPackage.replace(".","\\");
            String xmlFilesPath=sqlMapProjectPath+"\\"+xmlModelPath+"\\";//System.getProperty("user.dir")+"/src/main/resources/mapper/db";
            if(null!=xmlFilesPath){
                File dirFile=new File(xmlFilesPath);
                File[] files = dirFile.listFiles();
                if (null!=files){
                    for(int i=0; i<files.length; i++){
                        File xmlFile=files[i];
                        boolean flag=false;
                        if (!isCleanXml){//是否清理xml
                            for (String poName: poNameList) {
                                String xmlFileName=poName+"Mapper.xml";
                                if (xmlFileName.equals(xmlFile.getName())){
                                    flag=true;
                                    break;
                                }
                            }
                        }else{
                            flag=true;
                        }
                        if (flag){
                            /*logger.info("[generator]删除xml文件：[{}]",xmlFile.getPath()+""+xmlFile.getName());*/
                            xmlFile.delete();
                        }
                    }
                }
            }
            //结束
            /*logger.info("[generator]>>生成：pojo对象和xml");*/
            myBatisGenerator.generate(null);
            /*logger.info("[generator]<<生成：pojo对象和xml");*/
        }catch (Exception e){
            e.printStackTrace();
            /*logger.error("[generator]生成：pojo对象和xml异常，异常内容：{}",e);*/
        }

    }

    private static boolean isSplitTable(String tableName){
        String last=tableName.substring(tableName.lastIndexOf("_")+1,tableName.length());
        if (StringUtils.isNotEmpty(last)){
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(last);
            if(isNum.matches() ){
                return true;
            }
        }
        return false;
    }


    public static void  generatorMybatisFiles(String projectPath,String classPathEntry,String dbhost,String dbport,String dbname,String dbusername,String dbpassword,
                                     String javaModelPackage, List<String> excludeTables){

        generatorMybatisFiles( projectPath, classPathEntry, dbhost, dbport, dbname, dbusername, dbpassword,
                 javaModelPackage,  true, excludeTables);


        //System.out.println(sb.toString());
    }


    public static String underlineToCamel(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=param.charAt(i);
            if (c==UNDERLINE){
                if (++i<len){
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            }else{
                sb.append(c);
            }
        }
        String str=sb.toString();
        char[] cs=str.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs)+"PO";
    }

   /* public static void main(String argus[]){
        String  projectPath = "D:\\v3projects\\erpbiz\\erp-biz-model\\src\\main\\resources\\generator\\generatorConfig.xml";
        String classPathEntry = "D:\\data\\lib\\mysql-connector-java-5.1.44.jar";
        String  dbhost = "192.168.3.133";
        String  dbport = "3306";
        String  dbname = "edb_erp";
        String  dbusername = "root";
        String  dbpassword = "root123";
        String  javaModelPackage = "com.edb01.erp.biz.model.po";
        String  javaModelProjectPath = "D:\\v3projects\\erpbiz\\erp-biz-model\\src\\main\\java";
        String  sqlMapPackage = "mapper.db";
        String  sqlMapProjectPath = "D:\\v3projects\\erpbiz\\erp-biz-model\\src\\main\\resources";

        MybatisTableGenerator.generatorMybatisFiles(xmlFilePath,classPathEntry,dbhost,dbport,dbname,dbusername,dbpassword,javaModelPackage,javaModelProjectPath,sqlMapPackage,sqlMapProjectPath);
    }*/

  /* public static void main(String argus[]){
       System.out.println(isTableNameSplitTable("ts_task_store_goods_13"));
   }*/
}
