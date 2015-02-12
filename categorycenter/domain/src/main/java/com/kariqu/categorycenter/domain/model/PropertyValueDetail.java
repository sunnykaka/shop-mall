package com.kariqu.categorycenter.domain.model;

/**
 * 类目属性值的详情，比如鞋子类目中鞋跟这个属性，它有3cm,5cm等不同的属性值，
 * 可以通过具体的图片来展示这些类目属性值
 * 注意：它只描述属性和值，不和类目相关，所以如果不同类目都要用到这个值描述，那么
 * 他们必须妥协为公共的描述，比如红色的鞋就具体描述了鞋，如果另外一个类目衣服也要用这个红色
 * 那么他们共同决定用一个红色图片代替，而不是包含类目信息
 * @Author: Tiger
 * @Since: 11-6-25 下午1:17
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class PropertyValueDetail {

    private int id;

    private int propertyId;

    private int valueId;

    private String pictureUrl;

    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
