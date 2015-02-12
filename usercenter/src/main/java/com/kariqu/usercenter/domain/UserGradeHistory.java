package com.kariqu.usercenter.domain;

import java.util.Date;

/**
 * 等级成长历史
 * User: Asion
 * Date: 13-3-19
 * Time: 上午10:00
 */
public class UserGradeHistory {

    private int id;

    private int userId;

    private UserGrade grade;

    private Date createTime;

    private Date updateTime;

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

    public UserGrade getGrade() {
        return grade;
    }

    public void setGrade(UserGrade grade) {
        this.grade = grade;
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
