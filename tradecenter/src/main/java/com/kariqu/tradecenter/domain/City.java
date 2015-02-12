package com.kariqu.tradecenter.domain;

/**
 * 城市信息
 *
 * @author Athens(刘杰)
 */
public class City {

    private int id;

    private String name;

    private String provinceId;

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

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }
}
