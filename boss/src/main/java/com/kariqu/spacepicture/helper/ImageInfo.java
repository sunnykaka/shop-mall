package com.kariqu.spacepicture.helper;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-9-27
 * Time: 下午4:39
 */
public class ImageInfo {
    private String id;
    private String imgBorder;
    private String referMethod;
    private String align;
    private String viewServer;
    private String imgWidth;
    private String url;
    private String imgTitle;
    private String imgHeight;

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgBorder() {
        return imgBorder;
    }

    public void setImgBorder(String imgBorder) {
        this.imgBorder = imgBorder;
    }

    public String getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(String imgHeight) {
        this.imgHeight = imgHeight;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public String getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(String imgWidth) {
        this.imgWidth = imgWidth;
    }

    public String getReferMethod() {
        return referMethod;
    }

    public void setReferMethod(String referMethod) {
        this.referMethod = referMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getViewServer() {
        return viewServer;
    }

    public void setViewServer(String viewServer) {
        this.viewServer = viewServer;
    }
}
