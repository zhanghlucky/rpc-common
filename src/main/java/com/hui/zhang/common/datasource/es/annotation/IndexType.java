package com.hui.zhang.common.datasource.es.annotation;

import java.lang.annotation.*;

@Documented   
@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.TYPE)  
public @interface IndexType {
	
	String value();   
}
