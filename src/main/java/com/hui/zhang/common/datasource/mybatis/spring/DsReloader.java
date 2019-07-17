package com.hui.zhang.common.datasource.mybatis.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hui.zhang.common.datasource.mybatis.db.ShardDsNode;
import com.hui.zhang.common.datasource.mybatis.db.ds.DynamicDataSource;
import com.hui.zhang.common.spring.SpringBeanUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.po.CfgDatabasePO;
import com.hui.zhang.common.util.etc.po.CfgShardNodePO;
import com.hui.zhang.common.util.etc.po.CfgShardSpecifyNodeItemPO;
import com.hui.zhang.common.util.etc.vo.CfgShardSpecifyNodeVO;
import com.hui.zhang.common.util.etc.vo.CfgShardVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by zhanghui on 2018/1/17.
 */

/**
 * 数据源重载类
 */
public class DsReloader {
    private static final Logger logger = LoggerFactory.getLogger(DsReloader.class);
    public static final DsReloader INSTANCE = new DsReloader();

    private static List<ShardDsNode> shardDsNodes=new ArrayList<>();
    private List<String> shardDsNames=new ArrayList<>();
    private List<String> dsNames=new ArrayList<>();
    private static final String DS_TYPE_WRITE="write";
    private static final String DS_TYPE_READ="read";

    /**
     * 重载所有数据源
     */
    public  void  reloadDs(){
        logger.info("reload all ds");
        this.shardDsNames=MyBatisConfig.shardDsNames;
        this.dsNames= MyBatisConfig.dsNames;

        DynamicDataSource dynamicDataSource= SpringBeanUtil.getBean("dynamicDataSource",DynamicDataSource.class);
        //dynamicDataSource.addDataSource("dskey",initDataSource("dsName"));//追加数据源
        Map<Object, Object> targetDataSourcesMap = new HashMap<>();
        DataSource defaultDs=null;

        for (String shardDsName:shardDsNames) {
            this.addShardDs(targetDataSourcesMap,shardDsName);
        }
        for (String dsName:dsNames) {
            this.addDs(targetDataSourcesMap,dsName);
        }
        //dynamicDataSource.setTargetDataSources(targetDataSourcesMap);
        dynamicDataSource.setTargetDataSources(targetDataSourcesMap);
        //dynamicDataSource.setDefaultTargetDataSource(null);
        dynamicDataSource.setLenientFallback(true);
        dynamicDataSource.afterPropertiesSet();

    }

    //添加普通数据源
    private   void addDs(Map<Object, Object> targetDataSourcesMap,String dsName){
        DataSource ds=this.initDataSource(dsName);
        targetDataSourcesMap.put(dsName,ds);
    }

    //添加shard数据源
/*    private void addShardDs(Map<Object, Object> targetDataSourcesMap,String shardName){

        ShardBean shardBean=AppPropertyUtil.getShardConfig(shardName);//重新加载配置
        List<ShardNodeBean> list=shardBean.getShardNodeBeanList();
        for (ShardNodeBean shardNodeBean: list) {

            ShardDsNode readShardDsNode=new ShardDsNode(shardName,shardNodeBean.getStart(),shardNodeBean.getEnd(),DS_TYPE_READ);
            shardDsNodes.add(readShardDsNode);
            DataSource readDs=this.initDataSource(shardNodeBean.getReadDsName());
            targetDataSourcesMap.put(readShardDsNode.getDsKey(),readDs);

            ShardDsNode writeShardDsNode=new ShardDsNode(shardName,shardNodeBean.getStart(),shardNodeBean.getEnd(),DS_TYPE_WRITE);
            shardDsNodes.add(writeShardDsNode);
            DataSource writeDs=this.initDataSource(shardNodeBean.getWriteDsName());
            targetDataSourcesMap.put(writeShardDsNode.getDsKey(),writeDs);
        }

        List<ShardSpecifyNodeBean> specifyList=shardBean.getShardSpecifyNodeBeanList();
        for (ShardSpecifyNodeBean shardSpecifyNodeBean: specifyList) {
            List<Long> idList=new ArrayList<>();
            for (ShardSpecifyNodeIdBean shardSpecifyNodeIdBean :shardSpecifyNodeBean.getShardSpecifyNodeIdBeanList()) {
                idList.add(shardSpecifyNodeIdBean.getDataId());
            }
            ShardDsNode readShardDsNode=new ShardDsNode(shardName,idList,DS_TYPE_READ);
            shardDsNodes.add(readShardDsNode);
            DataSource readDs=this.initDataSource(shardSpecifyNodeBean.getReadDsName());
            targetDataSourcesMap.put(readShardDsNode.getDsKey(),readDs);

            ShardDsNode writeShardDsNode=new ShardDsNode(shardName,idList,DS_TYPE_WRITE);
            shardDsNodes.add(writeShardDsNode);
            DataSource writeDs=this.initDataSource(shardSpecifyNodeBean.getWriteDsName());
            targetDataSourcesMap.put(writeShardDsNode.getDsKey(),writeDs);
        }

    }*/

