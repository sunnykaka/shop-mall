package com.kariqu.usercenter.domain;

import com.kariqu.common.DateUtils;

import java.util.Date;

/**
 * User: kyle
 * Date: 13-1-6
 * Time: 下午2:04
 */
public class MessageTask {
    private  long  id;
    private  String contact;
    private String content;
    private MessageSendWay sendWay;
    private  boolean sendSuccess;
    private int sendCount;
    private Date createDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageSendWay getSendWay() {
        return sendWay;
    }

    public void setSendWay(MessageSendWay sendWay) {
        this.sendWay = sendWay;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public boolean isSendSuccess() {
        return sendSuccess;
    }

    public void setSendSuccess(boolean sendSuccess) {
        this.sendSuccess = sendSuccess;
    }

    public String getDate() {
        return DateUtils.formatDate(createDate, DateUtils.DateFormatType.DATE_FORMAT_STR);
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
