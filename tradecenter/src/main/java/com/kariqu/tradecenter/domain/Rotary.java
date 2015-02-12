package com.kariqu.tradecenter.domain;

import com.kariqu.common.DateUtils;
import com.kariqu.productcenter.domain.Money;

import java.util.Date;
import java.util.List;

/**
 * 抽奖轮盘
 *
 * @author Athens(刘杰)
 */
public class Rotary {

    private int id;

    /**
     * 轮盘名
     */
    private String name;

    /**
     * 详细说明
     */
    private String description;

    /**
     * 每次抽奖消耗的点数
     */
    private int tally;

    /**
     * 抽奖规则
     */
    private String rule;

    /**
     * 详细规则
     */
    private String detailRule;

    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 过期时间
     */
    private Date expireDate;

    private Date createDate;
    private Date updateDate;

    private List<RotaryMeed> rotaryMeedList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 将抽奖需要的积分
     *
     * @return
     */
    public String getCurrency() {
        return Money.getMoneyString(tally);
    }

    /**
     * 将积分转换成抽奖需要的积分
     *
     * @param currency
     */
    public void setCurrency(String currency) {
        this.tally = (int) Money.YuanToCent(currency);
    }

    public int getTally() {
        return tally;
    }

    public void setTally(int tally) {
        this.tally = tally;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getDetailRule() {
        return detailRule;
    }

    public void setDetailRule(String detailRule) {
        this.detailRule = detailRule;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStart(String start) {
        startDate = DateUtils.parseDate(start, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public String getStart() {
        return DateUtils.formatDate(startDate, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public void setEnd(String end) {
        expireDate = DateUtils.parseDate(end, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public String getEnd() {
        return DateUtils.formatDate(expireDate, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
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

    public List<RotaryMeed> getRotaryMeedList() {
        return rotaryMeedList;
    }

    public void setRotaryMeedList(List<RotaryMeed> rotaryMeedList) {
        this.rotaryMeedList = rotaryMeedList;
    }
}
