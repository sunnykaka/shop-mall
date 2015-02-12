package com.kariqu.tradecenter.domain;

import com.kariqu.common.DateUtils;
import org.apache.commons.httpclient.util.DateUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

/**
 * 中奖信息
 *
 * @author Athens(刘杰)
 */
public class Lottery {

    private int id;

    /**
     * 抽奖Id
     */
    private int rotaryId;

    /**
     * 抽奖的奖品Id
     */
    private int rotaryMeedId;

    /**
     * 中奖人
     */
    private String userName;

    /**
     * 奖品类型(积分还是商品)
     */
    private RotaryMeed.MeedType meedType;

    /**
     * 奖品值(若奖品类型是商品则是 skuId, 若是积分则是积分分数)
     */
    private String meedValue;

    private String value;

    /**
     * 是否真实(true 代表真实, 默认是 false)
     */
    private boolean really = false;

    /**
     * 收货人名字
     */
    private String consigneeName;

    /**
     * 收货人电话
     */
    private String consigneePhone;

    /**
     * 收货人地址
     */
    private String consigneeAddress;

    /**
     * 是否已发货
     */
    private boolean sendOut;

    private Date createDate;
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRotaryId() {
        return rotaryId;
    }

    public void setRotaryId(int rotaryId) {
        this.rotaryId = rotaryId;
    }

    public int getRotaryMeedId() {
        return rotaryMeedId;
    }

    public void setRotaryMeedId(int rotaryMeedId) {
        this.rotaryMeedId = rotaryMeedId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDimUserName() {
        int len = userName.substring(1).length();
        return userName.substring(0, 1) + ((len > 2) ? "***" : userName.substring(1).replaceAll(".", "\\*"));
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RotaryMeed.MeedType getMeedType() {
        return meedType;
    }

    public void setMeedType(RotaryMeed.MeedType meedType) {
        this.meedType = meedType;
    }

    public String getValue() {
        return value;
    }

    public String setValue(String value) {
        return this.value = value;
    }

    public String getMeedValue() {
        return meedValue;
    }

    public void setMeedValue(String meedValue) {
        this.meedValue = meedValue;
    }

    public boolean isReally() {
        return really;
    }

    public void setReally(boolean really) {
        this.really = really;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public boolean isSendOut() {
        return sendOut;
    }

    public void setSendOut(boolean sendOut) {
        this.sendOut = sendOut;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getCreate() {
        return DateUtils.formatDate(createDate, DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
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

}
