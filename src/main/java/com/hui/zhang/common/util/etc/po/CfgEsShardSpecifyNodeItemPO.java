package com.hui.zhang.common.util.etc.po;

public class CfgEsShardSpecifyNodeItemPO {
    private String esNodeItemId;

    private String esSpecifyNodeId;

    private Long dataId;

    private Integer pri;

    private Long createTime;

    private String creater;

    public String getEsNodeItemId() {
        return esNodeItemId;
    }

    public void setEsNodeItemId(String esNodeItemId) {
        this.esNodeItemId = esNodeItemId;
    }

    public String getEsSpecifyNodeId() {
        return esSpecifyNodeId;
    }

    public void setEsSpecifyNodeId(String esSpecifyNodeId) {
        this.esSpecifyNodeId = esSpecifyNodeId;
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