package com.hui.zhang.common.datasource.mongo.annotation;

import java.lang.annotation.*;

@Documented   
@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.TYPE)  
public @interface CollName {
	
	String value();   
}
