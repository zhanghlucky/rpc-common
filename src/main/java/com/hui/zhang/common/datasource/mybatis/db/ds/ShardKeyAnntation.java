package com.hui.zhang.common.datasource.mybatis.db.ds;

import java.lang.annotation.*;

/**
 * ${DESCRIPTION}
 *
 * @author:jiangshun@centaur.cn
 * @create 2018-04-25 18:02
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface ShardKeyAnntation {
    String classpath () default  "";
}
