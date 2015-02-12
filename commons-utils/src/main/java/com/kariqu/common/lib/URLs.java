package com.kariqu.common.lib;

import java.net.URLEncoder;

/**
 * 跟url相关的方法
 * Created by Canal.wen on 2014/6/27 14:33.
 */
public class URLs {
    public static String addParam(String originalUrl, String name, String value) {
        return originalUrl + (originalUrl.contains("?") ? "&" : "?") + encodePart(name) + "=" + encodePart(value);
    }

    public static String encodePart(String part) {
        try {
            return URLEncoder.encode(part, "utf-8");
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }
}
