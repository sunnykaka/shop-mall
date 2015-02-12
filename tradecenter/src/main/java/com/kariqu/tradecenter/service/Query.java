package com.kariqu.tradecenter.service;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.OrderState;

/**
 * User: ennoch
 * Date: 12-6-28
 * Time: 下午5:43
 */
public class Query {

    private OrderState orderState;

    private String startDate;

    private String endDate;

    private int customerId;

    /** 查询选项(订单创建人 userName, 收货人 consignee, 快递单号 expressNo, 订单编号 orderNo) */
    private String queryOption;
    /** 查询值(订单创建人, 收货人, 快递单号, 订单编号) */
    private String queryValue;

    /** 排序字段(若为空则默认使用支付时间反序) */
    private String sortValue;
    /** 排序模式(asc 正序, 默认 或 desc 反序) */
    private String sortMode;

    private String storageId;

    private String dateType;

    private DeliveryInfo.DeliveryType deliveryType;

    private int start;

    private int limit;

    public String getQuery() {
        return "";
    }


    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        if(null ==orderState || "".equals(orderState)){
            this.orderState =null;
        }else{
            this.orderState = orderState;
        }
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

    /** 查询选项(订单创建人 userName, 收货人 consignee, 快递单号 expressNo, 订单编号 orderNo) */
    public String getQueryOption() {
        return queryOption;
    }

    /** 查询选项(订单创建人 userName, 收货人 consignee, 快递单号 expressNo, 订单编号 orderNo) */
    public void setQueryOption(String queryOption) {
        this.queryOption = queryOption;
    }

    /** 查询值(订单创建人, 收货人, 快递单号, 订单编号) */
    public String getQueryValue() {
        return queryValue;
    }

    /** 查询值(订单创建人, 收货人, 快递单号, 订单编号) */
    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    /** 排序字段(若为空则默认使用支付时间反序) */
    public String getSortValue() {
        return sortValue;
    }

    /** 排序字段(若为空则默认使用支付时间反序) */
    public void setSortValue(String sortValue) {
        this.sortValue = sortValue;
    }

    /** 排序模式(asc 正序, 默认 或 desc 反序) */
    public String getSortMode() {
        return sortMode;
    }

    /** 排序模式(asc 正序, 默认 或 desc 反序) */
    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        if(null ==storageId ||"".equals(storageId)){
            this.storageId="0";
        }else{
            this.storageId = storageId;
        }
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public DeliveryInfo.DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryInfo.DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }
}
