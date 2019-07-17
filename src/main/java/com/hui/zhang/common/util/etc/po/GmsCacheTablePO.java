package com.hui.zhang.common.util.etc.po;

public class GmsCacheTablePO {
    private String cacheTableId;

    private String cacheDsId;

    private String cacheShardId;

    private String tableName;

    private String javaBeanName;

    private String tableKeys;

    private Integer dataCount;

    public String getCacheTableId() {
        return cacheTableId;
    }

    public void setCacheTableId(String cacheTableId) {
        this.cacheTableId = cacheTableId;
    }

    public String getCacheDsId() {
        return cacheDsId;
    }

    public void setCacheDsId(String cacheDsId) {
        this.cacheDsId = cacheDsId;
    }

    public String getCacheShardId() {
        return cacheShardId;
    }

    public void setCacheShardId(String cacheShardId) {
        this.cacheShardId = cacheShardId;
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

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }
}