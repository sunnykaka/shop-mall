package com.kariqu.productcenter.domain;

import java.util.Date;

/**
 * 168活动中参与活动的商品
 * User: Baron.Zhang
 * Date: 2014/10/14
 * Time: 10:51
 */
public class Promotion {

    private int id;

    private int topicId;

    /** 对应的商品id */
    private int productId;

    private String productName;
    private String productDesc1;
    private String productDesc2;

    /** 活动类型标签，1是爆款，3是主推，6是一般性商品 */
    private int tag;
    /** 状态，0为正常，2为已删除 */
    private int status;
    /** 商品原价 */
    private int price;
    private int saleNum;
    private int saleStatus;
    /** 活动价格 */
    private int promotionPrice;
    private int sort;

    private String picture;
    /** 记录新增的时间 */
    private Date createTime;
    /** 记录最近修改的时间，如果为新增后未改动则保持与createTime一致 */
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc1() {
        return productDesc1;
    }

    public void setProductDesc1(String productDesc1) {
        this.productDesc1 = productDesc1;
    }

    public String getProductDesc2() {
        return productDesc2;
    }

    public void setProductDesc2(String productDesc2) {
        this.productDesc2 = productDesc2;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public int getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(int saleStatus) {
        this.saleStatus = saleStatus;
    }

    public int getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(int promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
