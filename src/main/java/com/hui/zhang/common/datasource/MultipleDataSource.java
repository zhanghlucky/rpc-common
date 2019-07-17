package com.hui.zhang.common.datasource;


import com.hui.zhang.common.datasource.es.db.EsDB;
import com.hui.zhang.common.datasource.mongo.db.MongoDB;
import com.hui.zhang.common.datasource.mybatis.db.MybatisDB;
import com.hui.zhang.common.datasource.redis.db.RedisDB;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.MD5Util;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MultipleDataSource{
	//默认数据名称
	private  String defaultRedisName;
	private  String defaultMongoName;
	private  String defaultMysqlShardName;
	private  String defaultMysqlWriteName;
	private  String defaultMysqlReadName;

	private  String defaultEsName;
	private  String defaultEsShardName;

	//数据源存储
	private final static Map<String, RedisDB> REDIS_MAP = new ConcurrentHashMap<>();
	private final static Map<String, MongoDB> MONGO_MAP = new ConcurrentHashMap<>();
	private final static Map<String, MybatisDB> MYBATIS_MAP = new ConcurrentHashMap<>();
	private final static Map<String, EsDB> ES_MAP = new ConcurrentHashMap<>();

	//设置数据源方法
	protected  void setDefaultRedisName(String defaultRedisName){
		this.defaultRedisName=defaultRedisName;
	}
	protected  void setDefaultMongoName(String defaultMongoName){
		this.defaultMongoName=defaultMongoName;
	}
	protected  void setDefaultMysqlShardName(String defaultMysqlShardName){this.defaultMysqlShardName=defaultMysqlShardName;}
	protected  void setDefaultMysqlName(String defaultMysqlWriteName,String defaultMysqlReadName){
		this.defaultMysqlWriteName=defaultMysqlWriteName;
		this.defaultMysqlReadName=defaultMysqlReadName;
	}
	protected void setDefaultEsName(String defaultEsName){
		this.defaultEsName=defaultEsName;
	}
	protected void setDefaultEsShardName(String defaultEsShardName){
		this.defaultEsShardName=defaultEsShardName;
	}


	//初始化数据源方法
	protected abstract void initDefaultDs();

	/**
	 * 取默认的redis数据源
	 * @return
	 */
	public synchronized RedisDB redis(){
		initDefaultDs();
		return this.redis(this.defaultRedisName);
	}

	/**
	 * 按照名称取redis数据源
	 * @param redisName
	 * @return
	 */
	public synchronized RedisDB redis(String redisName){
		//RedisDB redisDB= new RedisDB(redisName);
		RedisDB redisDB=REDIS_MAP.get(redisName);
		if (null==redisDB){
			redisDB=new RedisDB(redisName);
			REDIS_MAP.put(redisName,redisDB);
		}
		return redisDB;
	}

	/**
	 * 取默认mongo数据源
	 * @return
	 */
	public synchronized MongoDB mongo(){
		initDefaultDs();
		return  this.mongo(defaultMongoName);
	}

	/**
	 * 按名称取mongo数据源
	 * @param mongoName
	 * @return
	 */
	public synchronized  MongoDB mongo(String mongoName){
		MongoDB mongoDB=MONGO_MAP.get(mongoName);
		if (null==mongoDB){
			mongoDB=new MongoDB(mongoName);
			MONGO_MAP.put(mongoName,mongoDB);
		}
		return mongoDB;
	}


	/**
	 * 获取默认的 mybatis
	 * @return
	 */
	public synchronized MybatisDB mybatis(){
		initDefaultDs();
		return mybatis(this.defaultMysqlShardName,this.defaultMysqlWriteName,this.defaultMysqlReadName);
	}

	/**
	 * 获取mybatis数据源
	 * @param dsWriteName
	 * @param dsReadName
	 * @return
	 */
	public synchronized MybatisDB mybatis(String dsWriteName,String dsReadName ){
		//initDefaultDs();
		return  mybatis(null,dsWriteName,dsReadName);
	}

	/**
	 * 获得shard
	 * @param shardName
	 * @return
	 */
	public synchronized MybatisDB mybatisShard(String shardName){
		return  mybatis(shardName,null,null);
	}

	private synchronized MybatisDB mybatis(String shardName,String writeName,String readName){
		String key= MD5Util.MD5(shardName+"-"+writeName+"-"+readName);

		MybatisDB mybatisDB=MYBATIS_MAP.get(key);
		if (null==mybatisDB){
			mybatisDB=new MybatisDB(shardName,writeName,readName);
			MYBATIS_MAP.put(key,mybatisDB);
		}
		return mybatisDB;
	}

	/**
	 * 按名称取mybatis
	 * @param mysqlShardName
	 * @return
	 */
	public   MybatisDB mybatis(String mysqlShardName){
		return  new MybatisDB(mysqlShardName,null,null);
	}

	public synchronized EsDB es(){
		initDefaultDs();
		return this.getEs(defaultEsName,defaultEsShardName);
	}
	public synchronized EsDB envEs(String envId){
		initDefaultDs();
		return this.getEs(defaultEsName,defaultEsShardName,envId);
	}

	public synchronized EsDB envEs(String envId,String esName){
		initDefaultDs();
		return this.getEs(esName,defaultEsShardName,envId);
	}

	public synchronized EsDB es(String esName){
		return this.getEs(esName,null);

	}
	public synchronized EsDB esShard(String esShardName){

		return this.getEs(null,esShardName);
	}
	private synchronized EsDB getEs(String esName,String esShardName){

		String key= MD5Util.MD5(esName+"-"+esShardName);

		EsDB esDB=ES_MAP.get(key);
		if (null==esDB){
			esDB=new EsDB(esName,esShardName);
			ES_MAP.put(key,esDB);
		}
		return esDB;
	}

	private synchronized EsDB getEs(String esName,String esShardName,String envId){

		String key= MD5Util.MD5(esName+"-"+esShardName+"-"+envId);

		EsDB esDB=ES_MAP.get(key);
		if (null==esDB){
			esDB=new EsDB(esName,esShardName,envId);
			ES_MAP.put(key,esDB);
		}
		return esDB;
	}


}
