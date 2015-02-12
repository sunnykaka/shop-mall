package com.kariqu.usercenter.helper;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-8-8
 * Time: 下午4:47
 */
public class SmsSend {
    private String message;
    private boolean isSuccess;
    private List<SendContent> sendContentList;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SendContent> getSendContentList() {
        return sendContentList;
    }

    public void setSendContentList(List<SendContent> sendContentList) {
        this.sendContentList = sendContentList;
    }
}

