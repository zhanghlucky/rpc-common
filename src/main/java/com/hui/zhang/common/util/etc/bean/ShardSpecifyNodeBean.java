package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;
import com.hui.zhang.common.datasource.mongo.annotation.FieldMeta;

import java.util.List;

/**
 * Created by zhanghui on 2018/1/16.
 */
@CollName("shard_specify_node")
public class ShardSpecifyNodeBean {
    private String _id;
    private String writeDsName;
    private String readDsName;
    private String shardId;

    @FieldMeta(save = false)
    private List<ShardSpecifyNodeIdBean> shardSpecifyNodeIdBeanList;

    private String creater;
    private long createTime;
    private String updater;
    private long updateTime;
    private String remark;
    private boolean isDelete;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getWriteDsName() {
        return writeDsName;
    }

    public void setWriteDsName(String writeDsName) {
        this.writeDsName = writeDsName;
    }

    public String getReadDsName() {
        return readDsName;
    }

    public void setReadDsName(String readDsName) {
        this.readDsName = readDsName;
    }

    public String getShardId() {
        return shardId;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    public List<ShardSpecifyNodeIdBean> getShardSpecifyNodeIdBeanList() {
        return shardSpecifyNodeIdBeanList;
    }

    public void setShardSpecifyNodeIdBeanList(List<ShardSpecifyNodeIdBean> shardSpecifyNodeIdBeanList) {
        this.shardSpecifyNodeIdBeanList = shardSpecifyNodeIdBeanList;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
