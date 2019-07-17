package com.hui.zhang.common.datasource.mybatis.db.ds;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hui.zhang.common.datasource.mybatis.db.MybatisDB;
import com.hui.zhang.common.spring.SpringBeanUtil;
import com.esotericsoftware.reflectasm.MethodAccess;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-04-25 10:58
 **/
@Aspect
@Component
public class DataSourceAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceAdvice.class);
    public static Map<String,Class> clazzMap = new HashMap<String,Class>();
    public static Map<String,Object> objectMap = new HashMap<String,Object>();
    public static  String CLASS_PATH = StringUtils.EMPTY;
    @Pointcut("@annotation(com.hui.zhang.common.datasource.mybatis.db.ds.ShardKeyAnntation)")
    public void dynamicDataSourceAuto(){

    }
    @Before("dynamicDataSourceAuto()")
    public void doBefore(JoinPoint joinPoint) throws ClassNotFoundException, NotFoundException, IOException {
        LOGGER.info("进入切面操作，切换数据源");
        try {
            //1.获取 tenantId 执行分片   （1.方法体参数 ，2 实体参数， 3.JSON）
            //2.获取注解value
            //3.执行反射方法
            long shardId = 0;
            Object [] objects = joinPoint.getArgs();
            String classPath = StringUtils.EMPTY;
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            Parameter[] parameters = method.getParameters();
            for (int j = 0; j < parameters.length; j++) {
                Parameter parameter = parameters[j];
                if (objects.length == 0){
                    // 实体
                    if (parameter.getType() instanceof  Class){
                        Field[] fields = objects[0].getClass().getDeclaredFields();
                        for (Field field : fields) {
                            Annotation[] allFAnnos= field.getAnnotations();
                            if(allFAnnos.length > 0) {
                                for (int i = 0; i < allFAnnos.length; i++) {
                                    if(allFAnnos[i].annotationType().equals(ShardKeyAnntation.class)) {
                                        field.setAccessible(true);
                                        ShardKeyAnntation shardKeyAnntation = ShardKeyAnntation.class.cast(allFAnnos[i]);
                                        shardId = Long.valueOf(field.get("tenantId").toString());
                                        classPath = shardKeyAnntation.classpath();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //json
                    else{
                        JSONObject jsonObject = JSON.parseObject(objects[0].toString());
                        shardId = Long.valueOf(jsonObject.get("tenantId").toString());
                        Annotation[] annotations = parameter.getDeclaredAnnotationsByType(ShardKeyAnntation.class);
                        if (null != annotations && annotations.length > 0){
                            ShardKeyAnntation shardKeyAnntation = ShardKeyAnntation.class.cast(annotations[0]);
                            classPath = shardKeyAnntation.classpath();
                            break;
                        }
                    }
                }
                else{
                    //方法体参数
                    Annotation[] annotations = parameter.getDeclaredAnnotationsByType(ShardKeyAnntation.class);
                    if (null != annotations && annotations.length > 0){
                        shardId = Long.valueOf(objects[j].toString());
                        ShardKeyAnntation shardKeyAnntation = ShardKeyAnntation.class.cast(annotations[0]);
                        classPath = shardKeyAnntation.classpath();
                        break;
                    }
                }
            }


            invokeMethod(shardId,classPath/*,"com.edb01.erp.data.biz.impl.BaseBiz"*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private   void invokeMethod(Object arg,String className) throws  Exception{
        //本地缓存
        Class  cls = clazzMap.get(className);
        if (null == cls){
            cls= Class.forName(className);
            clazzMap.put(className,cls);
        }
        Object obj = objectMap.get(className);
        if (null == obj){
            obj=  cls.newInstance();
            objectMap.put(className,obj);
        }
        MethodAccess access = MethodAccess.get(obj.getClass());
        Object[] params= new Object[]{arg};
        access.invoke(obj, "setMdataSource", Long.valueOf(arg.toString()));//代理出实例
    }
    /**
     * 循环向上转型, 获     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */

    public  Method getDeclaredMethod(Object object, String methodName, Class<?> ... parameterTypes){
        Method method = null ;

        for(Class<?> clazz = object.getClass() ; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes) ;
                return method ;
            } catch (Exception e) {
                //这里甚么都不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会进入
            }
        }

        return null;
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     * @param object : 子类对象
     * @param methodName : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @param parameters : 父类中的方法参数
     * @return 父类中方法的执行结果
     */

    public  void invokeMethod(Object object, String methodName, Class<?> [] parameterTypes,
                                      Object [] parameters) {
        //根据 对象、方法名和对应的方法参数 通过取 Method 对象
        Method method = getDeclaredMethod(object, methodName, parameterTypes) ;

        //抑制Java对方法进行检查,主要是针对私有方法而言
        method.setAccessible(true) ;

        try {
            if(null != method) {
                //调用object 的 method 所代表的方法，其方法的参数是 parameters
                 method.invoke(object, parameters) ;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
    /**
     * 通过反射机制 获取被切参数名以及参数值
     *
     * @param cls
     * @param clazzName
     * @param methodName
     * @param args
     * @return
     * @throws NotFoundException
     */
    private Map<String, Object> getFieldsName(Class cls, String clazzName, String methodName, Object[] args) throws NotFoundException {
        Map<String, Object> map = new HashMap<String, Object>();

        ClassPool pool = ClassPool.getDefault();
        //ClassClassPath classPath = new ClassClassPath(this.getClass());
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            // exception
        }
        // String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < cm.getParameterTypes().length; i++) {
            map.put(attr.variableName(i + pos), args[i]);//paramNames即参数名
        }
        return map;
    }
    public static Map  listtoMap(List<Object> list) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        if (list != null) {
            try {
                for (int i = 0; i < list.size(); i++) {
                    Object value = list.get(i);
                    map.put(value, value);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("field can't match the key!");
            }
        }
        return map;
    }
    /** tian.luan
     * 使用javassist来获取方法参数名称
     * @param class_name    类名
     * @param method_name   方法名
     * @return
     * @throws Exception
     */
    private String[] getFieldsName(String class_name, String method_name) throws Exception {
        Class<?> clazz = Class.forName(class_name);
        String clazz_name = clazz.getName();
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(clazz);
        pool.insertClassPath(classPath);

        CtClass ctClass = pool.get(clazz_name);
        CtMethod ctMethod = ctClass.getDeclaredMethod(method_name);
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if(attr == null){
            return null;
        }
        String[] paramsArgsName = new String[ctMethod.getParameterTypes().length];
        int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
        for (int i=0;i<paramsArgsName.length;i++){
            paramsArgsName[i] = attr.variableName(i + pos);
        }
        return paramsArgsName;
    }


    /**
     * 判断是否为基本类型：包括String
     * @param clazz clazz
     * @return  true：是;     false：不是
     */
    private boolean isPrimite(Class<?> clazz){
        if (clazz.isPrimitive() || clazz == String.class){
            return true;
        }else {
            return false;
        }
    }


    /**
     * 打印方法参数值  基本类型直接打印，非基本类型需要重写toString方法
     * @param paramsArgsName    方法参数名数组
     * @param paramsArgsValue   方法参数值数组
     */
    private Map logParam(String[] paramsArgsName,Object[] paramsArgsValue){
        Map<Object, Object> map = new HashMap<Object, Object>();
        if(ArrayUtils.isEmpty(paramsArgsName) || ArrayUtils.isEmpty(paramsArgsValue)){
            LOGGER.info("该方法没有参数");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<paramsArgsName.length;i++){
            //参数名
            String name = paramsArgsName[i];
            //参数值
            Object value = paramsArgsValue[i];
            map.put(name,value);
        }
       return  map;
    }
}
