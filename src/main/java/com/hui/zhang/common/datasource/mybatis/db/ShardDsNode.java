package com.hui.zhang.common.datasource.mybatis.db;

import com.hui.zhang.common.util.MD5Util;

import java.util.List;

public class  ShardDsNode{
    private String shardName;
    private long start;
    private long end;
    private String type;
    private String dsKey;
    private List<Long> idList;

    public ShardDsNode(String shardName,long start,long end,String type){
        this.shardName=shardName;
        this.start=start;
        this.end=end;
        this.type=type;
        this.dsKey=shardName+"_"+start+"_"+end+"_"+type;
    }

    public ShardDsNode(String shardName,List<Long> idList,String type){
        this.shardName=shardName;
        this.idList=idList;
        this.type=type;
        String ids="";
        for (Long id: idList) {
            ids+=id;
        }
        String key= MD5Util.MD5(ids);

        this.dsKey=shardName+"_"+key+"_"+type;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public String getShardName() {
        return shardName;
    }

    public void setShardName(String shardName) {
        this.shardName = shardName;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDsKey() {
        return dsKey;
    }

    public void setDsKey(String dsKey) {
        this.dsKey = dsKey;
    }
}
