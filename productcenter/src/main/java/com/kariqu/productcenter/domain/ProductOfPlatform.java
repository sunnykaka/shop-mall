package com.kariqu.productcenter.domain;

import java.util.Date;

/**
 * 加入第三方平台的商品
 * User: Alec
 * Date: 13-10-17
 * Time: 上午9:48
 */
public class ProductOfPlatform {
    private int id;
    private int productId;
    private Platform platform;
    private String productOfJson;
    private Date createDate;
    private Date updateDate;
    private boolean isDelete;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getProductOfJson() {
        return productOfJson;
    }

    public void setProductOfJson(String productOfJson) {
        this.productOfJson = productOfJson;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public enum Platform {
        JiaYouGou {
            @Override
            public String toDesc() {
                return "家有购";
            }
        };

        public abstract String toDesc();
    }
}
