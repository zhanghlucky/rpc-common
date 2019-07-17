package com.hui.zhang.common.task.service;

import com.hui.zhang.common.task.data.ExecuteParam;
import com.hui.zhang.common.task.data.Result;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2017-11-09 14:31
 **/
public interface TaskService {
    Result execute(ExecuteParam param);
}
