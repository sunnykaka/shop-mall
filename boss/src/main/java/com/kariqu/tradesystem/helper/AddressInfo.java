package com.kariqu.tradesystem.helper;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-28
 *        Time: 下午4:36
 */
public class AddressInfo {

    //订单编号
    private long orderId;

    //收货人
    private String consignee;

    //送货街道
    private String location;

    //省
    private String province;

    //市
    private String city;

    //区
    private String districts;

    // 移动电话
    private String mobile;

    private String orderState;

    //邮政编码
    private String zipCode;


    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

}
