package com.kariqu.productcenter.service.impl.sina;

/**
 * 临时存储上传图片的内容，格式，文件信息等
 */
public class ImageItem {
    private byte[] content;
    private String name = "pic";
    private String contentType;

    public ImageItem(String contentType, byte[] content) {
        this.content = content;
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

}
