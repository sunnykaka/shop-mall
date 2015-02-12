package com.kariqu.accountcenter.domain;


import java.io.Serializable;

/**
 * 后台系统的账户模型，有一个默认的密码
 * 各种系统都统一使用这个账号，但是可以使用自己的密码
 * User: Asion
 * Date: 11-11-15
 * Time: 下午4:07
 */
public class Account implements   Serializable {

    private int id;

    private String userName;
    
    private String englishName;

    private String email;

    private transient String password;

    private boolean leaveOffice;
    private boolean deleteData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public boolean isLeaveOffice() {
        return leaveOffice;
    }

    public void setLeaveOffice(boolean leaveOffice) {
        this.leaveOffice = leaveOffice;
    }

    public boolean isDeleteData() {
        return deleteData;
    }

    public void setDeleteData(boolean deleteData) {
        this.deleteData = deleteData;
    }

    @Override
    public String toString() {
        return "(id=" + id +
                ", userName='" + userName + "'" +
                ", englishName='" + englishName + "'" +
                ", email='" + email + "')";
    }
}
