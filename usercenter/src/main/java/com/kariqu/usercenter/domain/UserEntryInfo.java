package com.kariqu.usercenter.domain;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-05 17:53
 * @since 1.0.0
 */
public class UserEntryInfo {

    /** 用户名 */
    private String userName;

    /** 登录次数 */
    private int count;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
