package com.hui.zhang.common.dubbo;

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
import com.hui.zhang.common.util.UUIDGenerator;
import com.hui.zhang.common.util.logger.CentaurLogger;
import com.hui.zhang.common.util.logger.CentaurLoggerFactory;
import org.apache.commons.lang3.StringUtils;

@Activate(group = { Constants.PROVIDER },order = 200)
public class DubboProviderContextFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        //X-B3-SpanId  X-B3-TraceId
        String X_B3_SpanId=RpcContext.getContext().getAttachment("X-B3-SpanId");//zipkin
        String X_B3_TraceId=RpcContext.getContext().getAttachment("X-B3-TraceId");
        //System.out.println("X_B3_TraceId:"+X_B3_TraceId);

        String traceId=RpcContext.getContext().getAttachment("traceId");
        if (StringUtils.isEmpty(traceId)){
            TraceTool.getInstance().setRandomTraceId(TraceTool.DEFAULT_REQUEST);
        }else{
            TraceTool.getInstance().setTraceId(traceId);
        }

        if (!StringUtils.isEmpty(X_B3_TraceId)){
            TraceTool.getInstance().setZipkinTraceId(X_B3_TraceId);
        }
        try {
            return invoker.invoke(invocation);
        }finally {

        }
    }

}
