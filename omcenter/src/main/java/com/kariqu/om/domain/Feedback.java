package com.kariqu.om.domain;

/**
 * 信息反馈
 * @author: Eli
 * @since 1.0.0
 *        Date:12-11-13
 *        Time:上午10:56
 */

public class Feedback {
    /** 主键id */
    private int id;
    /** 反馈信息类型 */
    private String type;
    /** 反馈内容 */
    private String content;
    /** 反馈人的联系方式 */
    private String information;
    /** 上传附件 */
    private byte[] uploadFile;
    /** 附件类型 */
    private String fileType;
    /** 上传附件名 */
    private String fileName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public byte[] getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(byte[] uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
