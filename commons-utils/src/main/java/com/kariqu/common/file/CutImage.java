package com.kariqu.common.file;

/**
 * 图片裁剪
 * User: Alec
 * Date: 13-4-3
 * Time: 下午3:17
 */

import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class CutImage {
    private String srcpath;
    private String subpath;
    private String imageType;
    private int x;
    private int y;
    private int width;
    private int height;

    public CutImage() {
    }

    public CutImage(String srcpath, int x, int y, int width, int height) {
        this.srcpath = srcpath;
        if (StringUtils.isNotBlank(srcpath)) {
            this.imageType = srcpath.substring(srcpath.lastIndexOf(".") + 1);
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSrcpath() {
        return srcpath;
    }

    public void setSrcpath(String srcpath) {
        this.srcpath = srcpath;
    }

    public String getSubpath() {
        return subpath;
    }

    public void setSubpath(String subpath) {
        this.subpath = subpath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public void cut() throws IOException {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            is = new FileInputStream(srcpath);
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(this.imageType);
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, this.imageType, new File(subpath));
        } finally {
            if (is != null)
                is.close();
            if (iis != null)
                iis.close();
        }
    }

    public static void main(String[] args) {
       /* String x = "xxx.xxx.xx..jpg";
        System.out.println(x.substring(0, x.lastIndexOf(".")));//xxx.xxx.xx.
        System.out.println(x.substring(x.lastIndexOf(".")));//.jpg*/

        String c = "    xxx   xxx xx .    jpg    ";
        /* CutImage o = new CutImage("D:\\project\\1.jpg", 70, 70, 200, 200);
        o.setSubpath("D:\\project\\2.jpg");
        try {
            o.cut();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        String[] split = c.trim().split("[ ]+");
        for (String s : split) {

            System.out.println(s+"f");
        }
    }
}