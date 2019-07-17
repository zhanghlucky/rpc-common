package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;

/**
 * Created by zhanghui on 2018/1/16.
 */
@CollName("shard_specify_id")
public class ShardSpecifyNodeIdBean {
    private String _id;
    private String shardSpecifyNodeId;
    private Long dataId;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getShardSpecifyNodeId() {
        return shardSpecifyNodeId;
    }

    public void setShardSpecifyNodeId(String shardSpecifyNodeId) {
        this.shardSpecifyNodeId = shardSpecifyNodeId;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }
}
