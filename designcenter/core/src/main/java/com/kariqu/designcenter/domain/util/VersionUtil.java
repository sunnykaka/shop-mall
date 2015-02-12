package com.kariqu.designcenter.domain.util;

import org.apache.commons.lang.StringUtils;

/**
 * 版本工具类
 *
 * @Author: Tiger
 * @Since: 11-6-11 下午1:40
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class VersionUtil {

    /**
     * 升级版本
     * @param version
     * @return
     */
    public static String upgradeVersion(String version) {
        String[] pieces = version.split("\\.");
        StringBuilder result = new StringBuilder();
        for (String piece : pieces) {
            result.append(piece);
        }
        int intVersion = Integer.valueOf(result.toString());
        intVersion++;
        char[] chars = String.valueOf(intVersion).toCharArray();
        StringBuilder stringVersion = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0)
                stringVersion.append('.');
            stringVersion.append(chars[i]);
        }
        return stringVersion.toString();
    }

    /**
     * 降级版本
     * @param version
     * @return
     */
    public static String degradeVersion(String version) {
        if (StringUtils.isEmpty(version)) {
            return version;
        }
        StringBuilder builder = new StringBuilder();
        String[] pieces = version.split("\\.");
        for (String piece : pieces) {
            builder.append(piece);
        }
        int intVersion = Integer.valueOf(builder.toString());
        intVersion--;
        StringBuilder stringVersion = new StringBuilder();
        char[] chars = String.valueOf(intVersion).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0)
                stringVersion.append('.');
            stringVersion.append(chars[i]);
        }
        return stringVersion.toString();
    }
}
