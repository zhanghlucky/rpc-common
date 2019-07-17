package com.hui.zhang.common.datasource.mongo.db;

import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.po.CfgMongoPO;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.hui.zhang.common.datasource.mongo.annotation.CollName;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDB.class);
	private static final Map<String, MongoClient> clients = new ConcurrentHashMap<>();
	//private static final Map<String, String> DBNAME_MAP = new ConcurrentHashMap<>();
	private MongoClient mongoClient;
	private String dbName;

	public MongoDB(String dsCode){
		this.mongoClient=this.getMongoClient(dsCode);
	}

	private synchronized MongoClient getMongoClient(String dsCode){
		this.mongoClient =clients.get(dsCode);
		//this.dbName=DBNAME_MAP.get(dsCode);
		if (null!=mongoClient){
			return mongoClient;
		}
		CfgMongoPO cfgMongoPO= AppConfigUtil.getCfgMongoPO(dsCode);

		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
		build.connectionsPerHost(cfgMongoPO.getConnectionsPerHost());   //与目标数据库能够建立的最大connection数量为50
		build.threadsAllowedToBlockForConnectionMultiplier(50);
		build.maxWaitTime(cfgMongoPO.getMaxWaitTime());
		build.connectTimeout(cfgMongoPO.getConnectTimeout());
		build.socketTimeout( cfgMongoPO.getSocketTimeout());
		MongoClientOptions myOptions = build.build();
		ServerAddress serverAddress=new ServerAddress(cfgMongoPO.getDsHost(),cfgMongoPO.getDsPort());
		// 判断是否启用用户名密码
		if(StringUtils.isNotEmpty(cfgMongoPO.getUsername()) && StringUtils.isNotEmpty(cfgMongoPO.getPassword())){
			List<ServerAddress> lists=new ArrayList<ServerAddress>();
			lists.add(serverAddress);
			MongoCredential credential=MongoCredential.createCredential(cfgMongoPO.getUsername(), "admin", cfgMongoPO.getPassword().toCharArray());
			List<MongoCredential> listm=new ArrayList<MongoCredential>();
			listm.add(credential);
			this.mongoClient=new MongoClient(lists,listm);
			this.dbName=cfgMongoPO.getDbName();
			this.clients.put(dsCode,mongoClient);
			LOGGER.info("初始化mongodb dsCode:{},host:{},port:{},用户名:{},密码:{}",dsCode,cfgMongoPO.getDsHost(),cfgMongoPO.getDsPort(),cfgMongoPO.getUsername(),cfgMongoPO.getPassword());
		}
		else{
			this.mongoClient=new MongoClient(serverAddress,myOptions);
			this.dbName=cfgMongoPO.getDbName();
			this.clients.put(dsCode,mongoClient);
			LOGGER.info("初始化mongodb dsCode:{},host:{},port:{}",dsCode,cfgMongoPO.getDsHost(),cfgMongoPO.getDsPort());
		}
		/*this.mongoClient=new MongoClient(serverAddress,myOptions);
		this.dbName=cfgMongoPO.getDbName();
		this.clients.put(dsCode,mongoClient);*/
		//DBNAME_MAP.put(dsCode,dbName);
		return  mongoClient;

	}
	
	/**
	 * 获得普通collection
	 * @param colName
	 * @return
	 */
	public MongoCollection<Document> getCollection(String colName){
		MongoCollection<Document> mc=this.mongoClient.getDatabase(this.dbName).getCollection(colName);

		return mc;
	}
	
	/**
	 * 获得cls 的 Monog集合（cls 需要有 collName注解）
	 * @return
	 */
	public <T> MongoCollection<Document> getCollection(Class<T> cls) {
		CollName collName = cls.getAnnotation(CollName.class);
		return this.mongoClient.getDatabase(this.dbName).getCollection(collName.value());//MongoClient.open(this.dbName).getCollection(collName.value());
	}


}
