package com.kariqu.productcenter.service.impl;

import com.kariqu.common.image.EditPictureByJMagick;
import com.kariqu.common.file.FileStore;
import com.kariqu.common.file.FileType;
import com.kariqu.common.file.PictureToSmall;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.common.image.MarkupLogoByIcon;
import com.kariqu.productcenter.service.PictureUploadResult;
import com.kariqu.productcenter.service.UploadImageService;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 上传工具
 * User: Alec
 * Date: 12-7-3
 * Time: 下午5:01
 */
public class UploadImageServiceImpl implements UploadImageService {

    protected final Log logger = LogFactory.getLog(UploadImageServiceImpl.class);
    //是否上传云存储
    private boolean upLoadOnCdn;
    //是否使用jmagick
    private boolean useJMagick;
    private String paramPressImg;
    private String savePosition;
    private FileStore yunStore;
    private URLBrokerFactory urlBrokerFactory;
    private List<Double> scales = new ArrayList<Double>(0);
    //比例放大100倍
    private static int SCALE_ZOOM = 100;

    private static final Executor executor = Executors.newSingleThreadExecutor();


    /**
     * 上传商品图片，需要缩放图片
     * 将上传的文件保存并生成缩略图
     * 使用ImageMagick + jmagick
     * ImageMagick通过exe安装,jmagick是一个jar，放到tomcat的lib下，jmagick中的dll复制一份放到系统的system32下和再复制一份和ImageMagick的安装目录那些dll一起
     *
     * @param inputStream
     * @return
     * @throws java.io.IOException
     */
    public PictureUploadResult uploadPicture(InputStream inputStream, String fileName) throws Exception {
        System.setProperty("jmagick.systemclassloader", "no");
        PictureUploadResult uploadResult = new PictureUploadResult();
        OutputStream outputStream = null;
        try {
            File pictureFile = new File(savePosition + fileName);
            File parentFile = new File(pictureFile.getParent());
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!pictureFile.exists()) {
                pictureFile.createNewFile();
            }
            outputStream = new FileOutputStream(pictureFile);

            int readBytes;
            byte[] buffer = new byte[10000];
            while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
                outputStream.write(buffer, 0, readBytes);
            }
            if (logger.isInfoEnabled())
                logger.info("原图上传本地成功! path:" + pictureFile.getAbsoluteFile().getAbsolutePath());

            productImgCompress(scales, fileName, savePosition);

            if (logger.isInfoEnabled())
                logger.info("生成缩略图成功!");
            //加水印
            if (null != paramPressImg && !"".equals(paramPressImg)) {
                File logoFile = new File(UploadImageServiceImpl.class.getClassLoader().getResource(paramPressImg).getFile());
                if (logoFile.exists()) {
                    if (useJMagick) {
                        if (logger.isInfoEnabled())
                            logger.info("JMagick加水印处理！");
                        EditPictureByJMagick.initLogoImg(savePosition + fileName, savePosition + fileName, logoFile.getPath());

                        if (logger.isInfoEnabled())
                            logger.info("JMagick加水印成功！");
                    } else {
                        MarkupLogoByIcon.pressImage(logoFile.getPath(), savePosition + fileName, 1.0F, 0);
                    }
                } else {
                    if (logger.isErrorEnabled())
                        logger.error("配置的水印文件不存在[" + logoFile.getPath() + "]");
                }
            } else {
                if (logger.isErrorEnabled())
                    logger.error("未配置水印文件!");
            }

            uploadResult.setLocalSuccess(true);

