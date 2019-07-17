package com.hui.zhang.common.datasource.mybatis.db.ds;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-04-25 18:02
 **/
@Target({ METHOD })
@Retention(RetentionPolicy.SOURCE)
public @interface MyTransactional {
    String value() default "";
}
