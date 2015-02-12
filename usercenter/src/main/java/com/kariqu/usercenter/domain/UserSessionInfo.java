package com.kariqu.usercenter.domain;

/**
 * 用于自动登录实体类
 * User: ennoch
 * Date: 12-7-3
 * Time: 下午2:14
 */
public class UserSessionInfo {

    private String userId;

    private String cookieValue;

    private String checkInDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }
}
