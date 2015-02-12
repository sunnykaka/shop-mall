package com.kariqu.tradecenter.domain;

import com.kariqu.common.DateUtils;

import java.util.Date;

public class BackGoodsLog {

    private Long id;

    /**
     * 退货单编号
     */
    private Long backGoodsId;

    /**
     * 操作者
     */
    private String userName;

    /**
     * 做了什么
     */
    private String doWhat;

    /**
     * 操作说明(类似于客服备注)
     */
    private String remark;

    /**
     * 操作时间
     */
    private Date operaTime;

    /**
     * 操作时的退货状态
     */
    private BackGoodsState backState;

    public BackGoodsLog() {
    }

    public BackGoodsLog(BackGoods backGoods, String userName, String doWhat, String remark) {
        this.backGoodsId = backGoods.getId();
        this.userName = userName;
        this.doWhat = doWhat;
        this.remark = remark;
        this.backState = backGoods.getBackState();
    }

    public BackGoodsLog(BackGoods backGoods, BackGoodsState backGoodsState, String userName, String doWhat, String remark) {
        this.backGoodsId = backGoods.getId();
        this.userName = userName;
        this.doWhat = doWhat;
        this.remark = remark;
        this.backState = backGoodsState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(Long backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDoWhat() {
        return doWhat;
    }

    public void setDoWhat(String doWhat) {
        this.doWhat = doWhat;
    }

    /**
     * 操作说明(类似于客服备注)
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 操作说明(类似于客服备注)
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getOperaTime() {
        return operaTime;
    }


    public void setOperaTime(Date operaTime) {
        this.operaTime = operaTime;
    }

    public BackGoodsState getBackState() {
        return backState;
    }

    public void setBackState(BackGoodsState backState) {
        this.backState = backState;
    }

}