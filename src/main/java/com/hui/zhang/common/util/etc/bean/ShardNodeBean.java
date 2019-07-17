package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;

/**
 * Created by zhanghui on 2017/10/10.
 */
@CollName("shard_node")
public class ShardNodeBean {
    private String _id;
    private String writeDsName;
    private String readDsName;
    private String shardId;
    private long start;
    private long end;

    private String creater;
    private long createTime;
    private String updater;
    private long updateTime;
    private String remark;
    private boolean isDelete;

    public String getShardId() {
        return shardId;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
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
