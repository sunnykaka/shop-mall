package com.kariqu.productmanager.helper;

import com.kariqu.productcenter.domain.ConsultationCategory;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-8-31
 * Time: 下午5:46
 */
public class ConsultationVo {

    private int id;

    private String productCode;

    private String productName;

    private String askContent;

    private String answerContent;

    private ConsultationCategory consultationCategory;

    private int hasAnswer;

    private String askedUserName;

    private String askTime;

    private String answerTime;

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getAskContent() {
        return askContent;
    }

    public void setAskContent(String askContent) {
        this.askContent = askContent;
    }

    public String getAskedUserName() {
        return askedUserName;
    }

    public void setAskedUserName(String askedUserName) {
        this.askedUserName = askedUserName;
    }

    public ConsultationCategory getConsultationCategory() {
        return consultationCategory;
    }

    public void setConsultationCategory(ConsultationCategory consultationCategory) {
        this.consultationCategory = consultationCategory;
    }

    public int getHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(int hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAskTime() {
        return askTime;
    }

    public void setAskTime(String askTime) {
        this.askTime = askTime;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
