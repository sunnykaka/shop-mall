package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.Money;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Date;

/**
 * 退款交易单
 * User: Asion
 * Date: 13-2-20
 * Time: 上午11:47
 */
public class RefundTrade {

    private int id;

    //退款批次号
    private String batchNo;

    //批量退款
    private int successNum;

    //真实退款数目
    private String realRefund;

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

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public String getRealRefund() {
        return realRefund;
    }

    public String getRefundPrice() {
        return Money.getMoneyString(NumberUtils.toLong(realRefund));
    }

    public void setRealRefund(String realRefund) {
        this.realRefund = realRefund;
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
