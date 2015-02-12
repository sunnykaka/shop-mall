package com.kariqu.categorycenter.domain.model.navigate;

import java.util.List;

/**
 * User: Asion
 * Date: 13-4-26
 * Time: 上午9:51
 */
public class NavigateCategorySettings {

    private int navId;

    /** 二级类目 banner 图 */
    private List<String> mainImg;

    /** 二级类目广告图(在三级类目中显示) */
    private List<String> adImg;

    /** 图片链接 */
    private List<String> mainImgLink;

    /** 设置logo图 */
    private String logoImg;

    private int mainProduct;

    private String minorProduct;

    private boolean hot = false;

    public List<String> getMainImg() {
        return mainImg;
    }

    public void setMainImg(List<String> mainImg) {
        this.mainImg = mainImg;
    }

    public List<String> getAdImg() {
        return adImg;
    }

    public void setAdImg(List<String> adImg) {
        this.adImg = adImg;
    }

    public List<String> getMainImgLink() {
        return mainImgLink;
    }

    public void setMainImgLink(List<String> mainImgLink) {
        this.mainImgLink = mainImgLink;
    }

    public int getMainProduct() {
        return mainProduct;
    }

    public void setMainProduct(int mainProduct) {
        this.mainProduct = mainProduct;
    }

    public String getLogoImg() {
        return logoImg;
    }

    public void setLogoImg(String logoImg) {
        this.logoImg = logoImg;
    }

    public String getMinorProduct() {
        return minorProduct;
    }

    public void setMinorProduct(String minorProduct) {
        this.minorProduct = minorProduct;
    }

    public int getNavId() {
        return navId;
    }

    public void setNavId(int navId) {
        this.navId = navId;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }
}
