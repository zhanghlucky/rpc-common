package com.hui.zhang.common.task.data;

import com.hui.zhang.common.datasource.mongo.annotation.FieldMeta;

import java.io.Serializable;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2017-11-08 14:16
 **/
public class ExecuteParam implements Serializable {
    private static final long serialVersionUID = 1554L;
    private String taskId;
    private String appId;
    private String taskCode;
    private String taskName;
    private String taskNote;
    private String executer;
    private String cron;
    private Integer taskStatus;
    private List<TaskParamBean> taskParamBeans;

    public ExecuteParam() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
    }

    public String getExecuter() {
        return executer;
    }

    public void setExecuter(String executer) {
        this.executer = executer;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public List<TaskParamBean> getTaskParamBeans() {
        return taskParamBeans;
    }

    public void setTaskParamBeans(List<TaskParamBean> taskParamBeans) {
        this.taskParamBeans = taskParamBeans;
    }

}
