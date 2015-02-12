package com.kariqu.usercenter.domain;

import java.util.Date;

/**
 * 取消订阅的邮件
 *
 * @author Athens(刘杰)
 * @Time 13-4-25 上午11:46
 */
public class UnSubscribe {

    private int id;
    private String email;
    private Date createTime;
    private Date updateTime;

    public UnSubscribe() {}

    public UnSubscribe(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
