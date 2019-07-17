package com.hui.zhang.common.util;

/**
 */
public interface StringEncoder {
    public <T>  String encode(T t);

    public <T> T decode(String var1, Class<T> var2);

    public <T> T decode(String var1, Class<T> clazz, Class... constructClazzs);
}
