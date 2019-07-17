package com.hui.zhang.common.datasource.es.db;

import com.hui.zhang.common.datasource.es.ds.EsClient;
import com.hui.zhang.common.util.JsonEncoder;
import com.hui.zhang.common.util.etc.AppConfigUtil;
import com.hui.zhang.common.util.etc.po.CfgEsShardNodePO;
import com.hui.zhang.common.util.etc.po.CfgEsShardSpecifyNodeItemPO;
import com.hui.zhang.common.util.etc.vo.CfgEsShardSpecifyNodeVO;
import com.hui.zhang.common.util.etc.vo.CfgEsShardVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhanghui on 2019-04-17.
 */
public class EsDB {
    private static final Logger logger = LoggerFactory.getLogger(EsDB.class);
    private static final Map<String, EsClient> CLIENT_MAP = new ConcurrentHashMap<>();
    private String esDsCode;
    private String esShardDsCode;
    private String envId;

    public EsDB(String esDsCode,String esShardDsCode){
        this.esDsCode=esDsCode;
        this.esShardDsCode=esShardDsCode;
    }

    public EsDB(String esDsCode,String esShardDsCode,String envId){
        this.esDsCode=esDsCode;
        this.esShardDsCode=esShardDsCode;
        this.envId=envId;
    }


    public synchronized EsClient client(){
        String mapkey=this.esDsCode;
        if (StringUtils.isNotEmpty(envId)){
            mapkey=this.esDsCode+envId;
        }
        EsClient client=CLIENT_MAP.get(mapkey);
        if (null!=client){
            return client;
        }
        client=this.client(this.esDsCode);
        //System.out.println("**********"+ JsonEncoder.DEFAULT.encode(client));
        return client;
    }

    private synchronized EsClient client(String esName){
        String mapkey=this.esDsCode;
        if (StringUtils.isNotEmpty(envId)){
            mapkey=this.esDsCode+envId;
        }

        EsClient client=CLIENT_MAP.get(mapkey);
        if (null!=client){
            return client;
        }
        if (StringUtils.isNotEmpty(envId)){
            client=new EsClient(esName,envId);
        }else{
            client=new EsClient(esName);
        }
        CLIENT_MAP.put(mapkey,client);
        return client;
    }

    public synchronized EsClient esShradClent(long key){
        String dsCode=null;
        //后续优化
        CfgEsShardVO cfgEsShardVO=AppConfigUtil.getCfgEsShardVO(esShardDsCode);
        List<CfgEsShardNodePO>  cfgEsShardNodePOList=cfgEsShardVO.getCfgEsShardNodePOList();
        List<CfgEsShardSpecifyNodeVO> cfgEsShardSpecifyNodeVOList=cfgEsShardVO.getCfgEsShardSpecifyNodeVOList();
        for (CfgEsShardSpecifyNodeVO cfgEsShardSpecifyNodeVO : cfgEsShardSpecifyNodeVOList) {
            List<CfgEsShardSpecifyNodeItemPO> cfgEsShardSpecifyNodeItemPOList=cfgEsShardSpecifyNodeVO.getCfgEsShardSpecifyNodeItemPOList();
            for (CfgEsShardSpecifyNodeItemPO cfgEsShardSpecifyNodeItemPO: cfgEsShardSpecifyNodeItemPOList) {
                if (key==cfgEsShardSpecifyNodeItemPO.getDataId()){
                    dsCode=cfgEsShardSpecifyNodeVO.getDsCode();
                    break;
                }
            }
        }
        if (null==dsCode){
            for (CfgEsShardNodePO cfgEsShardNodePO:cfgEsShardNodePOList) {
                if(key>=cfgEsShardNodePO.getStart()&&key<cfgEsShardNodePO.getEnd()){
                    dsCode=cfgEsShardNodePO.getDsCode();
                    break;
                }
            }
        }
        //后续优化

        if (null!=dsCode){
            return this.client(dsCode);
        }
        logger.error("无法匹配es-shard");
        return null;
    }


}
