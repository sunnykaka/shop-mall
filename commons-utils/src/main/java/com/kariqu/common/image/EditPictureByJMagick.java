package com.kariqu.common.image;

import magick.CompositeOperator;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;

/**
 * User: Alec
 * Date: 13-1-11
 * Time: 下午3:13
 */
public class EditPictureByJMagick {

    protected final static Log logger = LogFactory.getLog(EditPictureByJMagick.class);

    /**
     * 水印(图片logo)
     *
     * @param filePath 源文件路径
     * @param toImg    修改图路径
     * @param logoPath logo图路径
     * @throws MagickException
     */
    public static void initLogoImg(String filePath, String toImg, String logoPath) throws MagickException {
        ImageInfo info = new ImageInfo();
        MagickImage fImage = null;
        MagickImage fLogo = null;
        Dimension imageDim = null;
        Dimension logoDim = null;
        try {
            fImage = new MagickImage(new ImageInfo(filePath));
            imageDim = fImage.getDimension();
            int width = imageDim.width;
            int height = imageDim.height;

            fLogo = new MagickImage(new ImageInfo(logoPath));
            logoDim = fLogo.getDimension();
            int logoWidth = logoDim.width;
            int logoHeight = logoDim.height;

            fImage.compositeImage(CompositeOperator.AtopCompositeOp, fLogo, (width - logoWidth) / 2, (height - logoHeight) / 2);
            fImage.setFileName(toImg);
            fImage.writeImage(info);
            logger.error("加水印后的图片文件："+fImage.getFileName());
        } finally {
            if (fImage != null) {
                fImage.destroyImages();
            }
            if (fLogo != null) {
                fLogo.destroyImages();
            }
        }
    }


}
