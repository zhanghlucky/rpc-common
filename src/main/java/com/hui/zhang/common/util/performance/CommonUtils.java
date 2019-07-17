/**
 * 
 */
package com.hui.zhang.common.util.performance;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by zuti on 2017/11/9.
 * email zuti@centaur.cn
 */
public class CommonUtils {

    /**
     * 转化时间格式 
     * 
     * @param timeInMillis
     * @return
     */
    public static String formatDate(long timeInMillis) {
        StringBuilder returnValue = new StringBuilder(19);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        returnValue.append(year).append('-');
        padIntToTwoDigits(month + 1, returnValue).append('-');
        padIntToTwoDigits(day, returnValue).append(' ');
        padIntToTwoDigits(hour, returnValue).append(':');
        padIntToTwoDigits(minute, returnValue).append(':');
        return padIntToTwoDigits(second, returnValue).toString();
    }
    
    /**
     * 补齐两位 
     * 
     * @param i
     * @param toAppend
     * @return
     */
    private static StringBuilder padIntToTwoDigits(int i, StringBuilder toAppend) {
        if (i < 10) {
            toAppend.append("0");
        }
        return toAppend.append(i);
    }
    
    /**
     * 获取最大长度 
     * 
     * @param keySet
     * @return
     */
    public static int getLongestStrLen(Set<String> keySet) {
        int longestLength = 0;
        for (String tag : keySet) {
            if (tag.length() > longestLength) {
                longestLength = tag.length();
            }
        }
        return longestLength;
    }
    
}
