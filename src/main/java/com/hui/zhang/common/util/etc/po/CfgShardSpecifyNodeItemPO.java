package com.hui.zhang.common.util.etc.po;

public class CfgShardSpecifyNodeItemPO {
    private String nodeItemId;

    private String specifyNodeId;

    private Long dataId;

    private Integer pri;

    private Long createTime;

    private String creater;

    public String getNodeItemId() {
        return nodeItemId;
    }

    public void setNodeItemId(String nodeItemId) {
        this.nodeItemId = nodeItemId;
    }

    public String getSpecifyNodeId() {
        return specifyNodeId;
    }

    public void setSpecifyNodeId(String specifyNodeId) {
        this.specifyNodeId = specifyNodeId;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Integer getPri() {
        return pri;
    }

    public void setPri(Integer pri) {
        this.pri = pri;
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
}