package com.hui.zhang.common.util.lock;

/**
 * Created by zhanghui on 2019-06-28.
 */
import com.hui.zhang.common.datasource.redis.db.RedisDB;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.po.CfgRedisPO;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RedissonDistributedLocker implements DistributedLocker {
    private static final Logger logger = LoggerFactory.getLogger(RedissonDistributedLocker.class);

    private RedissonClient redissonClient;

    public RedissonDistributedLocker(){
        if (null==redissonClient){
            String redisCode=AppConfigUtil.getCfgEnvironmentPO().getShareCacheDs();
            CfgRedisPO cfgRedisPO= AppConfigUtil.getCfgRedisPO(redisCode);

            boolean sentinelFlag=false;
            String redisAddress=cfgRedisPO.getDsHost();
            if (redisAddress.contains(":")){//哨兵
                sentinelFlag=true;
            }else {
                redisAddress=redisAddress+":"+cfgRedisPO.getDsPort();
                if(!redisAddress.startsWith("redis")){
                    redisAddress="redis://"+redisAddress;
                }
            }




            Config config = new Config();
            if (sentinelFlag){//哨兵模式
                logger.info("redisson sentinel");
                //哨兵模式
                SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redisAddress)
                        .setMasterName("mymaster")
                        .setTimeout(10000)
                        .setMasterConnectionPoolSize(cfgRedisPO.getMaxIdle())
                        .setSlaveConnectionPoolSize(cfgRedisPO.getMaxIdle());

                if(StringUtils.isNotBlank(cfgRedisPO.getPassword())) {
                    serverConfig.setPassword(cfgRedisPO.getPassword());
                }
            }else {
                logger.info("redisson single");
                //单例模式
                SingleServerConfig serverConfig = config.useSingleServer()
                        .setAddress(redisAddress)
                        .setTimeout(10000)
                        .setConnectionPoolSize(cfgRedisPO.getMaxIdle())
                        .setConnectionMinimumIdleSize(cfgRedisPO.getMinIdle());
                if(StringUtils.isNotBlank(cfgRedisPO.getPassword())) {
                    serverConfig.setPassword(cfgRedisPO.getPassword());
                }
            }
            redissonClient= Redisson.create(config);
        }
    }


    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String lockKey, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit ,int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
}
