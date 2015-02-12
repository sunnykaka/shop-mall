package com.kariqu.om.domain;

import java.util.Date;

/**
 * SEO推广
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:12
 */
public class Seo {
    private int id;
    /**
     * 推广所属ID
     * PS：categoryId,productId,pageId
     */
    private String seoObjectId;
    /**
     * 推广所属类型
     * PS：类目，商品详情，自定义页面
     */
    private SeoType seoType;
    private String title;
    private String description;
    private String keywords;
    private Date createDate;
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeoObjectId() {
        return seoObjectId;
    }

    public void setSeoObjectId(String seoObjectId) {
        this.seoObjectId = seoObjectId;
    }

    public SeoType getSeoType() {
        return seoType;
    }

    public void setSeoType(SeoType seoType) {
        this.seoType = seoType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
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

    @Override
    public String toString() {
        return "SEO:[id:" + id + ",seoObjectId:" + seoObjectId + ",seoType" + seoType + ",title:" + title + ",description:" + description + ",keywords:" + keywords+"]";
    }
}
