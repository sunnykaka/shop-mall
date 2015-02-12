package com.kariqu.suppliercenter.domain;

/**
 * 商品仓库
 * User: Asion
 * Date: 12-6-20
 * Time: 下午1:37
 */
public class ProductStorage {

    private int id;

    /**
     * 库存名字
     */
    private String name;

    /**
     * 哪个商家的库存
     */
    private int customerId;


    /**
     * 这个字段在此对象在表现层的时候set进来
     */
    private String customerName;

    /**
     * 库存的位置信息，将来可能用来自动定位库存
     * 目前没有使用
     */
    private String location;

    /**
     * 发货人
     */
    private String consignor;

    private String telephone;

    private String address;

    private String remarks;

    private String company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsignor() {
        return consignor;
    }

    public void setConsignor(String consignor) {
        this.consignor = consignor;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductStorage that = (ProductStorage) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
