package com.hui.zhang.common.task.data;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2017-11-10 13:53
 **/
public class Result implements Serializable {
    private static final long serialVersionUID = 1554L;
    private String requestId;
    public boolean Success;
    public String ErrorInfo;

    public Result(boolean success, String errorInfo) {
        Success = success;
        ErrorInfo = errorInfo;
    }

    public Result() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        ErrorInfo = errorInfo;
    }
}
