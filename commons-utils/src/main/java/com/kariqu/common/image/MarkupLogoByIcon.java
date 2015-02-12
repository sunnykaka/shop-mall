package com.kariqu.common.image;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 给图片添加水印
 * User: Eli
 * Date: 12-7-11
 * Time: 下午2:15
 */
public class MarkupLogoByIcon {

    protected static final Log logger = LogFactory.getLog(MarkupLogoByIcon.class);


    /**
     * @param iconPath   水印文件
     * @param srcImgPath 原文件
     * @param alpha      透明度[0.0, 1.0]
     * @param degree     logo旋转角度
     */
    private static BufferedImage getBufferedImage(String iconPath, String srcImgPath, Float alpha, Integer degree) {
        try {
            Image image = ImageIO.read(new File(srcImgPath));
            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);

            // 创建图片缓冲区
            BufferedImage bufferImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

            // 得到画笔对象
            Graphics2D graphics = bufferImage.createGraphics();
            // 设置对线段的锯齿状边缘处理
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH), 0, 0, null);

            if (null != degree) {
                // 设置水印旋转
                graphics.rotate(Math.toRadians(degree), (double) bufferImage.getWidth() / 2, (double) bufferImage.getHeight() / 2);
            }

            if (null == alpha || alpha < 0.0F || alpha > 1.0F) {
                alpha = 0.5F;
            }
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            Image icon_img = ImageIO.read(new File(iconPath));
            int icon_width = icon_img.getWidth(null);
            int icon_height = icon_img.getHeight(null);

            int centerX = (imageWidth - icon_width)/2;
            int centerY = (imageHeight - icon_height)/2;

            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

            // 表示水印图片的位置，让水印居中
            graphics.drawImage(icon_img, centerX, centerY, null);

            // 水印文件结束
            graphics.dispose();

            return bufferImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 给图片添加水印，会生成张新的带水印图片
     * 原图片 + 水印图片 + 新图片
     *
     * @param iconPath   水印图片路径
     * @param srcImgPath 原图片路径
     * @param targetPath 目标图片路径
     * @param alpha      透明度  [0.0, 1.0]
     * @param degree     logo的旋转角度
     */
    public static void markupImageByIcon(String iconPath, String srcImgPath, String targetPath, Float alpha, Integer degree) {
        OutputStream outputStream = null;
        try {

            BufferedImage bufferImage = getBufferedImage(iconPath, srcImgPath, alpha, degree);
            // 定义生成图片的名称和路径
            Date date = new Date();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = (fmt.format(date).replace(" ", "&")).replace(":", "-").trim();

            // 修改图片名(路径+当前时间+后缀)，如：F:/2012-07-11&15-59-25.jpg
            String realImgName = targetPath.trim() + "/" + currentTime + ".jpg";

            outputStream = new FileOutputStream(realImgName);

            // 生成图片，使用支持给定格式将图片写入流
            ImageIO.write(bufferImage, "JPG", outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(outputStream);  // 关闭流
        }
    }


    /**
     * 给图片添加水印（修改原图）
     *
     * @param iconPath   水印图片路径
     * @param srcImgPath 原图片路径
     * @param alpha      透明度  [0.0, 1.0]
     * @param degree     logo的旋转角度
     */
    public static byte[] markImageByIcon(String iconPath, String srcImgPath, Float alpha, Integer degree) {

        byte[] imgBytes = null;
        ByteArrayOutputStream out = null;

        try {
            BufferedImage bufferImage = getBufferedImage(iconPath, srcImgPath, alpha, degree);

            // 字节数组输出流
            out = new ByteArrayOutputStream();

            // 使用支持格式将一个图片写入流
            ImageIO.write(bufferImage, "JPG", out);

            // 创建字节数组
            imgBytes = out.toByteArray();

            return imgBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(out);  // 关闭流
        }

    }


    /**
     * 给图片添加水印（修改原图）
     *
     * @param iconPath   水印图片路径
     * @param srcImgPath 原图片路径
     * @param alpha      透明度  [0.0, 1.0]
     * @param degree     logo的旋转角度
     */
    public final static void pressImage(String iconPath, String srcImgPath, Float alpha, Integer degree) {
        OutputStream outputStream = null;
        try {
            BufferedImage bufferImage = getBufferedImage(iconPath, srcImgPath, alpha, degree);

            outputStream = new FileOutputStream(new File(srcImgPath));

            // 生成图片，使用给定格式将一个图片写入流
            ImageIO.write(bufferImage, "JPG", outputStream);
        } catch (Exception e) {
            logger.error("图片添加水印失败！", e);
        } finally {
            IOUtils.closeQuietly(outputStream);  // 关闭流
        }
    }

    public static void main(String[] args){
        pressImage("D:\\picture\\w.jpg", "D:\\picture\\1.jpg",1.0F, 0);
    }

}
