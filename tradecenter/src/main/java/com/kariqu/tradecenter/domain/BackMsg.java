package com.kariqu.tradecenter.domain;

import java.util.HashMap;
import java.util.List;

/**
 * 第三方物流查询平台返回的 json 字符串对应的 Java 对象.<br/>
 *
 * @author Athens(刘杰)
 * @Time 2012-09-25 10:23
 */
public class BackMsg {

    /** 轮询状态 (polling:轮询中, shutdown:结束, abort:中止, updateall:重新推送) */
    private String status;

    /** 快递单当前状态: (got:货物已寄出, sending:派件, check:签收) */
    private String billstatus;

    /** 轮询状态相关消息, 如: 3天查无结果, 7天无变化 */
    private String message;

    /** 最新查询结果 */
    private BackResult lastResult;

    /** 轮询状态 (polling:轮询中, shutdown:结束, abort:中止, updateall:重新推送) */
    public String getStatus() {
        return status;
    }

    /** 轮询状态 (polling:轮询中, shutdown:结束, abort:中止, updateall:重新推送) */
    public void setStatus(String status) {
        this.status = status;
    }

    /** 快递单当前状态: (got:货物已寄出, sending:派件, check:签收) */
    public String getBillstatus() {
        return billstatus;
    }

    /** 快递单当前状态: (got:货物已寄出, sending:派件, check:签收) */
    public void setBillstatus(String billstatus) {
        this.billstatus = billstatus;
    }

    /** 轮询状态相关消息, 如: 3天查无结果, 7天无变化 */
    public String getMessage() {
        return message;
    }

    /** 轮询状态相关消息, 如: 3天查无结果, 7天无变化 */
    public void setMessage(String message) {
        this.message = message;
    }

    /** 最新查询结果 */
    public BackResult getLastResult() {
        return lastResult;
    }

    /** 最新查询结果 */
    public void setLastResult(BackResult lastResult) {
        this.lastResult = lastResult;
    }

}