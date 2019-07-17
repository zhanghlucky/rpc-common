package com.hui.zhang.common.task.data;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-09-18 14:28
 **/
public class TaskServiceMeta {
    private String appName;

    private String method;

    public TaskServiceMeta(String appName, String method) {
        this.appName = appName;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskServiceMeta that = (TaskServiceMeta) o;

        if (appName != null ? !appName.equals(that.appName) : that.appName != null) return false;
        return method != null ? method.equals(that.method) : that.method == null;
    }

    @Override
    public int hashCode() {
        int result = appName != null ? appName.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}
