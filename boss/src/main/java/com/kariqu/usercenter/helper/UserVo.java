package com.kariqu.usercenter.helper;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-9-5
 * Time: 上午10:53
 */
public class UserVo {

    private int id;

    private String userName;

    private String email;

    //账户是否激活
    private boolean isActive;

    //最后登陆时间
    private String lastLoginTime;

    //注册时间
    private String registerDate;

    //登陆次数
    private int loginCount;

    //是否为禁用
    private boolean hasForbidden;

    //等级
    private String grade;

    //总积分
    private String pointTotal;

    private String phone;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHasForbidden() {
        return hasForbidden;
    }

    public void setHasForbidden(boolean hasForbidden) {
        this.hasForbidden = hasForbidden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPointTotal() {
        return pointTotal;
    }

    public void setPointTotal(String pointTotal) {
        this.pointTotal = pointTotal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
