package com.hui.zhang.common.task.data;



import com.hui.zhang.common.util.SettingMap;

import java.util.List;

/**
 *
 */
public class TaskContext {
    private String name;
    private String id;

    private SettingMap args = new SettingMap();

    public TaskContext(ExecuteParam param) {
        this.name = param.getTaskName();
        this.id = param.getTaskId();

        List<TaskParamBean> list = param.getTaskParamBeans();
        if (list != null && !list.isEmpty()) {
            list.forEach(arg -> this.args.put(arg.getParamKey(), arg.getParamValue()));
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public SettingMap getArgs() {
        return args;
    }

    public void setArgs(SettingMap args) {
        this.args = args;
    }
}
