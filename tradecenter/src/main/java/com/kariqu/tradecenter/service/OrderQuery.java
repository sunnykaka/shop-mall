package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.OrderState;
import org.apache.commons.lang.StringUtils;

/**
 * User: Asion
 * Date: 12-6-20
 * Time: 上午9:33
 */
public class OrderQuery {

    private int start;

    private int limit;

    private OrderState[] orderState;

    //下单时间的筛选区间
    private String startDate;

    private String endDate;

    //是哪个商家的订单
    private int customerId;

    //是哪个商家的哪个仓库的订单
    private int storageId;

    private long orderNo;

    //收货人
    private String consignee;

    //手机号
    private String mobile;

    //下单人
    private String orderOwner;

    /** 下单人 Id */
    private String userId;

    /** 商品名 */
    private String skuName;

    /** 评价相关 */
    private String appraise;

    /** 刷单(0 表示未刷单, 1 表示已刷单.) */
    private Integer brush;

    public Integer getBrush() {
        return brush;
    }

    public void setBrush(Integer brush) {
        this.brush = brush;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getAppraise() {
        return appraise;
    }

    public void setAppraise(String appraise) {
        this.appraise = appraise;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public OrderState[] getOrderState() {
        return orderState;
    }

    public String orderState() {
        if (orderState != null) {
            StringBuilder sbd = new StringBuilder();
            int i = 0;
            for (OrderState state : orderState) {
                sbd.append("'").append(state.toString()).append("'");
                if ((i + 1) != orderState.length) sbd.append(",");
                i++;
            }
            return sbd.toString();
        }
        return null;
    }

    public void setOrderState(OrderState[] orderState) {
        this.orderState = orderState;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderOwner() {
        return orderOwner;
    }

    public void setOrderOwner(String orderOwner) {
        this.orderOwner = orderOwner;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