            if (upLoadOnCdn) {
                File uploadFile = new File(savePosition + fileName);
                FileStore.StoreResult result = yunStore.store(uploadFile.getName(), new FileInputStream(uploadFile), FileType.PICTURE);
                String pictureUrl = result.getDataEntry("FilePath");

                String originalName = fileName.substring(0, fileName.lastIndexOf("."));
                String originalType = fileName.substring(fileName.lastIndexOf("."));
                List<String> scalePictureFileName = new ArrayList<String>(scales.size());

                for (Double scale : scales) {
                    String suffix = "_" + new Double(scale * SCALE_ZOOM).intValue();
                    scalePictureFileName.add(savePosition + originalName + suffix + originalType);
                }

                if (result.isSuccess() || StringUtils.isNotEmpty(pictureUrl)) {
                    asynchUploadScalePicture(scalePictureFileName);
                    uploadResult.setCdnSuccess(true);
                    uploadResult.setCdnUrl(pictureUrl);
                }
            }
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignore) {

            }
        }
        return uploadResult;
    }

    /**
     * 根据上传的图片生成配置的比例大小图片
     *
     * @param scaleList
     * @param newFileName
     * @param savePosition
     * @throws IOException
     * @throws MagickException
     */
    public void productImgCompress(List<Double> scaleList, String newFileName, String savePosition) throws IOException, MagickException {
        if (null == scaleList || scaleList.size() == 0) {
            return;
        }

        String originalName = newFileName.substring(0, newFileName.lastIndexOf("."));
        String originalType = newFileName.substring(newFileName.lastIndexOf("."));
        if (logger.isWarnEnabled())
            logger.warn("是否有使用 JMagick : " + useJMagick + ", 比例 : " + scaleList);
        MagickImage image = null;
        try {
            if (useJMagick) {
                ImageInfo info = new ImageInfo(savePosition + newFileName);
                image = new MagickImage(info);
                Dimension imageDim = image.getDimension();
                int width = imageDim.width;
                int height = imageDim.height;
                for (double scale : scaleList) {
                    MagickImage scaled = null;
                    try {
                        String suffix = "_" + new Double(scale * SCALE_ZOOM).intValue();
                        scaled = image.scaleImage((int) (width * scale + 0.5), (int) (height * scale + 0.5));//小图片文件的大小.
                        scaled.setFileName(savePosition + originalName + suffix + originalType);//存在位置+原始文件名+缩放像素+文件类型
                        scaled.writeImage(info);
                        if (logger.isWarnEnabled())
                            logger.warn("JMagick比例缩略图已生成 ：" + scaled.getFileName());
                    } finally {
                        if (scaled != null)
                            scaled.destroyImages();
                    }
                }
            } else {
                BufferedImage bufferedImage = ImageIO.read(new File(savePosition + newFileName));
                for (Double scale : scaleList) {
                    String suffix = "_" + new Double(scale * SCALE_ZOOM).intValue();
                    ImageIO.write(PictureToSmall.resizeImage(bufferedImage, scale), originalType.substring(1), new File(savePosition + originalName + suffix + originalType));
                }
            }
        } finally {
            if (image != null) image.destroyImages();
        }
    }

    /**
     * 上传压缩处理的图片到云端
     *
     * @param uploadFile
     * @throws FileNotFoundException
     */
    private void upLoadSpcPictureToYunStore(List<String> uploadFile) throws IOException {
        FileInputStream inputStream;
        for (String fileName : uploadFile) {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
            yunStore.store(file.getName(), inputStream, FileType.PICTURE);
            inputStream.close();
        }

    }

    /**
     * 商品所有的物理存储的图片
     *
     * @param pictureUrl
     * @return
     * @throws IOException
     */
    public boolean deletePicture(String pictureUrl) throws IOException {
        File pictureFile = new File(pictureUrl);
        if (null == pictureFile) {
            return false;
        }
        String pictureFileParentName = new File(pictureFile.getParent()).getName();
        String fileName = pictureFile.getName();
        String realName = fileName.substring(0, fileName.lastIndexOf("."));
        String realType = fileName.substring(fileName.lastIndexOf("."));


        List<String> deleteFileNames = new ArrayList();
        deleteFileNames.add(fileName);
        for (Double scale : scales) {
            String suffix = "_" + new Double(scale * SCALE_ZOOM).intValue();
            deleteFileNames.add(realName + suffix + realType);
        }

        for (String name : deleteFileNames) {
            String deletePath = savePosition + pictureFileParentName + File.separator + name;
            new File(deletePath).delete();
            if (upLoadOnCdn) {
                yunStore.deleteFile(name, FileType.PICTURE);
            }
        }
        return false;
    }


    /**
     * 新线程上传经过缩略图
     *
     * @param scalePictureFileName
     */
    private void asynchUploadScalePicture(final List scalePictureFileName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    upLoadSpcPictureToYunStore(scalePictureFileName);
                } catch (IOException e) {
                    logger.error("上传图片失败", e);
                }
            }
        });
    }

    public boolean isUpLoadOnCdn() {
        return upLoadOnCdn;
    }

    public void setUpLoadOnCdn(boolean upLoadOnCdn) {
        this.upLoadOnCdn = upLoadOnCdn;
    }

    public boolean isUseJMagick() {
        return useJMagick;
    }

    public void setUseJMagick(boolean useJMagick) {
        this.useJMagick = useJMagick;
    }

    public String getSavePosition() {
        return savePosition;
    }

    public void setSavePosition(String savePosition) {
        if (!savePosition.endsWith("/")) {
            savePosition = savePosition + "/";
        }
        this.savePosition = savePosition;
    }

    public URLBrokerFactory getUrlBrokerFactory() {
        return urlBrokerFactory;
    }

    public void setUrlBrokerFactory(URLBrokerFactory urlBrokerFactory) {
        this.urlBrokerFactory = urlBrokerFactory;
    }

    public FileStore getYunStore() {
        return yunStore;
    }

    public void setYunStore(FileStore yunStore) {
        this.yunStore = yunStore;
    }

    public List<Double> getScales() {
        return scales;
    }

    public void setScales(List<Double> scales) {
        this.scales = scales;
    }

    public String getParamPressImg() {
        return paramPressImg;
    }

    public void setParamPressImg(String paramPressImg) {
        this.paramPressImg = paramPressImg;
    }
}
