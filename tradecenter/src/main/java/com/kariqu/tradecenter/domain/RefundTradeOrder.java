package com.kariqu.tradecenter.domain;

import java.util.Date;

/**
 * 退款交易单
 * User: Asion
 * Date: 13-2-20
 * Time: 上午11:47
 */
public class RefundTradeOrder {

    private int id;

    //退款批次号
    private String batchNo;

    //退货单号
    private long backGoodsId;


    //交易号
    private String outerTradeNo;

    //退款金额
    private long refund;

    //是否成功
    private boolean success;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public long getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(long backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public long getRefund() {
        return refund;
    }

    public void setRefund(long refund) {
        this.refund = refund;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
}
