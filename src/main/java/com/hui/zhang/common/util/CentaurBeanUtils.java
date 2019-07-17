package com.hui.zhang.common.util;


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;


public class CentaurBeanUtils {
	
	/**
	 * 深度拷贝list里面bean的值
	 * @param <B>
	 * @param <T>
	 * @param sourceList
	 * @param targetList
	 * @param targetBeanCls
	 */
	public static <T,B> void copyListBeanProperties(List<T> sourceList,List<B> targetList,Class<B> targetBeanCls ){
		for (Object t : sourceList) {
			B b=createInstance(targetBeanCls);
			org.springframework.beans.BeanUtils.copyProperties(t, b);
			targetList.add(b);
		}
	}
	
	/**
	 * 深度拷贝list里面bean的值
	 * @param sourceList
	 * @param targetBeanCls
	 * @return
	 */
	public static <T,B> List<B> copyListBeanProperties(List<T> sourceList,Class<B> targetBeanCls ){
		List<B> targetList=new ArrayList<>();
		for (Object t : sourceList) {
			B b=createInstance(targetBeanCls);
			org.springframework.beans.BeanUtils.copyProperties(t, b);
			targetList.add(b);
		}
		return targetList;
	}
	
	/**
	 * 拷贝bean的属性
	 * @param source
	 * @param target
	 */
	public static <T,B> void copyBeanProperties(T source,B target){
		org.springframework.beans.BeanUtils.copyProperties(source, target);
	}
	
	/**
	 * 拷贝bean的属性
	 * @param source
	 * @param targetBeanCls
	 * @return
	 */
	public static <T,B> B copyBeanProperties(T source,Class<B> targetBeanCls ){
		if(null!=source){
			B target=createInstance(targetBeanCls);
			org.springframework.beans.BeanUtils.copyProperties(source, target);
			return target;
		}else{
			return null;
		}
		
		
	}
	
	
	private static <B> B createInstance(Class<B> cls) {
         B obj=null;
         try {
             obj=cls.newInstance();
         } catch (Exception e) {
             obj=null;
         }
         return obj;
     }

	public static <M> Object merge(M target, M destination) throws Exception {
		BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass());

		// Iterate over all the attributes
		for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

			// Only copy writable attributes
			if (descriptor.getWriteMethod() != null) {
				Object originalValue = descriptor.getReadMethod()
						.invoke(target);

				// Only copy values values where the destination values is null
				if (originalValue == null) {
					Object defaultValue = descriptor.getReadMethod().invoke(
							destination);
					descriptor.getWriteMethod().invoke(target, defaultValue);
				}

			}
		}
		return destination;
	}
}
