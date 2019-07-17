package com.hui.zhang.common.util.etc.vo;


import com.hui.zhang.common.util.etc.po.CfgEsShardNodePO;
import com.hui.zhang.common.util.etc.po.CfgEsShardPO;

import java.util.List;

public class CfgEsShardVO extends CfgEsShardPO {
    private String esShardId;

    private String envId;

    private String dsCode;

    private String dsName;

    private  List<CfgEsShardNodePO> cfgEsShardNodePOList;

    private  List<CfgEsShardSpecifyNodeVO> cfgEsShardSpecifyNodeVOList;

    @Override
    public String getEsShardId() {
        return esShardId;
    }

    @Override
    public void setEsShardId(String esShardId) {
        this.esShardId = esShardId;
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

    public List<CfgEsShardNodePO> getCfgEsShardNodePOList() {
        return cfgEsShardNodePOList;
    }

    public void setCfgEsShardNodePOList(List<CfgEsShardNodePO> cfgEsShardNodePOList) {
        this.cfgEsShardNodePOList = cfgEsShardNodePOList;
    }

    public List<CfgEsShardSpecifyNodeVO> getCfgEsShardSpecifyNodeVOList() {
        return cfgEsShardSpecifyNodeVOList;
    }

    public void setCfgEsShardSpecifyNodeVOList(List<CfgEsShardSpecifyNodeVO> cfgEsShardSpecifyNodeVOList) {
        this.cfgEsShardSpecifyNodeVOList = cfgEsShardSpecifyNodeVOList;
    }
}