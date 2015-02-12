package com.kariqu.productcenter.domain;

/**
 * sku 属性
 * 比如颜色红色，尺寸XL
 * @author Tiger
 * @version 1.0.0
 * @since 11-9-19 下午12:47
 */
public class SkuProperty {

    private long skuId;

    private int propertyId;

    private int valueId;

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
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
}
