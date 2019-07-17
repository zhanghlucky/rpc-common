package com.hui.zhang.common.dubbo;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.logger.EdbLogger;
import com.hui.zhang.common.logger.EdbLoggerFactory;
import com.hui.zhang.common.trace.TraceTool;
import com.hui.zhang.common.util.logger.CentaurLogger;
import com.hui.zhang.common.util.logger.CentaurLoggerFactory;
import org.apache.commons.lang3.StringUtils;

@Activate(group = { Constants.CONSUMER },order=200 )
public class DubboConsumerContextFilter implements Filter  {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //EdbLogger logger= EdbLoggerFactory.getLogger(invoker.getInterface());

        String X_B3_SpanId=invocation.getAttachment("X-B3-SpanId");//zipkin
        String X_B3_TraceId=invocation.getAttachment("X-B3-TraceId");
        if (StringUtils.isEmpty(X_B3_TraceId)){
        }else{
            TraceTool.getInstance().setZipkinTraceId(X_B3_TraceId);
        }
        RpcContext.getContext().setAttachment("traceId",TraceTool.getInstance().getTraceId());
        try {
            return invoker.invoke(invocation);
        }finally {

        }
    }
}
