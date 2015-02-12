package com.kariqu.suppliersystem.orderManager.vo;

import java.io.Serializable;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-12-10
 * Time: 下午12:31
 */
public class OrderPrintInfo implements Serializable {

    /**
     * 收货人*
     */
    private String consignee;

    /**
     * 收货人地址*
     */
    private String address;

    /**
     * 收货人联系方式*
     */
    private String telephone;

    /**
     * 发货人*
     */
    private String consignor;

    /**
     * 发货人地址*
     */
    private String consignorAddress;

    /**
     * 发货人联系方式*
     */
    private String consignorTelephone;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 发货人单位名称
     */
    private String company;

    /**发货时间*/
    private String deliveryTime;


    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    private String waybillNumber;

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getConsignor() {
        return consignor;
    }

    public void setConsignor(String consignor) {
        this.consignor = consignor;
    }

    public String getConsignorAddress() {
        return consignorAddress;
    }

    public void setConsignorAddress(String consignorAddress) {
        this.consignorAddress = consignorAddress;
    }

    public String getConsignorTelephone() {
        return consignorTelephone;
    }

    public void setConsignorTelephone(String consignorTelephone) {
        this.consignorTelephone = consignorTelephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
