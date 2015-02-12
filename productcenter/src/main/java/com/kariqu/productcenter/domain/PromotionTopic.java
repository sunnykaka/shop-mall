package com.kariqu.productcenter.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 168活动主题
 * User: Baron.Zhang
 * Date: 2014/10/14
 * Time: 10:56
 */
public class PromotionTopic {

    private int id;
    private String name;
    private String description;
    /** 状态，0为正常，2为已删除 */
    private int status;
    /** 活动开始时间 */
    private Date startTime;
    /** 活动结束时间 */
    private Date endTime;
    /** 记录新增的时间 */
    private Date createTime;
    /** 记录最近修改的时间，如果为新增后未改动则保持与createTime一致 */
    private Date updateTime;
    /** 活动标语 */
    private String slogan;
    /** 活动预览图 */
    private String previewImgUrl;
    private String secretImgUrl;

    private List<Promotion> promotionList = new ArrayList<Promotion>();

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }

    public String getSecretImgUrl() {
        return secretImgUrl;
    }

    public void setSecretImgUrl(String secretImgUrl) {
        this.secretImgUrl = secretImgUrl;
    }

    public List<Promotion> getPromotionList() {
        return promotionList;
    }

    public void setPromotionList(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }
}
