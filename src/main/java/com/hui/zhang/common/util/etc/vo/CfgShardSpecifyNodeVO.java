package com.hui.zhang.common.util.etc.vo;


import com.hui.zhang.common.util.etc.po.CfgShardSpecifyNodeItemPO;
import com.hui.zhang.common.util.etc.po.CfgShardSpecifyNodePO;

import java.util.List;

public class CfgShardSpecifyNodeVO extends CfgShardSpecifyNodePO {
    private String writeDsCode;

    private String writeDsId;

    private String readDsCode;

    private String readDsId;

    private List<CfgShardSpecifyNodeItemPO> cfgShardSpecifyNodeItemPOList;

    @Override
    public String getWriteDsCode() {
        return writeDsCode;
    }

    @Override
    public void setWriteDsCode(String writeDsCode) {
        this.writeDsCode = writeDsCode;
    }

    @Override
    public String getWriteDsId() {
        return writeDsId;
    }

    @Override
    public void setWriteDsId(String writeDsId) {
        this.writeDsId = writeDsId;
    }

    @Override
    public String getReadDsCode() {
        return readDsCode;
    }

    @Override
    public void setReadDsCode(String readDsCode) {
        this.readDsCode = readDsCode;
    }

    @Override
    public String getReadDsId() {
        return readDsId;
    }

    @Override
    public void setReadDsId(String readDsId) {
        this.readDsId = readDsId;
    }

    public List<CfgShardSpecifyNodeItemPO> getCfgShardSpecifyNodeItemPOList() {
        return cfgShardSpecifyNodeItemPOList;
    }

    public void setCfgShardSpecifyNodeItemPOList(List<CfgShardSpecifyNodeItemPO> cfgShardSpecifyNodeItemPOList) {
        this.cfgShardSpecifyNodeItemPOList = cfgShardSpecifyNodeItemPOList;
    }
}