package com.kariqu.productcenter.domain;

import java.util.Date;

/**
 * 用户浏览历史记录
 * User: Alec
 * Date: 13-8-1
 * Time: 上午10:37
 */
public class BrowsingHistory {

    private long id;

    /**
     * 用户登录Id
     */
    private int userId;
    /**
     * 商品Id
     */
    private int productId;
    /**
     * 访问用户跟踪Id
     */
    private String trackId;
    /**
     * 浏览历史创建日期
     */
    private Date createDate;


    public BrowsingHistory() {
    }

    public BrowsingHistory(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public BrowsingHistory(String trackId, int productId) {
        this.trackId = trackId;
        this.productId = productId;
    }

    public BrowsingHistory(int userId, String trackId, int productId) {
        this.userId = userId;
        this.trackId = trackId;
        this.productId = productId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
