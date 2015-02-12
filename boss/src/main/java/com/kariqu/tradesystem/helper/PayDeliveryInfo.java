package com.kariqu.tradesystem.helper;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.PayType;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-28
 *        Time: 下午4:51
 */
public class PayDeliveryInfo {

    //送货方式
    private DeliveryInfo.DeliveryType deliveryType;

    //支付信息
    private PayType payType;

    private String payBank;

    private String waybillNumber;

    //订单总价格
    private String totalPrice;

    private String orderState;


    public DeliveryInfo.DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryInfo.DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public PayType getPayType() {
        return payType;
    }

    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    public String getPayBank() {
        return payBank;
    }

    public void setPayBank(String payBank) {
        this.payBank = payBank;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }
}
