package com.hui.zhang.common.tcc.annotation;

import java.lang.annotation.*;

@Documented   
@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.METHOD)
public @interface Tcc {
	String confirm();
	String cancel();
}
