package com.kariqu.common;

/**
 * 获取字符串长度
 * User: Alec
 * Date: 13-9-2
 * Time: 下午5:30
 */
public class ChineseLength {
    /**
     * 中文为两个字符
     * @param value
     * @return
     */

    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
}
