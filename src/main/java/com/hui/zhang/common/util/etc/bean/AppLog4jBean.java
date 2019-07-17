package com.hui.zhang.common.util.etc.bean;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;

/**
 * Created by zhanghui on 2017/10/10.
 */
@CollName("app_log4j")
public class AppLog4jBean {
    private String _id;
    private String appName;
    private String properties;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
