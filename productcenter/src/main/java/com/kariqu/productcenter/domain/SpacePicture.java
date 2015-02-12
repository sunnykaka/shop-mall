package com.kariqu.productcenter.domain;

/**
 * 空间图片
 * User: ennoch
 * Date: 12-7-10
 * Time: 上午10:54
 */
public class SpacePicture {

    private int id;

    private int spaceId;

    private String pictureName;

    private String originalName;

    private String pictureUrl;

    private String pictureLocalUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureLocalUrl() {
        return pictureLocalUrl;
    }

    public void setPictureLocalUrl(String pictureLocalUrl) {
        this.pictureLocalUrl = pictureLocalUrl;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }
}
