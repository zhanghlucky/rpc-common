package com.hui.zhang.common.share.annotation;

import java.lang.annotation.*;

/**
 * Created by zhanghui on 2018/1/19.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CacheMapper {

    String[] cacheKey();

    Class cls();

    String valueColumn();

    String tableName() default "";

    String shardName() default  "";
}
