package com.kariqu.om.domain;

import java.util.Date;

public class ResultDetail {

    private int id;

    /** 问卷结果Id */
    private int resultId;

    /** 问题Id */
    private int questionId;

    /** 答案Id */
    private int answerId;

    /** 用户输入的回答(若答案要求用户输入) */
    private String answerInput;

    private Date createTime;
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getAnswerInput() {
        return answerInput;
    }

    public void setAnswerInput(String answerInput) {
        this.answerInput = answerInput;
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
