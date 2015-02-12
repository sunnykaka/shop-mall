package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.productcenter.domain.Consultation;

/**
 * 用户中心咨询列表页面
 * User: Alec
 * Date: 12-11-19
 * Time: 上午11:43
 * To change this template use File | Settings | File Templates.
 */
public class ConsultationAboutProduct{
    private Consultation consultation;
    private String productPictureUrl;
    private String productName;

    public String getProductPictureUrl() {
        return productPictureUrl;
    }

    public void setProductPictureUrl(String productPictureUrl) {
        this.productPictureUrl = productPictureUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }
}
