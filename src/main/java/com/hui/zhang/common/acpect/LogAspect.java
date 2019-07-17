package com.hui.zhang.common.acpect;

import com.alibaba.fastjson.JSON;
import com.hui.zhang.common.logger.EdbLogger;
import com.hui.zhang.common.logger.EdbLoggerFactory;
import com.hui.zhang.common.util.JsonEncoder;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * by zhanghui
 */
@Aspect
@Component
public class LogAspect {
    //private  static  final EdbLogger logger = EdbLoggerFactory.getLogger(LogAspect.class);

    private  static  final LocalVariableTableParameterNameDiscoverer lvtp =new LocalVariableTableParameterNameDiscoverer();

    @Pointcut("execution(* com.edb01..*.service..*.*(..))")
    private void serviceMethod(){}
    @Pointcut("execution(* com.edb01..*.biz..*.*(..))")
    private void bizMethod(){}
    @Pointcut("execution(* com.edb01..*.controller.*.*(..))")
    private void controllerMethod(){}

    @Around("serviceMethod()|| bizMethod() ||controllerMethod() ")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        Class<?> beanClass=this.getBeanClass(point);
        EdbLogger logger = EdbLoggerFactory.getLogger(beanClass);

        String serverType="S";
        if (isDubboServer(point)){
            serverType="D";
        }
        String classMethod=getClassMethod(point);
        String requestStr = parameterHandle(requestFormat(point.getArgs()),512);
        logger.info(">>"+serverType+"["+classMethod + "] params:" + requestStr);

        long startTime = System.currentTimeMillis();// 开始时间
        Object result = null;
        try{
            result = point.proceed();
        }catch(Exception e){
            throw e;
        }finally{
            long handleTime = System.currentTimeMillis()-startTime;// 开始时间
            String responseStr=parameterHandle(responseFormat(result), 512);
            logger.info("<<"+serverType+"["+classMethod +"] use_time:"+handleTime +"ms response:"+responseStr+" params:"+requestStr);
        }
        return result;
    }

    private String parameterHandle(String paramStr, int strlength){
        if (paramStr.length() > strlength){
            paramStr = paramStr.substring(0, strlength) + "...";
        }
        //paramStr = "[" + paramStr + "]";
        return paramStr;
    }

    private Class<?> getBeanClass(ProceedingJoinPoint point){
        Class<?> iCls=point.getTarget().getClass();
        Class[] interfaces=point.getTarget().getClass().getInterfaces();
        if (isDubboServer(point)){
            for (Class clss:interfaces) {
                if (!clss.getName().contains("com.alibaba.dubbo")){
                    iCls=clss;
                    break;
                }
            }
        }
        return iCls;
    }

    private boolean isDubboServer(ProceedingJoinPoint point){
        boolean flag=false;
        Class[] interfaces=point.getTarget().getClass().getInterfaces();
        if (interfaces.length>1){
            for (Class clss:interfaces) {
                if (clss.getName().contains("com.alibaba.dubbo")){
                    flag=true;
                    break;
                }
            }
        }
        return  flag;
    }



    private String getClassMethod(ProceedingJoinPoint point){
        Class<?> iCls=getBeanClass(point);
        String className=iCls.getName();
        String methodName=point.getSignature().getName();
        List<Class> paramsTypeClassList=new ArrayList<>();
        if (null!=point.getArgs()){
            for (Object arg:point.getArgs()) {
                if (null!=arg){
                    paramsTypeClassList.add(arg.getClass());
                }
            }
        }

        Method[] methodArray=iCls.getDeclaredMethods();
        String[] paramNames =null;
        Class[]  clsTypes= null;

        for (int k=0;k<methodArray.length;k++) {
            Method method = methodArray[k];
            //int modfilers = method.getModifiers();
            //boolean flag = Modifier.isPublic(modfilers);
            //System.out.println(method.getName()+" "+modfilers+" "+flag);

            paramNames = lvtp.getParameterNames(method);
            clsTypes= method.getParameterTypes();

            boolean fg=true;
            if (methodName.equals(method.getName())){
                for (int i = 0; i <paramsTypeClassList.size() ; i++) {
                    if(!paramsTypeClassList.get(i).getName().equals(clsTypes[i].getName())){
                        fg=false;
                        break;
                    }
                }
            }
            if (fg){
                Parameter[] pms=method.getParameters();
                if (null==paramNames){
                    paramNames=new String[pms.length];
                    for (int i = 0; i <pms.length ; i++) {
                        paramNames[i]=pms[i].getName();
                    }
                }
                break;
            }
        }

        String paramsStr="";
        if (null!=clsTypes){
            for (int i = 0; i <clsTypes.length ; i++) {
                String typeName=clsTypes[i].getSimpleName();
                String paramName=paramNames[i];
                paramsStr+=typeName+" "+paramName;
                if (i!=clsTypes.length-1){
                    paramsStr+=",";
                }
            }
        }

        String classMethod=className+"."+point.getSignature().getName()+"("+paramsStr+")";
        return classMethod;

    }

    private String requestFormat(Object [] params){
        String requestStr="";
        List<String> list=new ArrayList<>();
        for (int i = 0; i <params.length ; i++) {
            requestStr+=objToString(params[i]);
            if (i!=params.length-1){
                requestStr+=",";
            }
        }
        requestStr="["+requestStr+"]";
        return requestStr;

/*        try{
            requestStr=JSON.toJSONString(params);
        }catch (Exception e){
            for (int i = 0; i <params.length ; i++) {
                if (null!=params[i]){
                    requestStr+=params[i].toString();
                    if (i!=params.length-1){
                        requestStr+=",";
                    }
                }
            }
            requestStr="["+requestStr+"]";
        }
        return requestStr;*/
    }
    private String objToString(Object obj){
        if (null!=obj){
            String str=null;
            try{
                str=JSON.toJSONString(obj);
            }catch (Exception e){
                str=obj.toString();
            }
            return str;
        }
        return null;
    }

    private String responseFormat(Object result){
        String responseStr="";
        if (null!=result){
            if(result instanceof String){
                responseStr=result.toString();
            }else{
                try{
                    responseStr=JSON.toJSONString(result);
                }catch(Exception ee){
                    responseStr=result.toString();
                }
            }
        }
        return responseStr;
    }


    private boolean isPrimite(Class<?> clazz){
        if (clazz.isPrimitive() || clazz == String.class){
            return true;
        }else {
            return false;
        }
    }

}