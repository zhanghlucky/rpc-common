package com.hui.zhang.common.task;

import com.hui.zhang.common.task.contsant.TaskConstant;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-09-04 14:35
 **/
public class TaskInfo implements Serializable {
    private String appName;
    private String taskName;
    private String address;
    private String registryName;

    public TaskInfo(String appName, String taskName, String address) {
        this.appName = appName;
        this.taskName = taskName;
        this.address = address;
    }

    public String getRegistryName() {
        return this.appName + TaskConstant.Name_SEPARATOR + this.taskName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
