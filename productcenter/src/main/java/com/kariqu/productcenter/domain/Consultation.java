package com.kariqu.productcenter.domain;

import com.kariqu.usercenter.domain.UserGrade;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * 商品咨询
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 上午10:22
 */
public class Consultation {

    private int id;

    private int productId;

    private String askContent;

    private String answerContent;

    private ConsultationCategory consultationCategory;

    private Integer hasAnswer;

    private String askedUserName;

    private int askUserId;

    private Date askTime;

    private Date answerTime;

    private UserGrade grade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAskContent() {
        return askContent;
    }

    public void setAskContent(String askContent) {
        this.askContent = askContent;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public ConsultationCategory getConsultationCategory() {
        return consultationCategory;
    }

    public void setConsultationCategory(ConsultationCategory consultationCategory) {
        this.consultationCategory = consultationCategory;
    }

    public UserGrade getGrade() {
        return grade;
    }

    public void setGrade(UserGrade grade) {
        this.grade = grade;
    }

    public Integer getHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(Integer hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    public String getAskedUserName() {
        if (StringUtils.isNotBlank(askedUserName) && askedUserName.length() > 2) {
            return askedUserName.substring(0, 1)
                    + askedUserName.substring(1, askedUserName.length() - 1).replaceAll(".", "*")
                    + askedUserName.substring(askedUserName.length() - 1);
        }

        return askedUserName;
    }

    public void setAskedUserName(String askedUserName) {
        this.askedUserName = askedUserName;
    }

    public Date getAskTime() {
        return askTime;
    }

    public void setAskTime(Date askTime) {
        this.askTime = askTime;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public int getAskUserId() {
        return askUserId;
    }

    public void setAskUserId(int askUserId) {
        this.askUserId = askUserId;
    }
}
