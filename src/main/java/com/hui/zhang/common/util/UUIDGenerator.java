package com.hui.zhang.common.util;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class UUIDGenerator {
    /**
     * 获得32位的 UUID
     *
     * @return
     */
    public static String random32UUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13)
                + str.substring(14, 18) + str.substring(19, 23)
                + str.substring(24);
        return temp.toUpperCase();
    }

    /**
     * 获得16位的UUID
     *
     * @return
     */
    public static String random16UUID() {
        String key = String.valueOf(System.currentTimeMillis())
                + UUID.randomUUID().toString();
        StringBuffer buf = null;
        String uuid = MD5Util.MD5(key);
        return uuid.substring(8, 24).toUpperCase(); // 16位的加密
        // return buf.toString());// // 32位的加密
    }

    /**
     * 获得16位的UUID
     *
     * @return
     */
    public static String random8UUID() {
        String key = String.valueOf(System.currentTimeMillis())
                + UUID.randomUUID().toString();
        String uuid = MD5Util.MD5(key);
        return uuid.substring(8, 16).toUpperCase();
    }

}
