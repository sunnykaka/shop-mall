package com.kariqu.usercenter.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户基本资料表
 *
 * @author alec
 * @version 1.0.0
 * @since 2013-1-15 下午09:14:58
 */
public class UserData{

    private static final long serialVersionUID = 7993724590252729908L;

    private int id;

    private int userId;

    private String name;

    private int sex;

    private String phoneNumber;

    private int familyNumber;

    private int hasMarried;

    private String birthday;

    private boolean isDelete;

    private Date createDate;

    private Date updateDate;

    public UserData(){}

    public UserData(int userId) {
        this.userId = userId;
    }

    private static Map<Integer, String> userSex = new HashMap<Integer, String>();
    private static Map<Integer, String> userHasMarried = new HashMap<Integer, String>();
    private static Map<Integer, String> userFamilyNumber = new HashMap<Integer, String>();

    static {
        userSex.put(0, "保密");
        userSex.put(1, "男");
        userSex.put(2, "女");

        userHasMarried.put(0, "保密");
        userHasMarried.put(1, "未婚");
        userHasMarried.put(2, "已婚");

        userFamilyNumber.put(0, "保密");
        userFamilyNumber.put(1, "1-2人");
        userFamilyNumber.put(2, "2-4人");
        userFamilyNumber.put(3, "4人以上");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getFamilyNumber() {
        return familyNumber;
    }

    public void setFamilyNumber(int familyNumber) {
        this.familyNumber = familyNumber;
    }

    public int getHasMarried() {
        return hasMarried;
    }

    public void setHasMarried(int hasMarried) {
        this.hasMarried = hasMarried;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
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

    public String getUserSex() {
        if (sex < 0 || sex > 2) {
            return "保密";
        }
        return userSex.get(sex);
    }

    public String getUserHasMarried() {
        if (hasMarried < 0 || hasMarried > 2) {
            return "保密";
        }
        return userHasMarried.get(hasMarried);
    }

    public String getUserFamilyNumber() {
        if (familyNumber < 0 || familyNumber > 3) {
            return "保密";
        }
        return userFamilyNumber.get(hasMarried);
    }
}
