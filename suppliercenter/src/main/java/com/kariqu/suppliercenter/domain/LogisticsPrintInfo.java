package com.kariqu.suppliercenter.domain;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 上午10:06
 */
public class LogisticsPrintInfo {
    private int id;

    private DeliveryInfo.DeliveryType name;
    /**
     * 订单号递增规律
     */
    private int law;

    private String logisticsPicturePath;

    private int customerId;

    private String printHtml;

    private String deliveryTypeName;


    public String getDeliveryTypeName() {
        return deliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DeliveryInfo.DeliveryType getName() {
        return name;
    }

    public void setName(DeliveryInfo.DeliveryType name) {
        this.name = name;
    }

    public String getPrintHtml() {
        return printHtml;
    }

    public void setPrintHtml(String printHtml) {
        this.printHtml = printHtml;
    }

    public int getLaw() {
        return law;
    }

    public void setLaw(int law) {
        this.law = law;
    }

    public String getLogisticsPicturePath() {
        return logisticsPicturePath;
    }

    public void setLogisticsPicturePath(String logisticsPicturePath) {
        this.logisticsPicturePath = logisticsPicturePath;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
