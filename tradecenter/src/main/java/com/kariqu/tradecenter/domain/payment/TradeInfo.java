package com.kariqu.tradecenter.domain.payment;

import com.kariqu.common.DateUtils;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.payment.PayType;
import com.kariqu.tradecenter.payment.ResponseType;
import com.kariqu.tradecenter.payment.alipay.AlipayUtil;
import com.kariqu.tradecenter.payment.tenpay.TenpayUtils;
import com.kariqu.tradecenter.service.impl.PayMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Map;

/**
 * 交易信息
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午2:44
 */
public class TradeInfo {

    private static final Log log = LogFactory.getLog(TradeInfo.class);

    private int id;

    /**
     * 交易号
     */
    private String tradeNo;

    /**
     * 第三方交易号
     */
    private String outerTradeNo;

    /**
     * 卖家在支付平台的ID
     */
    private String outerBuyerId;

    /**
     * 买家在支付平台的账户
     */
    private String outerBuyerAccount;

    private String outerPlatformType;

    /**
     * 三方平台的交易方式，例如及时到账，网银网关，快捷支付
     */
    private String payMethod;

    /**
     * 交易金额
     */
    private long payTotalFee;

    /**
     * 第三方消息ID
     */
    private String notifyId;

    /**
     * 第三方消息类型
     */
    private String notifyType;

    /**
     * 交易状态
     */
    private String tradeStatus;

    private Date tradeGmtCreateTime;

    private Date tradeGmtModifyTime;

    private Date gmtCreateTime;

    private Date gmtModifyTime;

    /**
     * 业务方式：订单or优惠券（交易的方式)
     * 修改人：Json
     * 修改时间：  2013。12.06，18：14
     */
    private String bizType;

    /**
     * 是那家银行
     * 修改人：Json
     * 修改时间：2013。12.06，18：14
     */
    private String defaultbank;

    /** 只用于后台展示 */
    private String orderNo;

    /**
     * 修改人：Json
     * 修改时间：  2013。12.06，18：14
     * why：添加了两个字段 bizType，defaultBank
     *
     * @return
     */
    @Override
    public String toString() {
        return "TradeInfo{" +
                "id=" + id +
                ", tradeNo='" + tradeNo + '\'' +
                ", outerTradeNo='" + outerTradeNo + '\'' +
                ", outerBuyerId='" + outerBuyerId + '\'' +
                ", outerBuyerAccount='" + outerBuyerAccount + '\'' +
                ", payMethod='" + payMethod + '\'' +
                ", payTotalFee=" + payTotalFee +
                ", notifyId='" + notifyId + '\'' +
                ", notifyType='" + notifyType + '\'' +
                ", tradeStatus='" + tradeStatus + '\'' +
                ", tradeGmtCreateTime=" + tradeGmtCreateTime +
                ", tradeGmtModifyTime=" + tradeGmtModifyTime +
                ", gmtCreateTime=" + gmtCreateTime +
                ", gmtModifyTime=" + gmtModifyTime +
                ", outerPlatformType=" + outerPlatformType +
                ", bizType=" + bizType +
                ", defaultBank=" + defaultbank +
                '}';
    }

    /**
     * 交易签名是否真实
     */
    public boolean verify(Map<String, String> backParams, ResponseType type) {
        if (PayType.Alipay.getValue().equalsIgnoreCase(outerPlatformType)) {
            return AlipayUtil.verify(type.getValue(), backParams);
        }
        if (PayType.TenPay.getValue().equalsIgnoreCase(outerPlatformType)) {
            return TenpayUtils.verify(type.getValue(), backParams);
        }
        return false;
    }

    /**
     * 是否成功扣款
     */
    public boolean isSuccess() {
        if (PayType.Alipay.getValue().equalsIgnoreCase(outerPlatformType)) {
            if ("TRADE_FINISHED".equalsIgnoreCase(tradeStatus) || "TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)) {
                return true;
            }
        }
        if (PayType.TenPay.getValue().equalsIgnoreCase(outerPlatformType)) {
            if ("0".equalsIgnoreCase(tradeStatus)) {
                return true;
            }
        }
        return false;
    }

    public String getOuterPlatformType() {
        return outerPlatformType;
    }

    public void setOuterPlatformType(String outerPlatformType) {
        this.outerPlatformType = outerPlatformType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public String getOuterBuyerId() {
        return outerBuyerId;
    }

    public void setOuterBuyerId(String outerBuyerId) {
        this.outerBuyerId = outerBuyerId;
    }

    public String getOuterBuyerAccount() {
        return outerBuyerAccount;
    }

    public void setOuterBuyerAccount(String outerBuyerAccount) {
        this.outerBuyerAccount = outerBuyerAccount;
    }


    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public long getPayTotalFee() {
        return payTotalFee;
    }
    public String getPayTotalFeeTOString(){
       return Money.getMoneyString(payTotalFee);
    }

    public String getPayTotal() {
        return Money.getMoneyString(payTotalFee);
    }

    public void setPayTotalFee(long payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeDate() {
        return DateUtils.formatDate(gmtCreateTime, DateUtils.DateFormatType.DATE_FORMAT_STR);
    }

    public Date getTradeGmtCreateTime() {
        return tradeGmtCreateTime;
    }

    public void setTradeGmtCreateTime(Date tradeGmtCreateTime) {
        this.tradeGmtCreateTime = tradeGmtCreateTime;
    }

    public Date getTradeGmtModifyTime() {
        return tradeGmtModifyTime;
    }

    public void setTradeGmtModifyTime(Date tradeGmtModifyTime) {
        this.tradeGmtModifyTime = tradeGmtModifyTime;
    }

    public Date getGmtCreateTime() {
        return gmtCreateTime;
    }

    public void setGmtCreateTime(Date gmtCreateTime) {
        this.gmtCreateTime = gmtCreateTime;
    }

    public Date getGmtModifyTime() {
        return gmtModifyTime;
    }

    public void setGmtModifyTime(Date gmtModifyTime) {
        this.gmtModifyTime = gmtModifyTime;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getDefaultbank() {
        return defaultbank;
    }

    public void setDefaultbank(String defaultbank) {
        this.defaultbank = defaultbank;
    }

    public String getPayType() {
        try {
            if (outerPlatformType.equalsIgnoreCase(defaultbank))
                return Enum.valueOf(PayMethod.class, payMethod).toDesc();

            return Enum.valueOf(PayBank.class, defaultbank.substring(0, 1).toUpperCase() + defaultbank.substring(1)).toDesc();
        } catch (Exception e) {
            log.warn("转换时出错! defaultbank: " + defaultbank + ", payMethod: " + payMethod);
            return defaultbank;
        }
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
