package com.hui.zhang.common.datasource.mybatis.spring;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hui.zhang.common.datasource.mybatis.db.ShardDsNode;
import com.hui.zhang.common.datasource.mybatis.db.ds.DataSourceSupport;
import com.hui.zhang.common.datasource.mybatis.db.ds.DynamicDataSource;
import com.hui.zhang.common.util.PropertyUtil;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.AppParamsUtil;
import com.hui.zhang.common.util.etc.bean.*;
import com.hui.zhang.common.util.etc.po.CfgDatabasePO;
import com.hui.zhang.common.util.etc.po.CfgShardNodePO;
import com.hui.zhang.common.util.etc.po.CfgShardSpecifyNodeItemPO;
import com.hui.zhang.common.util.etc.vo.CfgShardSpecifyNodeVO;
import com.hui.zhang.common.util.etc.vo.CfgShardVO;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.*;


/**
 * springboot集成mybatis的基本入口
 * 1）创建数据源(如果采用的是默认的tomcat-jdbc数据源，则不需要)
 * 2）创建SqlSessionFactory
 * 3）配置事务管理器，除非需要使用事务，否则不用配置
 */
//@Configuration // 该注解类似于spring配置文件
public abstract class MyBatisConfig {
    private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);
    @Autowired
    private Environment env;
    //private ShardConfig aDefault;
    public static List<ShardDsNode> shardDsNodes=new ArrayList<>();
    public static List<String> shardDsNames=new ArrayList<>();
    public static List<String> dsNames=new ArrayList<>();

    public static final String DS_TYPE_WRITE="write";
    public static final String DS_TYPE_READ="read";
    public String defaultDsName = StringUtils.EMPTY;


    /**
     * 初始化数据源
     */
    protected abstract void  initDs();

    /**
     * 设置默认数据源
     */
    protected abstract void initDefaultDs();

    /**
     * 添加shard数据源
     * @param shardDsName
     */
    protected void addShardDsName(String shardDsName){
        shardDsNames.add(shardDsName);
    }

    /**
     * 添加普通数据源
     * @param dsName
     */
    protected void addDsName(String dsName){
        dsNames.add(dsName);
    }

    /**
     * 设置默认数据源
     * @param dsName
     */
    protected void setDefaultDsName(String dsName) {
        this.defaultDsName = dsName;
    }

    /**
     * @Primary 该注解表示在同一个接口有多个实现类可以注入的时候，默认选择哪一个，而不是让@autowire注解报错
     * @Qualifier 根据名称进行注入，通常是在具有相同的多个类型的实例的一个注入（例如有多个DataSource类型的实例）
     */
    @Bean(name="dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource() {

        initDs();//初始化数据源
        initDefaultDs();//设置默认数据源

        Map<Object, Object> targetDataSourcesMap = new HashMap<>();
        DataSource defaultDs=null;
        for (String shardDsName:shardDsNames) {
            this.addShardDs(targetDataSourcesMap,shardDsName);
        }
        for (String dsName:dsNames) {
            this.addDs(targetDataSourcesMap,dsName);
        }

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSourcesMap);// 该方法是AbstractRoutingDataSource的方法

        //设置默认数据源
        if (StringUtils.isNotEmpty(defaultDsName)) {
            defaultDs = (DataSource) targetDataSourcesMap.get(defaultDsName);
        } else {
            logger.info("未设置默认数据源，随机取默认数据源");
            for (Map.Entry<Object, Object> entry : targetDataSourcesMap.entrySet()) {
                defaultDs= (DataSource)entry.getValue();
                break;
            }
        }
        if (defaultDs == null) {
            throw new IllegalArgumentException("default datasource error!");
        }

        dataSource.setDefaultTargetDataSource(defaultDs);

        return dataSource;
    }

    //添加普通数据源
    private   void addDs(Map<Object, Object> targetDataSourcesMap,String dsName){
        DataSource ds=this.initDataSource(dsName);
        targetDataSourcesMap.put(dsName,ds);
    }

    //添加shard数据源
    private void addShardDs(Map<Object, Object> targetDataSourcesMap,String shardName) {

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
    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setDataSource(dynamicDataSource);// 指定数据源(这个必须有，否则报错)

        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml"));
        fb.setConfigLocation(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis.xml")[0]);
        return fb.getObject();
    }
    
    @Bean(name="dataSourceSupport")
    public DataSourceSupport dataSourceSupport(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory){
    	DataSourceSupport dss=new DataSourceSupport();
    	dss.setSqlSessionFactory(sqlSessionFactory);
    	return dss;
    }
    
    /**
     * 配置事务管理器
     */
    @Bean(name="txManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) throws Exception {
        return new DataSourceTransactionManager(dynamicDataSource);
    }


    /**
     * 初始化数据源
     * @return
     */
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
        ///////
        props.put("timeBetweenEvictionRunsMillis","60000");
        props.put("minEvictableIdleTimeMillis","300000");
        props.put("validationQuery","SELECT 1");
        props.put("testWhileIdle","true");
        props.put("testOnBorrow","true");
        props.put("testOnReturn","false");
        props.put("poolPreparedStatements","false");
        props.put("maxPoolPreparedStatementPerConnectionSize","200");
        // 连接池释放 临时屏蔽
        props.put("removeAbandoned","true"); //<!-- 打开removeAbandoned功能 -->
        props.put("removeAbandonedTimeout","18000"); //<!-- 1800秒，也就是30分钟 -->
        props.put("logAbandoned","true"); //<!-- 关闭abanded连接时输出错误日志 -->


        try {
            ds=  DruidDataSourceFactory.createDataSource(props);

            String env=System.getProperty("env");//读取 centaur-seata 的 env 设置。没有表示未引入此包，不支持分布式事物
            if (StringUtils.isNotEmpty(env)){
                logger.info("程序参数enabled_seata 为true,使用seata代理数据源，支持分布式事物");
                //分布式事物 start
                DataSource proxy=new DataSourceProxy(ds);//返回代理数据源
                return proxy;
                //分布式事物 end
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  ds;
    }

}
