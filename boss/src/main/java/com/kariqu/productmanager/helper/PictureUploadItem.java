package com.kariqu.productmanager.helper;

import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传表现层包装对象
 * User: Asion
 * Date: 11-9-22
 * Time: 上午10:05
 */
public class PictureUploadItem {

    private int productId;

    private String name;

    private MultipartFile uploadFile;

    private boolean mainPic;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public boolean isMainPic() {
        return mainPic;
    }

    public void setMainPic(boolean mainPic) {
        this.mainPic = mainPic;
    }

}
