package com.kariqu.productcenter.domain;

/**
 * 产品图片
 * 对应大图，因为一个商品可能有多张大图，其他小图通过大图的路径加上像素来定位
 * 商品图片上传的时候会生成各种缩略图
 * <p/>
 * 一个商品存在一个主图，主图唯一
 * 一个sku存在一组图片
 * @Author: Tiger
 * @Since: 11-6-26 下午1:57
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class ProductPicture {

    private int id;

    private int productId;

    private String name;

    private String originalName;

    private String pictureUrl;

    private String pictureLocalUrl;

    private boolean mainPic; //是否主图

    private boolean minorPic;//是否副图

    private String skuId;

    private int number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public boolean isMainPic() {
        return mainPic;
    }

    public void setMainPic(boolean mainPic) {
        this.mainPic = mainPic;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getPictureLocalUrl() {
        return pictureLocalUrl;
    }

    public void setPictureLocalUrl(String pictureLocalUrl) {
        this.pictureLocalUrl = pictureLocalUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public boolean isMinorPic() {
        return minorPic;
    }

    public void setMinorPic(boolean minorPic) {
        this.minorPic = minorPic;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
