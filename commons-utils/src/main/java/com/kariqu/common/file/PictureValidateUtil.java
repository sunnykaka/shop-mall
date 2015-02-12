package com.kariqu.common.file;

import java.util.LinkedList;
import java.util.List;

/**
 * 验证图片的工具
 * User: Asion
 * Date: 12-4-15
 * Time: 下午4:39
 */
public class PictureValidateUtil {

    /**
     * 检查是否是图片
     *
     * @param name
     * @return
     */
    public static boolean isPicture(String name) {
        List<String> pictureFile = new LinkedList<String>();
        name = name.toLowerCase();
        pictureFile.add(".jpg");
        pictureFile.add(".gif");
        pictureFile.add(".png");
        pictureFile.add(".jpeg");
        for (String type : pictureFile) {
            if (name.endsWith(type))
                return true;
        }
        return false;
    }

    /** 获取文件后缀(小写) */
    public static String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}
