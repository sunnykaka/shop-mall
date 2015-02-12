package com.kariqu.productmanager.helper;


import com.kariqu.categorycenter.domain.util.PidVid;

/**
 * 封装商品的属性值pidvid，分为销售属性和关键属性
 * User: Asion
 * Date: 11-9-15
 * Time: 下午6:47
 */
public class ProductPidVid {

    private PidVid sellProperty;

    private PidVid keyProperty;

    public ProductPidVid(PidVid sellProperty, PidVid keyProperty) {
        this.sellProperty = sellProperty;
        this.keyProperty = keyProperty;
    }

    //判断销售属性的值是否为空
    public boolean isSellEmpty() {
        return sellProperty.checkEmpty();
    }

    //判断关键属性的值是否为空
    public boolean isKeyEmpty() {
        return keyProperty.checkEmpty();
    }

    public PidVid getSellProperty() {
        return sellProperty;
    }

    public void setSellProperty(PidVid sellProperty) {
        this.sellProperty = sellProperty;
    }

    public PidVid getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(PidVid keyProperty) {
        this.keyProperty = keyProperty;
    }
}
