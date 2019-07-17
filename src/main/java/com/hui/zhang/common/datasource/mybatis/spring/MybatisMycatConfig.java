package com.hui.zhang.common.datasource.mybatis.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.hui.zhang.common.datasource.mybatis.db.ds.DataSourceSupport;
import com.hui.zhang.common.util.etc.bean.DatabaseBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by zuti on 2017/11/7.
 * email zuti@centaur.cn
 */
public abstract class MybatisMycatConfig {

	private String dsName = "";

	public void initDs(String dsName) {
		this.dsName = dsName;
	}

	/**
	 * 根据数据源创建SqlSessionFactory
	 */
	@Bean(name="sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
		fb.setDataSource(dataSource);// 指定数据源(这个必须有，否则报错)
		// 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加

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
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) throws Exception {
		return new DataSourceTransactionManager(dataSource);
	}

	/**
	 * 初始化数据源
	 * @return
	 */
	@Bean(name="dataSource")
	@Primary
	public DataSource dataSource(){
		DataSource ds=null;

		/*DatabaseBean databaseBean= AppPropertyUtil.getDatabaseConfig(dsName);
		Properties props = new Properties();
		String driverClass=databaseBean.getDriverClassName();
		props.put("driverClassName",driverClass);
		String url="jdbc:mysql://"+databaseBean.getHost()+":"+databaseBean.getPort()+"/"+databaseBean.getDbName()+"?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		props.put("url", url);
		props.put("username",databaseBean.getUsername());
		props.put("password",databaseBean.getPassword());
		props.put("initialSize",String.valueOf(databaseBean.getInitialSize()));
		props.put("maxActive",String.valueOf(databaseBean.getMaxActive()));
		props.put("maxIdle",String.valueOf(databaseBean.getMaxIdle()));
		props.put("maxWait",String.valueOf(databaseBean.getMaxWait()));

		//检测链接有效性
		props.put("minEvictableIdleTimeMillis", "1800000");
		props.put("numTestsPerEvictionRun", "3");
		props.put("testOnBorrow", "true");
		props.put("testWhileIdle", "true");
		props.put("testOnReturn", "true");
		props.put("validationQuery", "SELECT 1");*/

		try {
			//ds=  DruidDataSourceFactory.createDataSource(props);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  ds;
	}



}
