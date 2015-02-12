package com.kariqu.productcenter.service;

/**
 * 图片上传结果对象
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-7
 *        Time: 下午2:06
 */
public class PictureUploadResult {

    private boolean localSuccess;

    private boolean cdnSuccess;

    private String localUrl;
    
    private String cdnUrl;
    
    private String errorMsg;

    public boolean isLocalSuccess() {
        return localSuccess;
    }

    public void setLocalSuccess(boolean localSuccess) {
        this.localSuccess = localSuccess;
    }

    public boolean isCdnSuccess() {
        return cdnSuccess;
    }

    public void setCdnSuccess(boolean cdnSuccess) {
        this.cdnSuccess = cdnSuccess;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getCdnUrl() {
        return cdnUrl;
    }

    public void setCdnUrl(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
