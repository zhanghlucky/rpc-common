package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;

/**
 * Created by zhanghui on 2018/1/30.
 */
@CollName("cache_table")
public class CacheTableBean {
    private String cacheShardId;
    private String cacheTableId;
    private String tableName;
    private String javaBeanName;
    private String tableKeys;

    public String getCacheShardId() {
        return cacheShardId;
    }

    public void setCacheShardId(String cacheShardId) {
        this.cacheShardId = cacheShardId;
    }

    public String getCacheTableId() {
        return cacheTableId;
    }

    public void setCacheTableId(String cacheTableId) {
        this.cacheTableId = cacheTableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getJavaBeanName() {
        return javaBeanName;
    }

    public void setJavaBeanName(String javaBeanName) {
        this.javaBeanName = javaBeanName;
    }

    public String getTableKeys() {
        return tableKeys;
    }

    public void setTableKeys(String tableKeys) {
        this.tableKeys = tableKeys;
    }
}
