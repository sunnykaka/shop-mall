package com.kariqu.usercenter.domain;

import java.util.Date;

/**
 * 用户签到记录
 *
 * @author Athens(刘杰)
 */
public class UserSignIn {

    private int id;

    /** 用户Id */
    private int userId;

    /** 连续签到次数 */
    private int signInCount;

    private Date createDate;
    private Date updateDate;

    public UserSignIn() {}

    public UserSignIn(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSignInCount() {
        return signInCount;
    }

    public void setSignInCount(int signInCount) {
        this.signInCount = signInCount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

}
