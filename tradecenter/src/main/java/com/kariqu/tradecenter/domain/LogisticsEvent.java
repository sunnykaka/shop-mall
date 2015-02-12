package com.kariqu.tradecenter.domain;

import java.util.Date;

/**
 * 物流事件
 * User: Asion
 * Date: 11-10-11
 * Time: 下午8:57
 */
public class LogisticsEvent {

    private long logisticsId;

    /**
     * 事件时间
     */
    private Date date;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 事件的详细信息
     */
    private String eventInfo;


    /**
     * 事件前订单状态
     */
    private OrderState beforeState;


    /**
     * 事件后订单状态
     */
    private OrderState afterState;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public long getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(long logisticsId) {
        this.logisticsId = logisticsId;
    }

    public OrderState getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(OrderState beforeState) {
        this.beforeState = beforeState;
    }

    public OrderState getAfterState() {
        return afterState;
    }

    public void setAfterState(OrderState afterState) {
        this.afterState = afterState;
    }
}
