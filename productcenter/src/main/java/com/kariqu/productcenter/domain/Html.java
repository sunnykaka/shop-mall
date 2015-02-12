package com.kariqu.productcenter.domain;

/**
 * 商品的超文本描述
 * User: Asion
 * Date: 11-9-6
 * Time: 上午10:58
 */
public class Html {

    private int productId;

    private String name;

    private String content;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
