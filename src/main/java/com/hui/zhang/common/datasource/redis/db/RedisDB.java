package com.hui.zhang.common.datasource.redis.db;

import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.po.CfgRedisPO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RedisDB {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisDB.class);

	private  String dsCode;
	private  String envId;
	private final static Map<String, JedisPool> pools = new ConcurrentHashMap<>();
	private final static Map<String, JedisSentinelPool> shard_pools = new ConcurrentHashMap<>();

    public RedisDB(String dsCode) {
		this.dsCode=dsCode;
    }

	public RedisDB(String dsCode,String envId) {
		this.dsCode=dsCode;
		this.envId=envId;
	}

	private synchronized JedisPool getPool(String dsCode) {
		JedisPool pool = pools.get(dsCode);
		if (pool != null) {
			return pool;
		}
		CfgRedisPO cfgRedisPO= AppConfigUtil.getCfgRedisPO(dsCode);
		if (cfgRedisPO.getDsHost().contains(":")){
			return null;
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(cfgRedisPO.getMaxIdle());
		config.setMaxWaitMillis(cfgRedisPO.getMaxWait());
		config.setTestOnBorrow(false);
		if (StringUtils.isNotEmpty(cfgRedisPO.getPassword())){
			pool = new JedisPool(config, cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort(),2000,cfgRedisPO.getPassword());
		}else {
			pool = new JedisPool(config, cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort());
		}
		pools.put(cfgRedisPO.getDsCode(),pool);
		LOGGER.info("初始化redis. dsCode:{} host:{},port:{}",dsCode,cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort());
		return pool;
	}

	/**
	 * 哨兵模式 （带 envID）
	 * @param dsCode
	 * @param envId
	 * @return
	 */
	private  synchronized   JedisSentinelPool  getShardJedisPool(String dsCode,String envId){
		JedisSentinelPool  pool = shard_pools.get(dsCode + "_" + envId);
		if (null != pool){
			return  pool;
		}
		CfgRedisPO cfgRedisPO= AppConfigUtil.getCfgRedisPO(dsCode,envId);
		if (!cfgRedisPO.getDsHost().contains(":")){
			return null;
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(cfgRedisPO.getMaxIdle());
		config.setMaxWaitMillis(cfgRedisPO.getMaxWait());
		config.setTestOnBorrow(false);
		Set<String> sentinels = new  LinkedHashSet();
		if (null != cfgRedisPO.getDsHost()){
			String [] hosts = cfgRedisPO.getDsHost().split(",");
			for (int i = 0; i < hosts.length; i++){
				String [] host = hosts[i].split(":");
				sentinels.add(String.format("%s:%s", host[0], host[1]));
			}
		}
		if(StringUtils.isEmpty(cfgRedisPO.getPassword())){
			 pool= new JedisSentinelPool("mymaster", sentinels, config, 4000);
		}else{
			pool= new JedisSentinelPool("mymaster", sentinels, config, 4000, cfgRedisPO.getPassword());
		}
		shard_pools.put(cfgRedisPO.getDsCode()+ "_"+ envId,pool);
		LOGGER.info("初始化shard redis. dsCode:{} host:{},port:{}",dsCode,cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort());
		return  pool;
	}

	/**
	 * 哨兵模式 不带 envid
	 * @param dsCode
	 * @return
	 */
	private  synchronized   JedisSentinelPool  getShardJedisPool(String dsCode){
		JedisSentinelPool  pool = shard_pools.get(dsCode);
		if (null != pool){
			return  pool;
		}
		CfgRedisPO cfgRedisPO= AppConfigUtil.getCfgRedisPO(dsCode);
		if (!cfgRedisPO.getDsHost().contains(":")){
			return null;
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(cfgRedisPO.getMaxIdle());
		config.setMaxWaitMillis(cfgRedisPO.getMaxWait());
		config.setTestOnBorrow(false);
		Set<String> sentinels = new  LinkedHashSet();
		if (null != cfgRedisPO.getDsHost()){
			String [] hosts = cfgRedisPO.getDsHost().split(",");
			for (int i = 0; i < hosts.length; i++){
				String [] host = hosts[i].split(":");
				sentinels.add(String.format("%s:%s", host[0], host[1]));
			}
		}
		if(StringUtils.isEmpty(cfgRedisPO.getPassword())){
			pool= new JedisSentinelPool("mymaster", sentinels, config, 4000);
		}else{
			pool= new JedisSentinelPool("mymaster", sentinels, config, 4000, cfgRedisPO.getPassword());
		}
		shard_pools.put(dsCode,pool);
		return  pool;
	}



	private synchronized JedisPool getPool(String dsCode,String envId) {
		JedisPool pool = pools.get(dsCode + "_" +envId);
		if (pool != null) {
			return pool;
		}

		CfgRedisPO cfgRedisPO= AppConfigUtil.getCfgRedisPO(dsCode,envId);
		if (cfgRedisPO.getDsHost().contains(":")){
			return null;
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(cfgRedisPO.getMaxIdle());
		config.setMaxWaitMillis(cfgRedisPO.getMaxWait());
		config.setTestOnBorrow(false);
		if (StringUtils.isNotEmpty(cfgRedisPO.getPassword())){
			pool = new JedisPool(config, cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort(),2000,cfgRedisPO.getPassword());
		}else {
			pool = new JedisPool(config, cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort());
		}
		pools.put(cfgRedisPO.getDsCode()+ "_"+ envId,pool);
		LOGGER.info("初始化redis. dsCode:{} host:{},port:{}",dsCode,cfgRedisPO.getDsHost(),cfgRedisPO.getDsPort());
		return pool;
	}

	/**
	 * fix by 栾天
	 * @return
	 */
	private Jedis getJedisPlloResource(){
		/*JedisPool jedisPool =this.getPool(this.dsCode);
		if (null!=jedisPool){
			return jedisPool.getResource();
		}else{
			if (StringUtils.isNotEmpty(envId)){
				JedisSentinelPool shardedJedisPool=this.getShardJedisPool(this.dsCode,this.envId);
				if (null!=shardedJedisPool){
					return  shardedJedisPool.getResource();
				}
			}
			JedisSentinelPool shardedJedisPool=this.getShardJedisPool(this.dsCode);
			return  shardedJedisPool.getResource();
		}*/
		if (StringUtils.isEmpty(envId)){
			JedisPool jedisPool =this.getPool(this.dsCode);
			if (null !=jedisPool){
				return  jedisPool.getResource();
			}else{
				JedisSentinelPool shardedJedisPool=this.getShardJedisPool(this.dsCode);
				if (null != shardedJedisPool){
					return  shardedJedisPool.getResource();
				}
			}
		}else{
			JedisPool jedisPool =this.getPool(this.dsCode,this.envId);
			if (null != jedisPool){
				return jedisPool.getResource();
			}else{
				JedisSentinelPool shardedJedisPool=this.getShardJedisPool(this.dsCode,this.envId);
				if (null != shardedJedisPool){
					return  shardedJedisPool.getResource();
				}
			}
		}
		LOGGER.info("获取redis 实例失败");
		return null;
	}
    /**
     * 设置缓存对象
     * @param key
     * @param value
     * @param seconds
     */
    public void set(Object key,Object value,int seconds){
    	
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()) {
    		String skey=String.valueOf(key);
    		jedis.set(skey, JsonEncoder.DEFAULT.encode(value));
    		if (seconds>0){
				jedis.expire(skey, seconds);
			}
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.set(key:{},value:{},secods:{}) {}",key,value,seconds,e);
	    	throw e;
		}
    	
    	
    }

    
    
	
    /**
     * 设置值  keyPrefix+key 为存储的key
     * @param keyPrefix key前缀
     * @param key key值
     * @param value
     * @param seconds
     */
    public void set(String keyPrefix ,Object key,Object value,int seconds){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()) {
    			String skey=keyPrefix+"_"+String.valueOf(key);
            jedis.set(skey, JsonEncoder.DEFAULT.encode(value));
			if (seconds>0){
				jedis.expire(skey, seconds);
			}
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.set(keyPrefix:{},key:{},value:{},secods:{}) {}",keyPrefix,key,value,seconds,e);
          throw e;
		}
    }
    

    
    /**
     * 获取值keyPrefix+key 为存储的key
     * @param keyPrefix key前缀
     * @param key key值
     * @param clazz
     * @return
     */
    public <T> T get(String keyPrefix ,Object key,Class<T> clazz){
    	//ShardedJedis jedis = this.getJedisPlloResource();
		Jedis jedis = this.getJedisPlloResource();
		try{
    		 String skey=keyPrefix+"_"+String.valueOf(key);
    		 String json=jedis.get(skey);
    		 if (StringUtils.isNotEmpty(json)){
				 return JsonEncoder.DEFAULT.decode(json, clazz);
			 }else {
    		 	 return null;
			 }
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.get(keyPrefix:{},key:{},clazz:{}) {}",keyPrefix,key,clazz.getSimpleName(),e);
	    	throw e;
		}finally {
			jedis.close(); // TODO 临时修改.测试性能
		}
    }

	/**
	 * 获取值keyPrefix+key 为存储的key
	 * @param keyPrefix key前缀
	 * @param key 值
	 * @param collectionCls 集合class
	 * @param objCls 对象class
	 * @param <T> 集合
	 * @param <M> 对象
	 * @return
	 */
	public <T,M> T get(String keyPrefix ,Object key,Class<T> collectionCls, Class<M> objCls){
		//ShardedJedis jedis = this.getJedisPlloResource();
		Jedis jedis = this.getJedisPlloResource();
		try{
			String skey=keyPrefix+"_"+String.valueOf(key);
			String json=jedis.get(skey);
			if (StringUtils.isNotEmpty(json)){
				return JsonEncoder.DEFAULT.decode(json, collectionCls,objCls);
			}else {
				return null;
			}
		}catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.get(keyPrefix:{},key:{},collectionCls:{}) {}",keyPrefix,key,collectionCls.getSimpleName(),e);
			throw e;
		}finally {
			jedis.close(); // TODO 临时修改.测试性能
		}
	}

    
    /**
     * 取得缓存对象
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(Object key,Class<T> clazz){
    	//ShardedJedis jedis = this.getJedisPlloResource();
		Jedis jedis = this.getJedisPlloResource();
    	try{
    		String skey=String.valueOf(key);
			String json=jedis.get(skey);
			if (StringUtils.isNotEmpty(json)){
				return JsonEncoder.DEFAULT.decode(json, clazz);
			}else {
				return null;
			}

	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.get(key:{},clazz:{}) {}",key,clazz.getSimpleName(),e);
          throw e;
		}finally {
			jedis.close(); // TODO 临时修改.测试性能
		}
    }

	/**
	 * 取得缓存对象
	 * @param key key
	 * @param collectionCls 集合class
	 * @param objCls 对象class
	 * @param <T> 集合
	 * @param <M> 对象
	 * @return
	 */
	public <T,M> T get(String key,Class<T> collectionCls, Class<M> objCls){
		//ShardedJedis jedis = this.getJedisPlloResource();
		Jedis jedis = this.getJedisPlloResource();
		try{
			String skey=String.valueOf(key);
			String json=jedis.get(skey);
			if (StringUtils.isNotEmpty(json)){
				return JsonEncoder.DEFAULT.decode(json, collectionCls,objCls);
			}else {
				return null;
			}

		}catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.get(key:{},clazz:{}) {}",key,collectionCls.getSimpleName(),e);
			throw e;
		}finally {
			jedis.close(); // TODO 临时修改.测试性能
		}
	}
    
    
	
    /**
	 * tian.luan
     * 分布式锁
     * @param key   名称
     * @param value 缓存值
     * @param seconds 过期时间
     * @return
     */
    public boolean setnx(String key,Object value,int seconds){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    		 Long result = jedis.setnx(key, JsonEncoder.DEFAULT.encode(value));
			if (seconds>0 && result == 1){
				jedis.expire(key, seconds);
			}
             return result == 1;
	    }catch (Exception e) {
	    	 LOGGER.error("ERROR-> RedisDB.get(key:{},value:{},seconds:{}) {}",key,value,seconds,e);
             throw e;
		}
    }
    
   
    

    
 
        
    /**
     * 设置值
     * @param key
     * @param field
     * @param value
     * @param seconds
     */
    public void hset(String key, String field, String value,int seconds){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			jedis.hset(key, field, value);
			if (seconds>0){
				jedis.expire(key, seconds);
			}
		}catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.hset(key:{},field:{},value:{},seconds:{}) {}",key,field,value,seconds,e);
			throw e;
		}
	}

	public void hset(String key, String field, Object value,int seconds){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			jedis.hset(key, field, JsonEncoder.DEFAULT.encode(value));
			if (seconds>0){
				jedis.expire(key, seconds);
			}
		}catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.hset(key:{},field:{},value:{},seconds:{}) {}",key,field,value,seconds,e);
			throw e;
		}
	}

    /**
     * 设置值
     * @param keyPrefix
     * @param key
     * @param field
     * @param value
     * @param seconds
     */
    public void hset(String keyPrefix,Object key, String field, Object value,int seconds){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    			String skey=keyPrefix+"_"+String.valueOf(key);
            jedis.hset(skey, field, JsonEncoder.DEFAULT.encode(value));
			if (seconds>0){
				jedis.expire(skey, seconds);
			}
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hset(keyPrefix:{},key:{},field:{},value:{},seconds:{}) {}",keyPrefix,key,field,value,seconds,e);
          throw e;
		}
    }
    
   /**
    * 设置超时时间
    * @param keyPrefix
    * @param key
    * @param seconds
    */
    public void expire(String keyPrefix,Object key,int seconds){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			String skey=keyPrefix+"_"+String.valueOf(key);
			if (seconds>0){
				jedis.expire(skey, seconds);
			}
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.expire(keyPrefix:{},key:{},secods:{}) {}",keyPrefix,key,seconds,e);
          throw e;
		}
    	
    }
    
    
    /**
     * 取得值
     * @param key
     * @param field
     * @param clazz
     * @return
     */
    public <T> T hget(String key,String field,Class<T> clazz){
		try(Jedis jedis = this.getJedisPlloResource()){
			String json=jedis.hget(key,field);
			if (StringUtils.isNotEmpty(json)){
				return JsonEncoder.DEFAULT.decode(json, clazz);
			}else {
				return null;
			}

		}catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.hget(key:{},field:{},clazz:{}) {}",key,field,clazz.getSimpleName(),e);
			throw e;
		}
    }

	public <T> T hget(String key,String field,Class<T> clazz,Class... constructClazzs){
		try(Jedis jedis = this.getJedisPlloResource()){
			String json=jedis.hget(key,field);
			if (StringUtils.isNotEmpty(json)){
				return JsonEncoder.DEFAULT.decode(json, clazz, constructClazzs);
			}else{
				return null;
			}
		}catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.hget(key:{},field:{},clazz:{}) {}",key,field,clazz.getSimpleName(),e);
			throw e;
		}
	}

    /**
     * 获取对象
     * @param keyPrefix
     * @param key
     * @param field
     * @param clazz
     * @return
     */
    public <T> T hget(String keyPrefix,Object key,String field,Class<T> clazz){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			String skey=keyPrefix+"_"+String.valueOf(key);
			String json=jedis.hget(skey,field);
			if (StringUtils.isNotEmpty(json)){
				return JsonEncoder.DEFAULT.decode(json, clazz);
			}else {
				return null;
			}
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hget(keyPrefix:{},key:{},field:{},clazz:{}) {}",keyPrefix,key,field,clazz.getSimpleName(),e);
          throw e;
		}
    
    }
    /**
     * 删除redis数据
     * @param keyPrefix
     * @param key
     */
    public void remove(String keyPrefix ,Object key){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    		 String skey=keyPrefix+"_"+String.valueOf(key);
    		 jedis.del(skey);
	    }catch (Exception e) {
	    	 LOGGER.error("ERROR-> RedisDB.remove(keyPrefix:{},key:{}) {}",keyPrefix,key,e);
          throw e;
		}
    	
    }
    /**
     * 删除数据
     * @param key
     */
    public void remove(String key){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    		 	 jedis.del(key);
	    }catch (Exception e) {
	    	 LOGGER.error("ERROR-> RedisDB.remove(key:{}) {}",key,e);
          throw e;
		}
    	
    }
    
    
    /**
     * 删除redis
     * @param key
     * @param field
     */
    public void hremove(String key ,String field){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    		 	 //String skey=keyPrefix+"_"+String.valueOf(key);
    		 jedis.hdel(key,field);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hremove(key:{},field:{}) {}",key,field,e);
          throw e;
		}
    }
    
    /**
     * 删除值
     * @param keyPrefix
     * @param key
     * @param field
     */
    public void hremove(String keyPrefix,Object key ,String field){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			String skey=keyPrefix+"_"+String.valueOf(key);
    		 jedis.hdel(skey,field);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hremove(key:{},field:{}) {}",key,field,e);
          throw e;
		}
    }


    

    
    
    /**
     * 哈希表中设置多个值
     * @param key   hashKey 名称
     * @param map 多个键值对
     * @param seconds 过期时间
     */
    public void hmset(String key, Map<String, String> map,int seconds) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    			jedis.hmset(key, map);
			if (seconds>0){
				jedis.expire(key, seconds);
			}
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hmset(key:{},map:{}) {}",key,map.size(),e);
         throw e;
		} 
    }
    
    
    /**
     * 获取过个缓存值
     * @param key hashKey 名称
     * @param fields 多个键
     * @return
     */
    public List<String> hmget(String key, String[] fields) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    			return jedis.hmget(key, fields);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hmget(key:{},fields:{}) {}",key,fields.length,e);
	    	throw e;
		} 
    }
    
    /**
     * 获取一个哈希表中的所有field名
     * @param key hashKey 名称
     * @return
     */
    public Set<String> hkeys(String key) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    			return jedis.hkeys(key);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hkeys(key:{}) {}",key,e);
	    	throw e;
		} 
    }
    
    /**
     * 取一个哈希表中的所有field名
     * @param keyPrefix
     * @param key
     * @return
     */
    public Set<String> hkeys(String keyPrefix,Object key) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {

		try(Jedis jedis = this.getJedisPlloResource()){
			String skey=keyPrefix+"_"+String.valueOf(key);

    		return jedis.hkeys(skey);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hkeys(key:{}) {}",key,e);
	    	throw e;
		} 
    }
    
    
    /**
     * 哈希表 key 中所有域的值
     * @param key hashKey 名称
     * @return 
     */
    public List<String> hvals(String key) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    		 	 return jedis.hvals(key);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hvals(key:{}) {}",key,e);
	    	throw e;
		} 
    }
    
    /**
     * 返回哈希表 key 中，所有的域和值
     * @param key key hashKey 名称
     * @return
     */
    public Map<String, String> hgetAll(String key) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    			return jedis.hgetAll(key);
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hgetAll(key:{}) {}",key,e);
	    	throw e;
		} 
    }
    
    /**
     * 返回哈希表 key 中，所有的域和值
     * @param keyPrefix
     * @param key
     * @return
     */
    public <T> Map<String,String> hgetAll(String keyPrefix,Object key) {
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
    			String skey=keyPrefix+"_"+String.valueOf(key);
    		
    		Map<String,String> map=jedis.hgetAll(skey);
    		return map;
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.hgetAll(keyPrefix:{},key:{}) {}",keyPrefix,key,e);
	    	throw e;
		} 
    }
    
    
    /**
     * 计数器
     * @param key
     * @return
     */
    public long inc(String key){
    	//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
            long count = jedis.incr(key);
            return count;
	    }catch (Exception e) {
	    	LOGGER.error("inc failed error: {}",e);
	    	throw e;
		}
    }

	/**
	 * 根据某个数值递增
	 * 加入超时时间
	 * @param key
	 * @param value
	 * @return
	 */
	public long  incrby(String key,int value,int second){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			boolean exist = jedis.exists(key);
			long count = jedis.incrBy(key, value);
			if(!exist){
				// 默认存储10秒
				jedis.expire(key, second);
			}
			return count;
		}
		catch (Exception e) {
			LOGGER.error("ERROR-> incrby(key:{}) {}",key,e);
			throw e;
		}
	}
	/**
	 * 根据某个数值递增 (默认十秒超时时间)
	 * @param key
	 * @param value
	 * @return
	 */
	public long  incrby(String key,int value){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			//boolean exist = jedis.exists(key);
			long count = jedis.incrBy(key, value);
			/*if(!exist){
				// 默认存储10秒
				jedis.expire(key, 10);
			}*/
			return count;
		}
		catch (Exception e) {
			LOGGER.error("ERROR-> incrby(key:{}) {}",key,e);
			throw e;
		}
	}
    
    /**
     * 递减
     * @param key
     * @return
     */
	public long decr(String key) {
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
			Long val = this.get(key,Long.class);
			if (val > 0) {
				return jedis.decr(key);
			}
			return 0;
	    }catch (Exception e) {
	    	LOGGER.error("decr failed error: {}",e);
	    	throw e;
		}
		
		
		
	}
	/**
	 * redis 分布式锁 解决并发问题
	 * 当key 存在 返回 0  当key 不存在 返回1
	 * 增加 5 秒超时时间
	 */
	public long setnx(String value){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
            Long val = jedis.setnx(value, value);
            if (val == 1){
                jedis.expire(value,5);
            }
			return val;
	    }catch (Exception e) {
	    	LOGGER.error("setnx failed error: {}",e);
	    	throw e;
		}
	} 
	/**
	  * 判断key是否存在
	  * @return boolean
	  * */
	 public boolean exists(String key){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		 try(Jedis jedis = this.getJedisPlloResource()){
		 		boolean exis=false;
			exis = jedis.exists(key);
			return exis;
	    }catch (Exception e) {
	    	LOGGER.error("ERROR-> RedisDB.exists(key:{}) {}",key,e);
	    	throw e;
		}
	 }
	 
	 

	/**
	 * redis 分布式锁 解决并发问题
	 * 当key 存在 返回 0  当key 不存在 返回1
	 * 增加 5 秒超时时间
	 */
	public long setnx(String value,int second){
		//try(ShardedJedis jedis = this.getJedisPlloResource()) {
		try(Jedis jedis = this.getJedisPlloResource()){
            Long val = jedis.setnx(value, value);
            if (val == 1){
                jedis.expire(value,second);
            }
			return val;
		}catch (Exception e) {
			LOGGER.error("setnx failed error: {}",e);
			throw e;
		}
	}

	/**
	 *  set 添加
	 * @param key
	 * @param members
	 * @return
	 */
	public long sadd(String key, String... members){
		try(Jedis jedis = this.getJedisPlloResource()){
			Long val = jedis.sadd(key, members);
			return val;
		}catch (Exception e) {
			LOGGER.error("setnx failed error: {}",e);
			throw e;
		}
	}

	/**
	 * 判断是否是集合成员
	 * @param key
	 * @param member
	 * @return
	 */
	public Boolean sismember(String key, String member) {
		try(Jedis jedis = this.getJedisPlloResource()){
			Boolean val = jedis.sismember(key, member);
			return val;
		}catch (Exception e) {
			LOGGER.error("setnx failed error: {}",e);
			throw e;
		}
	}

	//存储list集合数据
	public void lpush(String key,String value,int seconds){
		try(Jedis jedis = this.getJedisPlloResource()){
			jedis.lpush(key,value);
			if (seconds>0){
				jedis.expire(key, seconds);
			}
		}catch (Exception e) {
			LOGGER.error("lpush failed error: {}",e);
			throw e;
		}
	}
	//取list集合数据
	public List<String> brpop(String key){
		try(Jedis jedis = this.getJedisPlloResource()){
			List<String>list=jedis.lrange(key,0,-1);
			Collections.reverse(list);
			return list;
		}catch (Exception e) {
			LOGGER.error("brpop failed error: {}",e);
			throw e;
		}
	}

	public String lrpop(String key){
		try(Jedis jedis = this.getJedisPlloResource()){
			String str = jedis.lpop(key);
			return str;
		}catch (Exception e) {
			LOGGER.error("brpop failed error: {}",e);
			throw e;
		}
	}
	//存储list集合数据 rpush
	public void rpush(String key,String value,int seconds){
		try(Jedis jedis = this.getJedisPlloResource()){
			jedis.rpush(key,value);
			if (seconds>0){
				jedis.expire(key, seconds);
			}
		}catch (Exception e) {
			LOGGER.error("lpush failed error: {}",e);
			throw e;
		}
	}
	public  void scanDel(String keys){
		try(Jedis jedis = this.getJedisPlloResource()){
			/*ScanParams scanParams = new ScanParams();
			scanParams.match(keys);
			scanParams.count(1000);
			ScanResult<String> result = jedis.scan(0,scanParams);
			result.getResult().forEach(key -> {
				jedis.del(key);
			});*/
			Set<String> key = jedis.keys(keys);
			if (null != keys && keys.length() > 0){
				key.stream().forEach(s -> {
					jedis.del(s);
				});
			}
		}catch (Exception e){
			LOGGER.error("scanDel failed error: {}",e);
			throw e;
		}

	}

	/**
	 * 根据某个数值递增
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hincrby(String key, String field, int value) {
		try (Jedis jedis = this.getJedisPlloResource()) {
			return jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			LOGGER.error("ERROR-> RedisDB.hincrby(key:{},field:{}) {}", key, field, e);
			throw e;
		}
	}




}