    private void addShardDs(Map<Object, Object> targetDataSourcesMap,String shardName){

        CfgShardVO cfgShardVO = AppConfigUtil.getCfgShardVO(shardName);
        List<CfgShardNodePO> cfgShardNodePOList = cfgShardVO.getCfgShardNodePOList();
        for (CfgShardNodePO cfgShardNodePO : cfgShardNodePOList) {

            ShardDsNode readShardDsNode = new ShardDsNode(shardName, cfgShardNodePO.getStart(), cfgShardNodePO.getEnd(), DS_TYPE_READ);
            shardDsNodes.add(readShardDsNode);
            DataSource readDs = this.initDataSource(cfgShardNodePO.getReadDsCode());
            targetDataSourcesMap.put(readShardDsNode.getDsKey(), readDs);

            ShardDsNode writeShardDsNode = new ShardDsNode(shardName, cfgShardNodePO.getStart(), cfgShardNodePO.getEnd(), DS_TYPE_WRITE);
            shardDsNodes.add(writeShardDsNode);
            DataSource writeDs = this.initDataSource(cfgShardNodePO.getWriteDsCode());
            targetDataSourcesMap.put(writeShardDsNode.getDsKey(), writeDs);
        }

        List<CfgShardSpecifyNodeVO> specifyList = cfgShardVO.getCfgShardSpecifyNodeVOList();
        for (CfgShardSpecifyNodeVO cfgShardSpecifyNodeVO : specifyList) {
            List<Long> idList = new ArrayList<>();
            for (CfgShardSpecifyNodeItemPO cfgShardSpecifyNodeItemPO : cfgShardSpecifyNodeVO.getCfgShardSpecifyNodeItemPOList()) {
                idList.add(cfgShardSpecifyNodeItemPO.getDataId());
            }
            ShardDsNode readShardDsNode = new ShardDsNode(shardName, idList, DS_TYPE_READ);
            shardDsNodes.add(readShardDsNode);
            DataSource readDs = this.initDataSource(cfgShardSpecifyNodeVO.getReadDsCode());
            targetDataSourcesMap.put(readShardDsNode.getDsKey(), readDs);

            ShardDsNode writeShardDsNode = new ShardDsNode(shardName, idList, DS_TYPE_WRITE);
            shardDsNodes.add(writeShardDsNode);
            DataSource writeDs = this.initDataSource(cfgShardSpecifyNodeVO.getWriteDsCode());
            targetDataSourcesMap.put(writeShardDsNode.getDsKey(), writeDs);
        }

    }

    /*private  DataSource initDataSource(String dsName){
        DataSource ds=null;

        DatabaseBean databaseBean= AppPropertyUtil.getDatabaseConfig(dsName);
        Properties props = new Properties();
        String jdbcUrl = "";
        String driverClass=databaseBean.getDriverClassName();
        if (driverClass.indexOf("com.mysql.jdbc.Driver") > -1){
            jdbcUrl = "jdbc:mysql://"+databaseBean.getHost()+":"+databaseBean.getPort()+"/"+databaseBean.getDbName()+"?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        }
        else{
            jdbcUrl = "jdbc:sqlserver://"+databaseBean.getHost()+":"+databaseBean.getPort()+";DatabaseName="+databaseBean.getDbName();
        }
        props.put("driverClassName",driverClass);
        props.put("url", jdbcUrl);
        props.put("username",databaseBean.getUsername());
        props.put("password",databaseBean.getPassword());
        props.put("initialSize",String.valueOf(databaseBean.getInitialSize()));
        props.put("maxActive",String.valueOf(databaseBean.getMaxActive()));
        props.put("maxIdle",String.valueOf(databaseBean.getMaxIdle()));
        props.put("maxWait",String.valueOf(databaseBean.getMaxWait()));

        try {
            ds=  DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  ds;
    }*/

    private DataSource initDataSource(String dsCode){
        DataSource ds=null;

        CfgDatabasePO cfgDatabasePO= AppConfigUtil.getCfgDatabasePO(dsCode);
        Properties props = new Properties();
        String jdbcUrl = "";
        String driverClass=cfgDatabasePO.getDriverClassName();
        if (driverClass.indexOf("com.mysql.jdbc.Driver") > -1){
            jdbcUrl = "jdbc:mysql://"+cfgDatabasePO.getDsHost()+":"+cfgDatabasePO.getDsPort()+"/"+cfgDatabasePO.getDbName()+"?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
        }
        else{
            jdbcUrl = "jdbc:sqlserver://"+cfgDatabasePO.getDsHost()+":"+cfgDatabasePO.getDsPort()+";DatabaseName="+cfgDatabasePO.getDbName();
        }
        props.put("driverClassName",driverClass);
        props.put("url", jdbcUrl);
        props.put("username",cfgDatabasePO.getUsername());
        props.put("password",cfgDatabasePO.getPassword());
        props.put("initialSize",String.valueOf(cfgDatabasePO.getInitialSize()));
        props.put("maxActive",String.valueOf(cfgDatabasePO.getMaxActive()));
        props.put("maxIdle",String.valueOf(cfgDatabasePO.getMaxIdle()));
        props.put("maxWait",String.valueOf(cfgDatabasePO.getMaxWait()));
        props.put("filters","stat");//
        /////
        props.put("timeBetweenEvictionRunsMillis","60000");
        props.put("minEvictableIdleTimeMillis","300000");
        props.put("validationQuery","SELECT 1");
        props.put("testWhileIdle","true");
        props.put("testOnBorrow","true");
        props.put("testOnReturn","false");
        props.put("poolPreparedStatements","false");
        props.put("maxPoolPreparedStatementPerConnectionSize","200");

        try {
            ds=  DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  ds;
    }
}
