package com.hui.zhang.common.datasource.mybatis.db.ds;

//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.hui.zhang.common.datasource.mybatis.spring.AbstractRoutingDataSource;

/**
 * 动态数据源（需要继承AbstractRoutingDataSource）
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
    public static void setDataSourceKey(String databaseType) {
        contextHolder.set(databaseType);
    }
    public static String getDatabaseType(){
        return contextHolder.get();
    }
    protected Object determineCurrentLookupKey() {
        return contextHolder.get();
    }
    public static void clear(){
        contextHolder.remove();
    }


}