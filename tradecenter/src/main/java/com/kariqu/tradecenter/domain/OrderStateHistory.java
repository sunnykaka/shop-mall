package com.kariqu.tradecenter.domain;

import java.util.Date;

/**
 * 状态历史
 * User: Asion
 * Date: 12-6-6
 * Time: 下午4:05
 */
public class OrderStateHistory {

    private long id;

    /**
     * 订单状态
     */
    private OrderState orderState;

    /**
     * 该状态的时间
     */
    private Date date;

    /**
     * 关联订单
     */
    private long orderId;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 做了何事?
     */
    private String doWhat;

    /**
     * 备注
     */
    private String remark;


    /**
     * 是否被覆盖，有些状态是互斥的，比如等待确认和确认成功
     * 那么查询的时候可以根据这个标志查询最新的状态
     * <p/>
     * 默认都是没有被覆盖的，有些状态是不互斥的
     */
    private boolean overlay = false;

    /**
     * 如果这个值为真，则状态不会出现在用户所看到的界面上
     * 订单状态可能有很多种，但是不一定每个状态用户都关心
     */
    private boolean debugMode = false;

    /**
     * 状态级别，这个值来自当前的状态
     */
    private int stateLevel;

    public OrderStateHistory() {
    }

    /**
     * 根据 订单 及 具体操作 创建历史记录
     */
    public OrderStateHistory(Order order) {
        this(order, order.getUserName(), order.getOrderState().toDesc(), "");
    }

    /**
     * 根据 订单 及 具体操作 创建历史记录
     */
    public OrderStateHistory(Order order, String doWhat) {
        this(order, order.getUserName(), doWhat, "");
    }

    /**
     * 根据 订单、具体操作 及 备注 创建历史记录
     */
    public OrderStateHistory(Order order, String doWhat, String remark) {
        this(order, order.getUserName(), doWhat, remark);
    }

    /**
     * 根据 订单、操作者、具体操作 及 备注 创建历史记录
     */
    public OrderStateHistory(Order order, String operator, String doWhat, String remark) {
        this.orderState = order.getOrderState();
        this.date = new Date();
        this.orderId = order.getId();
        this.operator = operator;
        this.doWhat = doWhat;
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
        //this.stateLevel = orderState.getLevel();
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public void setOverlay(boolean overlay) {
        this.overlay = overlay;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public int getStateLevel() {
        return stateLevel;
    }

    public void setStateLevel(int stateLevel) {
        this.stateLevel = stateLevel;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDoWhat() {
        return doWhat;
    }

    public void setDoWhat(String doWhat) {
        this.doWhat = doWhat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
