package com.kariqu.om.domain;

import java.util.Date;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-22 17:06
 * @since 1.0.0
 */
public class Question {

    private int id;

    /** 问卷调查外键 */
    private int surveyId;

    /** 问卷调查详细问题 */
    private String questionDetail;

    /** 是否要求用户必须回答(默认是 true) */
    private boolean mustReply = true;

    /** 问卷调查的答案类型 */
    private AnswerType answerType = AnswerType.radio;

    private Date createTime;
    private Date updateTime;

    private List<Answer> answerList;

    /** 提交问卷时针对此一问题的回答数量(不写入数据库, 只在统计时用到) */
    private int number;

    public static enum AnswerType {
        /** 单选 */
        radio,

        /** 多选 */
        checkbox;
    }

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

    public String getQuestionDetail() {
        return questionDetail;
    }

    public void setQuestionDetail(String questionDetail) {
        this.questionDetail = questionDetail;
    }

    /** 是否要求用户必须回答(默认是 true) */
    public boolean isMustReply() {
        return mustReply;
    }

    /** 是否要求用户必须回答(默认是 true) */
    public void setMustReply(boolean mustReply) {
        this.mustReply = mustReply;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
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

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}

