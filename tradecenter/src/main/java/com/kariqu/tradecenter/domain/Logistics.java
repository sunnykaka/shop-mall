package com.kariqu.tradecenter.domain;

import com.kariqu.suppliercenter.domain.DeliveryInfo;

/**
 * 物流
 * 这个对象冗余了地址的信息，这样即便地址删除了，订单还保留这个信息
 * User: Asion
 * Date: 11-10-11
 * Time: 下午8:29
 */
public class Logistics {

    private long id;

    private long orderId;

    private int addressId;

    //配送信息
    private DeliveryInfo deliveryInfo;

    //冗余地址信息，当用户删除地址的时候，地址信息还在

    /**
     * 是谁的地址
     */
    private String addressOwner;

    /**
     * 收获人姓名
     */
    private String name;

    /**
     * 省份，比如浙江
     */
    private String province;

    /**
     * 具体位置，到门牌号
     */
    private String location;

    /**
     * 移动电话
     */
    private String mobile;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 邮编
     */
    private String zipCode;

    private LogisticsRedundancy logisticsRedundancy;

    /**
     * 注入冗余的地址信息，物流对象保存一份当时订单的地址数据
     *
     * @param address
     */
    public void injectBackUpAddress(Address address) {
        this.setEmail(address.getEmail());
        this.setLocation(address.getLocation());
        this.setProvince(address.getProvince());
        this.setTelephone(address.getTelephone());
        this.setMobile(address.getMobile());
        this.setZipCode(address.getZipCode());
        this.setName(address.getName());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getAddressOwner() {
        return addressOwner;
    }

    public void setAddressOwner(String addressOwner) {
        this.addressOwner = addressOwner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public LogisticsRedundancy getLogisticsRedundancy() {
        return logisticsRedundancy;
    }

    public void setLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy) {
        this.logisticsRedundancy = logisticsRedundancy;
    }
}
