package com.kariqu.backgoods.helper;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-12-5
 *        Time: 下午1:14
 */
public class BackSku {
    
    private String productName;
    private int number;
    private String skuPrice;
    private String attribute;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
