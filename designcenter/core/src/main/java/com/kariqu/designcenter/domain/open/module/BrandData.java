package com.kariqu.designcenter.domain.open.module;

/**
 * @author Athens(刘杰)
 * @Time 2012-10-24 14:32
 * @since 1.0.0
 */
public class BrandData {

    /** 品牌编号 */
    private int brandId;

    /** 品牌名字 */
    private String brandName;

    /** 图片地址 */
    private String imageUrl;

    /** 此值主要用来生成 品牌链接地址 */
    private long pidVid;

    /** 品牌编号 */
    public int getBrandId() {
        return brandId;
    }

    /** 品牌编号 */
    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    /** 品牌名字 */
    public String getBrandName() {
        return brandName;
    }

    /** 品牌名字 */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /** 图片地址 */
    public String getImageUrl() {
        return imageUrl;
    }

    /** 图片地址 */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /** 此值主要用来生成 品牌链接地址 */
    public long getPidVid() {
        return pidVid;
    }

    /** 此值主要用来生成 品牌链接地址 */
    public void setPidVid(long pidVid) {
        this.pidVid = pidVid;
    }
}
