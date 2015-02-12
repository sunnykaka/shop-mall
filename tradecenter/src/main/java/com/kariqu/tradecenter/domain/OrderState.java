package com.kariqu.tradecenter.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单状态
 *
 * @author Athens
 */
public enum OrderState {

    /** 已创建 */
    Create("创建订单, 等待付款", "", "【客户】提交订单","待支付"),

    /** 付款完成(若订单是货到付款, 此一步将省略) */
    Pay("已付款, 等待发货", "", "【客户】付款完成, 等待【客服】确认","待发货"),

    /** 确认完成 */
    Confirm("已付款, 等待发货", "等待打单", "【客服】确认完成, 等待【商家】打单","待发货"),

    /** 商家确认打单 */
    Print("已付款, 等待发货", "等待验货", "【商家】已打单, 等待【商家】验货","待发货"),

    /** 商家已验货 */
    Verify("已付款, 等待发货", "等待发货", "【商家】已验货, 等待【商家】发货","待发货"),

    /** 商家已发货 */
    Send("已发货,等待收货", "已发货", "【商家】已发货, 等待【客户】收货","待收货"),

    /** 交易成功 */
    Success("交易完成", "交易完成", "交易完成","交易完成"),

    /** 退货完成 */
    Back("已退货", "", "【客户】已退货","交易完成"),

    /** 取消 */
    Cancel("已取消", "", "订单已取消","交易取消"),

    /** 关闭 */
    Close("已关闭", "", "订单已关闭","交易取消");

    String userVisible;
    String supplierVisible;
    String serviceVisible;
    String customerVisible;//用户订单状态
    OrderState(String userVisible, String supplierVisible, String serviceVisible,String customerVisible) {
        this.userVisible = userVisible;
        this.supplierVisible = supplierVisible;
        this.serviceVisible = serviceVisible;
        this.customerVisible = customerVisible;
    }

    /**
     * 针对客户的状态说明
     */
    public String toDesc() {
        return userVisible;
    }

    /**
     * 针对客服的状态说明
     */
    public String serviceDesc() {
        return serviceVisible;
    }

    /**
     * 针对商家的状态说明
     */
    public String supplierDesc() {
        return supplierVisible;
    }

    public String customerDesc(){
        return customerVisible;
    }

    /**
     * 检查订单状态是否能取消.
     *
     * @return 若不能取消则返回 true
     */
    public boolean checkCanNotCancel() {
        return this != Create;
    }

    /**
     * 是否可以增加赠品, 若能增加则返回 true.
     */
    public boolean canAddGift() {
        return this == Pay;
    }

    /**
     * 检查订单状态是否能确认收货.
     *
     * @return 若不能确认收货则返回 true
     */
    public boolean checkCanNotConfirmSuccess() {
        return this != Send;
    }

    /**
     * 判断是否可以确认收货.
     *
     * @return 若能确认收货则返回 true
     */
    public boolean accordComplete() {
        return !checkCanNotConfirmSuccess();
    }

    /**
     * 检查订单状态是否能退货.
     *
     * @return 若不能退货则返回 true
     */
    public boolean checkCanNotBack() {
        return this == Create || this == Back || this == Cancel || this == Close;
    }

    /**
     * 根据 订单状态 判断 订单 是否有发货, 可以退货且订单还未流到商家处则表示未发货.
     *
     * @return 若订单未发货, 则返回 true. <span style="color:red;">状态流转到商家后由客服线下判断</span>
     */
    public boolean checkNotSend() {
        return !checkCanNotBack() && this == Pay;
    }

    /**
     * 检查订单状态是否有发货(用于请求物流数据).
     *
     * @return 若还未发货则返回 true
     */
    public boolean checkSendForLogistics() {
        return this == Send || this == Success;
    }

    /**
     * 是否可以填写评价
     */
    public boolean waitValuation() {
        return this == Success;
    }

    /**
     * 是否是等待付款
     */
    public boolean waitPay(PayType payType) {
        return this == Create && payType == PayType.OnLine;
    }

    /**
     * 前台判断检查订单状态是否是未付款状态
     */
    public boolean checkNotPay() {
        return this == Create;
    }

    /**
     * 前台判断订单信息,检查交易是否是已付款状态
     */
    public boolean checkPay() {
        return this == Pay || this == Confirm || this == Print || this == Verify || this == Send;
    }

    /**
     * 前台判断订单信息,检查订单交易是否完成
     */
    public boolean checkTradeComplete() {
        return this == Success || this == Back;
    }

    /**
     * 前台判断订单信息,检查是否是无效订单
     */
    public boolean checkInvalidTrade() {
        return this == Cancel || this == Close;
    }

    /**
     * 用户在查看详情时可见的订单状态
     */
    public static List<OrderState> userState() {
        return Arrays.asList(Create, Pay, Confirm, Print, Verify, Send, Success, Back, Close, Cancel);
    }

}
