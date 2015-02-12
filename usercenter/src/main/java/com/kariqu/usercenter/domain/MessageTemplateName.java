package com.kariqu.usercenter.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-6
 * Time: 下午2:26
 */
public enum MessageTemplateName {

    /** 付款成功 */
    PAY_SUCCESS,

    /** 订单确认 */
    ORDER_CONFIRM,

    /** 账户激活 */
    ACCOUNT_ACTIVATE,

    /** 找回密码 */
    PASSWORD_RECOVERY,

    /** 账户通知 */
    ACCOUNT_INFORM,

    /** 催款 */
    CALL_ORDER,

    /** 退货单审核通过 */
    BACKGOODS_AUDIT_PASS,

    /** 退货成功 */
    REFUND_SUCCESS,

    /** 发货 */
    DELIVER,

    /** 送积分 */
    SEND_POINT,

    /** 评价后送积分短信 */
    VALUATION_SEND_POINT,

    /** 现金券过期提醒 */
    EXPIRE_COUPON_REMIND;


    private static Map<MessageTemplateName, String> mapping = new HashMap<MessageTemplateName, String>();

    static {
        // 对应的模版名字
        mapping.put(PAY_SUCCESS, "paySuccessSendSMs.vm");
        mapping.put(ORDER_CONFIRM, "orderConfirm.vm");
        mapping.put(ACCOUNT_ACTIVATE, "changePasswordMail.vm");
        mapping.put(PASSWORD_RECOVERY, "activeMail.vm");
        mapping.put(ACCOUNT_INFORM, "messageMail.vm");
        mapping.put(CALL_ORDER, "callOrder.vm");
        mapping.put(BACKGOODS_AUDIT_PASS, "backGoodsAuditPass.vm");
        mapping.put(REFUND_SUCCESS, "refundSuccess.vm");
        mapping.put(DELIVER, "onDelivery.vm");
        mapping.put(SEND_POINT, "email.vm");
        mapping.put(VALUATION_SEND_POINT, "valuationSendPoint.vm");
        mapping.put(EXPIRE_COUPON_REMIND, "expireCouponRemind.vm");
    }

    public String toDesc() {
        return mapping.get(this);
    }


}
