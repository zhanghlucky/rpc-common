package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;

/**
 * Created by zhanghui on 2017/10/12.
 */
@CollName("environment")
public class EnvironmentBean {

    private String _id;
    private String zookeeperAddress;
    private String uploadHost;
    private String fileServerHost;
    private String staticServerHost;
    private String environment;
    private String rocketmqServer;
    private String langs;
    private String shareCacheDs;
    private String internalEndpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String filesBucket;

    public String getInternalEndpoint() {
        return internalEndpoint;
    }

    public void setInternalEndpoint(String internalEndpoint) {
        this.internalEndpoint = internalEndpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getFilesBucket() {
        return filesBucket;
    }

    public void setFilesBucket(String filesBucket) {
        this.filesBucket = filesBucket;
    }

    public String getRocketmqServer() {
        return rocketmqServer;
    }

    public void setRocketmqServer(String rocketmqServer) {
        this.rocketmqServer = rocketmqServer;
    }

    private String creater;
    private long createTime;
    private String updater;
    private long updateTime;
    private String remark;
    private boolean isDelete;

    public String getShareCacheDs() {
        return shareCacheDs;
    }

    public void setShareCacheDs(String shareCacheDs) {
        this.shareCacheDs = shareCacheDs;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public String getUploadHost() {
        return uploadHost;
    }

    public void setUploadHost(String uploadHost) {
        this.uploadHost = uploadHost;
    }

    public String getFileServerHost() {
        return fileServerHost;
    }

    public void setFileServerHost(String fileServerHost) {
        this.fileServerHost = fileServerHost;
    }

    public String getStaticServerHost() {
        return staticServerHost;
    }

    public void setStaticServerHost(String staticServerHost) {
        this.staticServerHost = staticServerHost;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
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

    public String getLangs() {
        return langs;
    }

    public void setLangs(String langs) {
        this.langs = langs;
    }
}
