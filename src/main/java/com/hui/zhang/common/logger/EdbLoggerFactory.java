package com.hui.zhang.common.logger;

/**
 * Created by zhanghui on 2019-06-04.
 */
public class EdbLoggerFactory {
    //private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();
    public static EdbLogger getLogger(Class<?> clazz){
        EdbLogger cLogger=new EdbLogger(clazz);
        return cLogger;
    }
}
