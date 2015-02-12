package com.kariqu.productcenter.service;

import org.apache.commons.lang.StringUtils;

/**
 * 图片在上传商品原图时会将图片压缩, 当前工具类主要用来给替换图片中的访问路径. 会被注入到 velocity 上下文中去
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-7
 *        Time: 下午1:07
 */
public class ProductPictureResolver {

    /** 图片比例 */
    private static final int MIN = 10, MIDDLE = 20, BIG = 37, HALF = 50, ORIG = 100;

    /** 小图, 比例 10, 示例: http://img01.yijushang.com/100_main_10_10.jpg */
    public static String getMinSizeImgUrl(String pictureUrl) {
        return buildPictureUrl(pictureUrl, MIN);
    }

    /** 中图, 比例 20, 示例: http://img01.yijushang.com/100_main_10_20.jpg */
    public static String getMiddleSizeImgUrl(String pictureUrl) {
        return buildPictureUrl(pictureUrl, MIDDLE);
    }

    /** 大图, 比例 37, 示例: http://img01.yijushang.com/100_main_10_37.jpg */
    public static String getBigSizeImgUrl(String pictureUrl) {
        return buildPictureUrl(pictureUrl, BIG);
    }

    /** 半图, 比例 50, 示例: http://img01.yijushang.com/100_main_10_50.jpg */
    public static String getHalfSizeImgUrl(String pictureUrl) {
        return buildPictureUrl(pictureUrl, HALF);
    }

    /** 原始无水印图, 示例: http://img01.yijushang.com/100_main_10_100.jpg */
    public static String getOriginalSizeImgUrl(String pictureUrl) {
        return buildPictureUrl(pictureUrl, ORIG);
    }

    /** 原始有水印图, 示例: http://img01.yijushang.com/100_main_10.jpg */
    public static String getWaterOriginalSizeImgUrl(String pictureUrl) {
        return buildPictureUrl(pictureUrl, 0);
    }

    private static String buildPictureUrl(String pictureUrl, int ratio) {
        if (StringUtils.isBlank(pictureUrl)) return StringUtils.EMPTY;

        // 文件后缀
        String fileType = pictureUrl.substring(pictureUrl.lastIndexOf("."));
        // 匹配所有 带下划线规格 或 不带下划线 的后缀, 替换成想要替换的后缀
        String regex = "_(\\d+)(_(" + MIN + "|" + MIDDLE + "|" + BIG + "|" + HALF + "|" + ORIG + "))?" + fileType + "$";
        // 若是有水印图, 则无需要替换
        return pictureUrl.replaceFirst(regex, (ratio != 0) ? ("_$1_" + ratio + fileType) : ("_$1" + fileType));
    }

}
