package com.hui.zhang.common.datasource.mybatis.db;



import com.hui.zhang.common.datasource.mybatis.db.ds.DynamicDataSource;
import com.hui.zhang.common.datasource.mybatis.db.ds.MdataSource;
import com.hui.zhang.common.datasource.mybatis.spring.MyBatisConfig;
import com.mongodb.MongoClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MybatisDB {
    private static final Logger logger = LoggerFactory.getLogger(MybatisDB.class);

    private String shardName;
    private String dsWriteName;
    private String dsReadName;

    private static final Map<String, MdataSource> clients = new ConcurrentHashMap<>();

    public MybatisDB(String shardName,String dsWriteName,String dsReadName){
        this.shardName=shardName;
        this.dsWriteName=dsWriteName;
        this.dsReadName=dsReadName;
    }

    /**
     * 获取数据源
     * @param dsName
     * @return
     */
    public synchronized MdataSource ds(String dsName){
        DynamicDataSource.setDataSourceKey(dsName);
        MdataSource dataSource=clients.get(dsName);
        if (null==dataSource){
            dataSource=new MdataSource(dsName);
            clients.put(dsName,dataSource);
        }
        return dataSource;
    }

    /**
     * 获取写数据源
     * @return
     */
    public synchronized MdataSource dsWrite(){
        return this.ds(dsWriteName);
    }

    /**
     * 获取读数据源
     * @return
     */
    public synchronized MdataSource dsRead(){
        return this.ds(dsReadName);
    }

   /* public synchronized MdataSource mycat(){
        return new MdataSource(null);
    }*/

    /**
     * 获取shard写数据源
     * @param key
     * @return
     */
    public synchronized MdataSource shardWrite(long key){
        return  this.shardDs(shardName,key,MyBatisConfig.DS_TYPE_WRITE);
    }

    /**
     * 获取shard读数据源
     * @param key
     * @return
     */
    public synchronized MdataSource shardRead(long key){
        return  this.shardDs(shardName,key,MyBatisConfig.DS_TYPE_READ);
    }

    private MdataSource shardDs(String shardName,long key,String dsType){
        String  dsKey=null;
        List<ShardDsNode> ShardDsNodes= MyBatisConfig.shardDsNodes;
        for (ShardDsNode shardDsNode:ShardDsNodes) {
            if (shardName.equals(shardDsNode.getShardName())){
                //先判断指定id数据源
                List<Long> idList=shardDsNode.getIdList();
                if (null!=idList){
                    for (Long id: idList) {
                        if (id==key){
                            dsKey=shardDsNode.getDsKey();
                        }
                    }
                }
                //后判断区间id数据源
                if(key>=shardDsNode.getStart()&&
                        key<shardDsNode.getEnd()&&
                        shardDsNode.getType().equals(dsType)){
                    dsKey=shardDsNode.getDsKey();
                }
            }
        }
        if (StringUtils.isNotEmpty(dsKey)){
            return this.ds(dsKey);
        }
        logger.error("shard:{},key:{},dsType:{}找不到配置节点，请检查配置管理中心配置！",shardName,key,dsType);
        return null;
    }

   /* private MdataSource shardWrite(String shardName,long key){
        String  dsKey=null;
        List<ShardDsNode> ShardDsNodes= MyBatisConfig.shardDsNodes;
        for (ShardDsNode shardDsNode:ShardDsNodes) {
            if (shardName.equals(shardDsNode.getShardName())){
                //先判断指定id数据源
                List<Long> idList=shardDsNode.getIdList();
                if (null!=idList){
                    for (Long id: idList) {
                        if (id==key){
                            dsKey=shardDsNode.getDsKey();
                            //DynamicDataSource.setDataSourceKey(dsKey);
                            //return new MdataSource(dsKey);
                        }
                    }
                }
                //后判断区间id数据源
                if(key>=shardDsNode.getStart()&&
                        key<shardDsNode.getEnd()&&
                        shardDsNode.getType().equals(MyBatisConfig.DS_TYPE_WRITE)){
                    dsKey=shardDsNode.getDsKey();
                    ///DynamicDataSource.setDataSourceKey(dsKey);
                    //return new MdataSource(dsKey);
                }
            }
        }
        if (StringUtils.isNotEmpty(dsKey)){
            return this.ds(dsKey);
        }
        logger.error("shard:{},key:{}找不到配置节点，请检查配置管理中心配置！",shardName,key);
        return null;

    }

    private MdataSource shardRead(String shardName,long key){
        String  dsKey=null;
        List<ShardDsNode> shardDsNodes= MyBatisConfig.shardDsNodes;
        for (ShardDsNode shardDsNode:shardDsNodes) {
            if (shardName.equals(shardDsNode.getShardName())){
                //先判断指定id数据源
                List<Long> idList=shardDsNode.getIdList();
                if (null!=idList){
                    for (Long id: idList) {
                        if (id==key){
                            dsKey=shardDsNode.getDsKey();
                            //DynamicDataSource.setDataSourceKey(dsKey);
                            //return new MdataSource(dsKey);
                        }
                    }
                }
                //后判断区间id数据源
                if(key>=shardDsNode.getStart()&&
                        key<shardDsNode.getEnd()&&
                        shardDsNode.getType().equals(MyBatisConfig.DS_TYPE_READ)){
                    dsKey=shardDsNode.getDsKey();
                    //DynamicDataSource.setDataSourceKey(dsKey);
                    //return new MdataSource(dsKey);
                }
            }
        }
        if (StringUtils.isNotEmpty(dsKey)){
            return this.ds(dsKey);
        }
        logger.error("shard:{},key:{}找不到配置节点，请检查配置管理中心配置！",shardName,key);
        return null;
    }*/

}
