package com.kariqu.om.domain;

import java.util.Date;

public class Answer {

    private int id;

    /** 问题Id */
    private int questionId;

    /** 答案内容 */
    private String answerDetail;

    /** 是否需要用户手动输入(默认是 false 不需要) */
    private boolean hasWrite = false;

    private Date createTime;
    private Date updateTime;

    /** 提交问卷的此答案的数量(不写入数据库, 只在统计时用到) */
    private int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getAnswerDetail() {
        return answerDetail;
    }

    public void setAnswerDetail(String answerDetail) {
        this.answerDetail = answerDetail;
    }

    /** 是否需要用户手动输入(默认是 false 不需要) */
    public boolean isHasWrite() {
        return hasWrite;
    }

    /** 是否需要用户手动输入(默认是 false 不需要) */
    public void setHasWrite(boolean hasWrite) {
        this.hasWrite = hasWrite;
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

    /** 提交的数量(不写入数据库, 只在统计时用到) */
    public int getNumber() {
        return number;
    }

    /** 提交的数量(不写入数据库, 只在统计时用到) */
    public void setNumber(int number) {
        this.number = number;
    }

}
