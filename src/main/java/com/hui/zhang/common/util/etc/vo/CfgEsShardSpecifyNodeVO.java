package com.hui.zhang.common.util.etc.vo;


import com.hui.zhang.common.util.etc.po.CfgEsShardSpecifyNodeItemPO;
import com.hui.zhang.common.util.etc.po.CfgEsShardSpecifyNodePO;

import java.util.List;

public class CfgEsShardSpecifyNodeVO extends CfgEsShardSpecifyNodePO {

    private List<CfgEsShardSpecifyNodeItemPO> cfgEsShardSpecifyNodeItemPOList;

    public List<CfgEsShardSpecifyNodeItemPO> getCfgEsShardSpecifyNodeItemPOList() {
        return cfgEsShardSpecifyNodeItemPOList;
    }

    public void setCfgEsShardSpecifyNodeItemPOList(List<CfgEsShardSpecifyNodeItemPO> cfgEsShardSpecifyNodeItemPOList) {
        this.cfgEsShardSpecifyNodeItemPOList = cfgEsShardSpecifyNodeItemPOList;
    }
}