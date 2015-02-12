package com.kariqu.productcenter.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图片上传服务
 * User: Asion
 * Date: 12-6-1
 * Time: 下午2:03
 */
public interface UploadImageService {

    PictureUploadResult uploadPicture(InputStream inputStream, String fileName) throws  Exception;

    boolean deletePicture(String productPictureUrl)throws IOException;

}
