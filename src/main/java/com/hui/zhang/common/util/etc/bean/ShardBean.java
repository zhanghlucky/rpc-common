package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;
import com.hui.zhang.common.datasource.mongo.annotation.FieldMeta;

import java.util.List;

/**
 * Created by zhanghui on 2017/10/10.
 */
@CollName("shard")
public class ShardBean {
    private String _id;
    private String dsName;

    @FieldMeta(save = false)
    private List<ShardNodeBean> shardNodeBeanList;

    @FieldMeta(save = false)
    private List<ShardSpecifyNodeBean> shardSpecifyNodeBeanList;

    private String creater;
    private long createTime;
    private String updater;
    private long updateTime;
    private String remark;
    private boolean isDelete;

    public List<ShardNodeBean> getShardNodeBeanList() {
        return shardNodeBeanList;
    }

    public void setShardNodeBeanList(List<ShardNodeBean> shardNodeBeanList) {
        this.shardNodeBeanList = shardNodeBeanList;
    }

    public List<ShardSpecifyNodeBean> getShardSpecifyNodeBeanList() {
        return shardSpecifyNodeBeanList;
    }

    public void setShardSpecifyNodeBeanList(List<ShardSpecifyNodeBean> shardSpecifyNodeBeanList) {
        this.shardSpecifyNodeBeanList = shardSpecifyNodeBeanList;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
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
