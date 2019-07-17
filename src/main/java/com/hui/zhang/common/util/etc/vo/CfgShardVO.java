package com.hui.zhang.common.util.etc.vo;


import com.hui.zhang.common.util.etc.po.CfgShardNodePO;
import com.hui.zhang.common.util.etc.po.CfgShardPO;

import java.util.List;

public class CfgShardVO  extends CfgShardPO {
    private String shardId;

    private String envId;

    private String dsCode;

    private String dsName;

    private  List<CfgShardNodePO> cfgShardNodePOList;

    private  List<CfgShardSpecifyNodeVO> cfgShardSpecifyNodeVOList;

    @Override
    public String getShardId() {
        return shardId;
    }

    @Override
    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    @Override
    public String getEnvId() {
        return envId;
    }

    @Override
    public void setEnvId(String envId) {
        this.envId = envId;
    }

    @Override
    public String getDsCode() {
        return dsCode;
    }

    @Override
    public void setDsCode(String dsCode) {
        this.dsCode = dsCode;
    }

    @Override
    public String getDsName() {
        return dsName;
    }

    @Override
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public List<CfgShardSpecifyNodeVO> getCfgShardSpecifyNodeVOList() {
        return cfgShardSpecifyNodeVOList;
    }

    public void setCfgShardSpecifyNodeVOList(List<CfgShardSpecifyNodeVO> cfgShardSpecifyNodeVOList) {
        this.cfgShardSpecifyNodeVOList = cfgShardSpecifyNodeVOList;
    }

    public List<CfgShardNodePO> getCfgShardNodePOList() {
        return cfgShardNodePOList;
    }

    public void setCfgShardNodePOList(List<CfgShardNodePO> cfgShardNodePOList) {
        this.cfgShardNodePOList = cfgShardNodePOList;
    }
}