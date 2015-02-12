package com.kariqu.common.lib;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * web层的一些工具方法
 * Created by Canal.wen on 2014/6/27 14:32.
 */
public class WebUtils {
    public static void outputTxt(HttpServletResponse response, String text){
        response.setContentType("text/plain;charset=UTF-8");
        writeText(response, text);
    }

    public static void outputJson(HttpServletResponse response, String json){
        response.setContentType("application/json;charset=UTF-8");
        writeText(response, json);
    }

    /**
     * 设置下载文件时,提示文件名为中文. 解决在firefox, chrome下中文文件名的问题
     * @param response
     */
    public static void downloadFileHttpHeadSet(String fileName, HttpServletResponse response) {
        response.setHeader("content-disposition", String.format("attachment;filename*=utf-8'zh_cn'%s.xls", URLs.encodePart(fileName)));
    }

    public static void downloadHttpHeadSet(String fileName, HttpServletResponse response) {
        String[] suffixs=fileName.split("\\.");
        String prefix = suffixs[0];
        String suffix = suffixs[suffixs.length-1];
        response.setHeader("content-disposition", String.format("attachment;filename*=utf-8'zh_cn'%s.%s", URLs.encodePart(prefix),suffix));
        response.setHeader("Content-Type", "application/octet-stream");
    }

    private static void writeText(HttpServletResponse response, String text) {
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
        response.addDateHeader("Expires", 1L);
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNullOrZero(Object o) {
        if(o == null) return true;
        if (o instanceof Number) {
            if(((Number)o).intValue() == 0) {
                return true;
            }
        }
        return false;
    }

}
