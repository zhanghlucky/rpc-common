package com.hui.zhang.common.rocketmq.mongo;

import com.hui.zhang.common.datasource.mongo.util.MgoUtil;
import com.hui.zhang.common.util.etc.BaseEtc;
import com.hui.zhang.common.util.etc.bean.AppParamBean;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.stereotype.Service;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-04-24 10:00
 **/
@Service
public class MessageBiz  extends  BaseEtc{

    /*public void saveMessage(MessageBean bean){
        MongoCollection<Document> collection= getCollection(MessageBean.class);
        collection.insertOne(MgoUtil.toDocument(bean));
    }
*/
}
