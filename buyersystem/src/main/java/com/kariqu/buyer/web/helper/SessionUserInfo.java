package com.kariqu.buyer.web.helper;

import com.kariqu.usercenter.domain.AccountType;

import java.io.Serializable;

/**
 * @author Athens(刘杰)
 * @Time 13-6-24 上午10:21
 */
public class SessionUserInfo implements Serializable {

    /** 用户Id */
    private int id;

    /** 用户类型 */
    private AccountType accountType;

    /** 用户名 */
    private String userName;

    private String email;

    private String phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "(" +
                "userId=" + id +
                ", accountType=" + accountType +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ')';
    }
}
