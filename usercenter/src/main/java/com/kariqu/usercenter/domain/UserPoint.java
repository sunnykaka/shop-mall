package com.kariqu.usercenter.domain;

import com.kariqu.common.lib.Money;

import java.util.Date;

/**
 * 用户积分
 * User: Asion
 * Date: 13-3-18
 * Time: 上午10:11
 */
public class UserPoint {

    private int id;

    private int userId;

    //积分
    private long point;

    private Date createTime;

    private Date updateTime;

    //描述
    private String description;

    /**
     * 收入支出类型
     */
    private InOutComingType inOutComingType;

    /**
     * 表示收入还是支出
     */
    private PointType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getPoint() {
        return point;
    }

    public String getCurrency() {
        return Money.getMoneyString(point);
    }

    public void setPoint(long point) {
        this.point = point;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InOutComingType getInOutComingType() {
        return inOutComingType;
    }

    public void setInOutComingType(InOutComingType inOutComingType) {
        this.inOutComingType = inOutComingType;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

    public static enum InOutComingType {
        /**
         * 购买行为得到积分
         */
        Order,

        /**
         * 评价行为得到积分
         */
        Valuation,

        /**
         * 取消订单回加积分
         */
        Cancel,

        /**
         * 签到得到的积分
         */
        SignIn,

        /**
         * 抽奖时抽中的积分
         */
        Lottery,

        /**
         * 兑换现金券时消耗积分
         */
        Exchange,

        /**
         * 注册得积分
         */
        Register;
    }

    public static enum PointType {
        /**
         * 收入
         */
        InComing,

        /**
         * 支出
         */
        OutComing
    }

}
