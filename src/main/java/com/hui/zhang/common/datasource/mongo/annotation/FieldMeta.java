package com.hui.zhang.common.datasource.mongo.annotation;

/**
 * Created by zhanghui on 2017/10/18.
 */
public @interface FieldMeta {
    String name() default "";
    boolean save() default true;
}
