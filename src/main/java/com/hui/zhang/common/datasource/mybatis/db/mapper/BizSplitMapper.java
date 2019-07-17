package com.hui.zhang.common.datasource.mybatis.db.mapper;

import com.hui.zhang.common.util.generator.SplitTablePlugin;
import com.hui.zhang.common.util.generator.dto.SplitTableBean;
import com.github.pagehelper.PageInfo;
import com.hui.zhang.common.datasource.mybatis.db.ds.DataSourceSupport;
import com.hui.zhang.common.datasource.mybatis.pager.DataPager;
import com.hui.zhang.common.datasource.mybatis.pager.Pager;
import com.hui.zhang.common.spring.SpringBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class BizSplitMapper implements  IBizMapper {
    private static final Logger logger = LoggerFactory.getLogger(BizSplitMapper.class);
    private String namespace;
    private DataSourceSupport dataSourceSupport;
    private Long key=null;
    public static Map<String,SplitTableBean> splitConfigMap=null;

    /**
     * 构建有分表 自定义sql mapper
     * @param namespace 命名空间
     * @param key 分表key
     */
    public BizSplitMapper(String namespace, long key){
        initSplitMap();
        this.namespace=namespace;
        this.dataSourceSupport= SpringBeanUtil.getBeanByType(DataSourceSupport.class);
        this.key=key;
    }

    /**
     * 插入
     * @param insertId
     * @param parameter
     * @return
     */
    public int insert(String insertId, Map parameter) {
        this.initParameter(parameter);
        insertId=namespace+"."+insertId;
        return dataSourceSupport.insert(insertId, parameter);
    }

    /**
     * 更新
     * @param updateId
     * @param parameter
     * @return
     */
    public int update(String updateId, Map parameter) {
        this.initParameter(parameter);
        updateId=namespace+"."+updateId;
        return dataSourceSupport.update(updateId, parameter);
    }

    /**
     * 删除
     * @param deleteId
     * @param parameter
     * @return
     */
    public int delete(String deleteId, Map parameter) {
        this.initParameter(parameter);
        deleteId=namespace+"."+deleteId;
        return dataSourceSupport.delete(deleteId, parameter);
    }

    /**
     * 查询一个
     * @param selectId
     * @param parameter
     * @param <M>
     * @return
     */
    public <M> M selectOne(String selectId, Map parameter) {
        this.initParameter(parameter);
        selectId=namespace+"."+selectId;
        return dataSourceSupport.selectOne(selectId, parameter);
    }

    /**
     * 查询列表
     * @param selectId
     * @param parameter
     * @param <E>
     * @return
     */
    public <E> List<E> selectList(String selectId, Map parameter) {
        this.initParameter(parameter);
        selectId=namespace+"."+selectId;
        return dataSourceSupport.selectList(selectId, parameter);
    }

    /**
     * 查询列表的第一个
     * @param selectId
     * @param parameter
     * @param <M>
     * @return
     */
    public<M> M  selectFirstFromList(String selectId, Map parameter) {
        this.initParameter(parameter);
        selectId=namespace+"."+selectId;
        List<M> ls=dataSourceSupport.selectList(selectId, parameter);
        if(ls.size()>0){
            return ls.get(0);
        }
        return null;
    }


    public <E> List<E> selectListForPage(String selectId, Map parameter, int page, int size) {
        this.initParameter(parameter);
        selectId=namespace+"."+selectId;
        RowBounds rowBounds=new RowBounds(page,size);
        return dataSourceSupport.selectLimitedList(selectId, parameter, rowBounds);
    }

    /**
     * 分页查询
     * @param selectId
     * @param parameter
     * @param rowBounds
     * @param <E>
     * @return
     */
    public <E> List<E> selectLimitedList(String selectId,Map parameter, RowBounds rowBounds) {
        this.initParameter(parameter);
        selectId=namespace+"."+selectId;
        return dataSourceSupport.selectLimitedList(selectId, parameter, rowBounds);
    }

    /**
     * 通过Example分页查询 DataPager
     * @param selectId
     * @param pager
     * @param <T>
     * @return
     */
    public <T> DataPager<T> selectDataPager (String selectId, Map parameter, Pager pager){
        this.initParameter(parameter);
        selectId=namespace+"."+selectId;
        RowBounds rowBounds = new RowBounds(pager.getPage(), pager.getSize());
        List<T> ls=dataSourceSupport.selectLimitedList(selectId, parameter, rowBounds);

        DataPager dataPager=new DataPager(pager);
        dataPager.setRows(ls);
        PageInfo page = new PageInfo(ls);
        dataPager.setTotalPage(page.getPages());
        dataPager.setTotal(page.getTotal());
        return  dataPager;
    }

    /**
     * 查询总数
     * @param countId
     * @param parameter
     * @return
     */
    public int selectCount(String countId, Map parameter) {
        this.initParameter(parameter);
        countId=namespace+"."+countId;
        return dataSourceSupport.selectOne(countId, parameter);
    }
    private void initParameter(Map parameter){
        if (null!=key){
            for (Map.Entry<String, SplitTableBean> entry : splitConfigMap.entrySet()) {
                SplitTableBean splitTableBean=entry.getValue();
                //System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                //parameter.put(entry.getKey(),entry.getKey()+"_"+key%entry.getValue());
                if (splitTableBean.getSplitType().equals("MODULO")){
                    parameter.put(splitTableBean.getTableName(),splitTableBean.getTableName()+"_"+key%splitTableBean.getTotalSize());
                }else{//APPEND
                    parameter.put(splitTableBean.getTableName(),splitTableBean.getTableName()+"_"+key);
                }

            }
        }
    }
    private void   initSplitMap(){

        if (null==splitConfigMap){
            splitConfigMap=new HashMap();
            String path=  Class.class.getClass().getResource("/").getPath()+"/generator/splitTableConfig.xml";
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
                logger.error("读取{}异常，异常描述：{}","splitTableConfig.xml",e.getMessage());
            }
        }

    }
}
