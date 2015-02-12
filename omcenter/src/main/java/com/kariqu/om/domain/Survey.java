package com.kariqu.om.domain;

import java.util.Date;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-22 17:02
 * @since 1.0.0
 */
public class Survey {

    private int id;

    /** 问卷调查名称 */
    private String surveyName;

    /** 问卷调查说明 */
    private String surveyExplain;

    private Date createTime;
    private Date updateTime;

    private List<Question> questionList;

    /** 提交问卷的数量(不写入数据库, 只在统计时用到) */
    private int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyExplain() {
        return surveyExplain;
    }

    public void setSurveyExplain(String surveyExplain) {
        this.surveyExplain = surveyExplain;
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

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
