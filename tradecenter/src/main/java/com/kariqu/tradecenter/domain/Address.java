package com.kariqu.tradecenter.domain;

/**
 * 用户的地址对象
 * User: Asion
 * Date: 11-10-11
 * Time: 下午12:41
 */
public class Address {

    /**
     * 地址Id
     */
    private int id;


    /**
     * 是谁的地址
     */
    private int userId;

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

    /**
     * 是否是缺省地址
     */
    private boolean defaultAddress;

    /**
     * 使用频率
     */
    private int frequency;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(province).append(location);
        return sb.toString();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
