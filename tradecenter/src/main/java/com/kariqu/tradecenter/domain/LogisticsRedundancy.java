package com.kariqu.tradecenter.domain;


import java.util.Date;

/**
 * 冗余客服修改用户收货地址.
 * User: Alec
 * Date: 12-12-10
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public class LogisticsRedundancy {
    /**
     * 物流跟踪主键Id
     */
    private long id;
    private String editor;
    private String nameRewrite;
    private String provinceRewrite;
    private String locationRewrite;
    private String mobileRewrite;
    private String zipCodeRewrite;
    private Date timeRewrite;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getNameRewrite() {
        return nameRewrite;
    }

    public void setNameRewrite(String nameRewrite) {
        this.nameRewrite = nameRewrite;
    }

    public String getProvinceRewrite() {
        return provinceRewrite;
    }

    public void setProvinceRewrite(String provinceRewrite) {
        this.provinceRewrite = provinceRewrite;
    }

    public String getLocationRewrite() {
        return locationRewrite;
    }

    public void setLocationRewrite(String locationRewrite) {
        this.locationRewrite = locationRewrite;
    }

    public String getMobileRewrite() {
        return mobileRewrite;
    }

    public void setMobileRewrite(String mobileRewrite) {
        this.mobileRewrite = mobileRewrite;
    }

    public String getZipCodeRewrite() {
        return zipCodeRewrite;
    }

    public void setZipCodeRewrite(String zipCodeRewrite) {
        this.zipCodeRewrite = zipCodeRewrite;
    }

    public Date getTimeRewrite() {
        return timeRewrite;
    }

    public void setTimeRewrite(Date timeRewrite) {
        this.timeRewrite = timeRewrite;
    }
}
