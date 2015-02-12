package com.kariqu.usercenter.domain;

import java.util.Date;

/**
 * 第三方帐号信息
 * User: Alec
 * Date: 12-12-11
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public class UserOuter {
    private int id;
    /**
     * 外部账号认证系统的跟踪ID
     */
    private String outerId;

    //账户类型，可区别出来自什么网站，比如QQ，sina，KRQ代表我们自己，不持久，是动态构造的
    private AccountType accountType;

    private int userId;

    private Date createDate;

    private boolean isLocked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


}
