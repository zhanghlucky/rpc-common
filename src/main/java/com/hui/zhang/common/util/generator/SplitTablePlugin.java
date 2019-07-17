package com.hui.zhang.common.util.generator;

import com.hui.zhang.common.datasource.mybatis.db.ds.MybatisUtil;
import com.hui.zhang.common.util.generator.dto.SplitTableBean;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghui on 2017/3/28.
 */
public  class SplitTablePlugin extends PluginAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SplitTablePlugin.class);
    private static final String TYPE_APPEND="APPEND";
    private static final String TYPE_MODULO="MODULO";
    private static final String TABLE_NAME_FIELD = "tableName";
    private static Map<String,SplitTableBean> splitConfigMap=null;

    public SplitTablePlugin(){
        this.initSplitConfigXml();
    }

    /**
     * 在Exmaple类中添加tableName字段
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            Field tableNameField = new Field("tableName", PrimitiveTypeWrapper.getStringInstance());
            // 默认设置为当前的table名字
            tableNameField.setInitializationString("\""+ introspectedTable.getTableConfiguration().getTableName()+ "\"");
            tableNameField.setVisibility(JavaVisibility.PRIVATE);
            addField(topLevelClass, introspectedTable, tableNameField);
        }
        return super.modelExampleClassGenerated(topLevelClass,introspectedTable);
    }

    /**
     * 在object类中添加tableName字段
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            Field tableNameField = new Field(TABLE_NAME_FIELD, PrimitiveTypeWrapper.getStringInstance());
            // 默认设置为当前的table名字
            tableNameField.setInitializationString("\""+ introspectedTable.getTableConfiguration().getTableName()+ "\"");
            tableNameField.setVisibility(JavaVisibility.PRIVATE);
            addField(topLevelClass, introspectedTable, tableNameField);
        }
        return super.modelBaseRecordClassGenerated(topLevelClass,introspectedTable);
    }

    //DeleteByPrimaryKey
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            Object subSentence = new TextElement("delete from ${" + TABLE_NAME_FIELD + "}");
            elements.set(0, (Element) subSentence);
        }
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element,introspectedTable);
    };

    //selectByPrimaryKey
    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            Object subSentence = new TextElement("from ${" + TABLE_NAME_FIELD + "}");
            elements.set(2, (Element) subSentence);
        }
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element,introspectedTable);
    };

    //CountByExample
    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable){
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            String content = elements.get(0).getFormattedContent(0);
            String[] data = content.split(" ");
            data[3] = "${" + TABLE_NAME_FIELD + "}";
            TextElement subSentence = new TextElement(
                    join(" ", data));
            elements.set(0, subSentence);
        }
        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

   //SelectByExample
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            Object subSentence = new TextElement("from ${" + TABLE_NAME_FIELD + "}");
            elements.set(3, (Element) subSentence);
        }
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element,introspectedTable);
    }
    //UpdateByExample
    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            TextElement subSentence = new TextElement("update ${record."
                    + TABLE_NAME_FIELD + "}");
            elements.set(0, subSentence);
        }

        return super.sqlMapUpdateByExampleSelectiveElementGenerated(element,
                introspectedTable);
    };
    //UpdateByExampleWithoutBLOBs
    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            TextElement subSentence = new TextElement("update ${record."
                    + TABLE_NAME_FIELD + "}");
            elements.set(0, subSentence);
        }

        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }
    //UpdateByPrimaryKeyWithoutBLOBs
    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            TextElement subSentence = new TextElement("update ${" + TABLE_NAME_FIELD + "}");
            elements.set(0, subSentence);
        }
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
                element, introspectedTable);
    };
    //UpdateByPrimaryKeySelective
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            TextElement subSentence = new TextElement("update ${" + TABLE_NAME_FIELD + "}");
            elements.set(0, subSentence);
        }
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    };
    //Insert
    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element,
                                                IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            String content = elements.get(0).getFormattedContent(0);
            String[] data = content.split(" ");
            data[2] = "${" + TABLE_NAME_FIELD + "}";
            TextElement subSentence = new TextElement(
                    join(" ", data));
            elements.set(0, subSentence);
        }
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }
    //InsertSelective
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable) {
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            String content = elements.get(0).getFormattedContent(0);
            String[] data = content.split(" ");
            data[2] = "${" + TABLE_NAME_FIELD + "}";
            TextElement subSentence = new TextElement(
                    join(" ", data));
            elements.set(0, subSentence);
        }
        return super.sqlMapInsertSelectiveElementGenerated(element,
                introspectedTable);

    };

    //DeleteByExample
    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable){
        if (isTableSplit(introspectedTable)){
            List<Element> elements = element.getElements();
            String content = elements.get(0).getFormattedContent(0);
            String[] data = content.split(" ");
            data[2] = "${" + TABLE_NAME_FIELD + "}";
            TextElement subSentence = new TextElement(
                    join(" ", data));
            elements.set(0, subSentence);
        }
        return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
    }



    public static String join(String join, String[] strAry) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.length; i++) {
            if (i == (strAry.length - 1)) {
                sb.append(strAry[i]);
            } else {
                sb.append(strAry[i]).append(join);
            }
        }
        return new String(sb);
    }

    /**
     * 取消验证
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 获取系统分隔符
     *
     * @return
     */
    protected String getSeparator() {
        return System.getProperty("line.separator");
    }
    /**
     * 添加字段，同时也添加get,set方法
     *
     * @param topLevelClass
     * @param introspectedTable
     * @param field
     */
    protected void addField(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, Field field) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        // 添加Java字段
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        String fieldName = field.getName();
        // 生成Set方法
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(generateSetMethodName(fieldName));
        method.addParameter(new Parameter(field.getType(), fieldName));
        method.addBodyLine("this." + fieldName + "=" + fieldName + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        // 生成Get方法
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(generateGetMethodName(fieldName));
        method.addBodyLine("return " + fieldName + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        //生成分表方法
        method=new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("generatorTableName");
        method.addParameter(0,new Parameter(new FullyQualifiedJavaType("long"),"key"));
        String key=field.getInitializationString().replace("\"","");
        String line="this." + fieldName + "=" + fieldName + ";";
        if(null!=splitConfigMap.get(key)){
            SplitTableBean splitTableBean=splitConfigMap.get(key);
            if (splitTableBean.getSplitType().equals(TYPE_MODULO)){
                line="this.tableName="+TABLE_NAME_FIELD+"+\"_\"+key%"+splitTableBean.getTotalSize()+";";
            }
            if (splitTableBean.getSplitType().equals(TYPE_APPEND)){
                line="this.tableName="+TABLE_NAME_FIELD+"+\"_\"+key;";
            }

        }
        method.addBodyLine(line);
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    protected static String generateGetMethodName(String fieldName) {
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    protected static String generateSetMethodName(String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }


    //判断是否分表
    private boolean isTableSplit(IntrospectedTable introspectedTable){
        String tableName=introspectedTable.getTableConfiguration().getTableName();
        if (null!=splitConfigMap.get(tableName)){
            return true;
        }
        return false;
    }

    /**
     * 获得分表配置
     */
    private void   initSplitConfigXml(){
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
                    for(Iterator it=root.elementIterator();it.hasNext();){
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
