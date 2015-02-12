package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.BaseQuery;

/**
 * User: Asion
 * Date: 13-3-7
 * Time: 下午2:04
 */
public class ValuationQuery extends BaseQuery {

    private int productId;

    private int userId;

    private String userName;

    private long orderItemId;

    private LikeFilter point;


    private ValuationQuery() {

    }

    public static ValuationQuery asProductId(int productId) {
        ValuationQuery valuationQuery = new ValuationQuery();
        valuationQuery.setProductId(productId);
        return valuationQuery;
    }

    public static ValuationQuery asProductIdAndLikeFilter(int productId, LikeFilter filter) {
        ValuationQuery valuationQuery = new ValuationQuery();
        valuationQuery.setProductId(productId);
        valuationQuery.setPoint(filter);
        return valuationQuery;
    }

    public static ValuationQuery asProductIdAndUserId(int productId, int userId) {
        ValuationQuery valuationQuery = new ValuationQuery();
        valuationQuery.setProductId(productId);
        valuationQuery.setUserId(userId);
        return valuationQuery;
    }


    public static ValuationQuery asUsrId(int userId) {
        ValuationQuery valuationQuery = new ValuationQuery();
        valuationQuery.setUserId(userId);
        return valuationQuery;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public LikeFilter getPoint() {
        return point;
    }

    public void setPoint(LikeFilter point) {
        this.point = point;
    }

    public enum LikeFilter {
        all("全部评论"),

        Good("好评"),

        Fine("一般"),

        Bad("差评");

        private String value;

        LikeFilter(String value) {
            this.value = value;
        }
        public String toDesc() {
            return value;
        }
    }
}
