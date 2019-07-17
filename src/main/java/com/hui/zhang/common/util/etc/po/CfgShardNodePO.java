package com.hui.zhang.common.util.etc.po;

public class CfgShardNodePO {
    private String nodeId;

    private String shardId;

    private String writeDsCode;

    private String writeDsId;

    private String readDsCode;

    private String readDsId;

    private Long start;

    private Long end;

    private Long createTime;

    private String creater;

    private Long updateTime;

    private String updater;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getShardId() {
        return shardId;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    public String getWriteDsCode() {
        return writeDsCode;
    }

    public void setWriteDsCode(String writeDsCode) {
        this.writeDsCode = writeDsCode;
    }

    public String getWriteDsId() {
        return writeDsId;
    }

    public void setWriteDsId(String writeDsId) {
        this.writeDsId = writeDsId;
    }

    public String getReadDsCode() {
        return readDsCode;
    }

    public void setReadDsCode(String readDsCode) {
        this.readDsCode = readDsCode;
    }

    public String getReadDsId() {
        return readDsId;
    }

    public void setReadDsId(String readDsId) {
        this.readDsId = readDsId;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }
}