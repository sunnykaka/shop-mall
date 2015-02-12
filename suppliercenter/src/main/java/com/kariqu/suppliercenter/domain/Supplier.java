package com.kariqu.suppliercenter.domain;

/**
 * 运营商家
 * User: Asion
 * Date: 12-6-20
 * Time: 下午1:36
 */
public class Supplier {
    private int id;

    private String name;

    private DeliveryInfo.DeliveryType defaultLogistics;

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

    public DeliveryInfo.DeliveryType getDefaultLogistics() {
        return defaultLogistics;
    }

    public void setDefaultLogistics(DeliveryInfo.DeliveryType defaultLogistics) {
        this.defaultLogistics = defaultLogistics;
    }
}
