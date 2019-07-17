package com.hui.zhang.common.task.data;

import com.hui.zhang.common.datasource.mongo.annotation.CollName;

import java.io.Serializable;

public class TaskParamBean implements Serializable {
    private String taskId;  //任务ID
    private String paramKey; // 参数key
    private String paramValue; // 参数 value

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
