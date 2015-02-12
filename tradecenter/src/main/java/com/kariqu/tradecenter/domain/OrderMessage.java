package com.kariqu.tradecenter.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-01 16:48
 * @since 1.0.0
 */
public class OrderMessage {

    public static enum UserType {
        /**
         * 用户
         */
        User,

        /**
         * 客服
         */
        Server,

        /**
         * 商家
         */
        Supplier;

        public static Map<UserType, String> mapping = new HashMap<UserType, String>();

        static {
            mapping.put(User, "用户(买家)");
            mapping.put(Server, "客服(卖家)");
            mapping.put(Supplier, "商家(厂家)");
        }

        public String toDesc() {
            return mapping.get(this);
        }

    }

    private long id;
    private long orderId;
    private long orderNo;

    private int userId;
    private String userName;

    private UserType userType;
    private String messageInfo;

    private Date createDate;
    private Date updateDate;

    public OrderMessage() {
    }

    /**
     * 此一构造只在用户添加留言备注时使用(userId, userName), 客服备注不要使用这个
     *
     * @param order
     * @param userType
     * @param messageInfo
     */
    public OrderMessage(Order order, UserType userType, String messageInfo) {
        this.orderId = order.getId();
        this.orderNo = order.getOrderNo();

        this.userId = order.getUserId();
        this.userName = order.getUserName();

        this.userType = userType;
        this.messageInfo = messageInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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

    public String getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
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
}
