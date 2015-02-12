package com.kariqu.om.domain;

import java.util.Date;
import java.util.List;

public class Result {

    private int id;
    
    /** 问卷调查Id */
    private int surveyId;
    
    /** 问卷提交人(若不登录并提交问卷则此值为 0) */
    private int userId;
    
    /** 问卷提交人的邮箱 */
    private String email;
    
    /** 问卷提交人的收件地址 */
    private String address;
    
    /** 问卷提交人的名字(用于收货) */
    private String userName;
    
    /** 问卷提交人的电话(用于收货) */
    private String mobile;
    
    /** 提交的建议 */
    private String suggest;
    
    private Date createTime;
    private Date updateTime;

    private List<ResultDetail> resultDetailList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    /** 问卷提交人(若不登录并提交问卷则此值为 0) */
    public int getUserId() {
        return userId;
    }

    /** 问卷提交人(若不登录并提交问卷则此值为 0) */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
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

    public List<ResultDetail> getResultDetailList() {
        return resultDetailList;
    }

    public void setResultDetailList(List<ResultDetail> resultDetailList) {
        this.resultDetailList = resultDetailList;
    }
}
