package com.kariqu.searchengine.domain;

import com.google.common.base.Objects;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.ProductActivity;
import com.kariqu.productcenter.domain.ProductActivityType;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * 搜索引擎返回的产品信息对象
 *
 * @Author: Tiger
 * @Since: 11-6-26 下午5:46
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class ProductInfo {

    @Field
    private String id;

    @Field
    private Long price;

    @Field
    private String name;

    /**
     * 活动类型
     */
    @Field
    private String activityType;

    /**
     * 活动价格
     */
    @Field
    private String activityPrice;

    /**
     * 高亮的商品名，加入了一些html字符
     */
    private String highlightName;

    /**
     * 商品主图
     */
    @Field
    private String pictureUrl;

    /**
     * 图片(在三级和搜索页显示, 共有多张)
     */
    @Field
    private List<String> pictureUrlList;

    /*品牌名称*/
    @Field
    private String brandName;

    @Field
    private int leafId;

    /* 销量 */
    @Field
    private int sell;

    /* 上架时间 */
    @Field
    private long time;

    /* 推荐理由 */
    @Field
    private String desc;

    /**
     * 评价数量
     */
    @Field
    private int valuation;
    /*标签类型，比如：最新，热卖*/
    @Field
    private String tagType;
    /*skuID 索引库建索引的最小单位为:sku*/
    @Field
    private String skuId;
    /**
     * 产地*
     */
    @Field
    private String produceArea;

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    /**
     * sku 中文描述*
     */
    @Field
    private String skuDesc;

    /**
     * 显示的价格
     */
    public String getDisplayPrice() {
        return Money.getMoneyString(price);
    }

    //判断是否是新品
    public boolean isNewProduct() {
        if (StringUtils.equals(Product.TagType.NEW.name(), tagType)) {
            return true;
        }
        return false;
    }

    //判断是否是热销产品
    public boolean isHotProduct() {
        if (StringUtils.equals(Product.TagType.HOT.name(), tagType)) {
            return true;
        }
        return false;
    }

    //判断是否是显示折扣产品
    public boolean isLimitProduct() {
        if (StringUtils.equals(ProductActivityType.LimitTime.name(), activityType)) {
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return StringUtils.isBlank(pictureUrl) ? StringUtils.EMPTY : pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<String> getPictureUrlList() {
        return pictureUrlList;
    }

    public void setPictureUrlList(List<String> pictureUrlList) {
        this.pictureUrlList = pictureUrlList;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSell() {
        return sell;
    }

    public void setSell(int sell) {
        this.sell = sell;
    }

    public int getLeafId() {
        return leafId;
    }

    public int getCategoryId() {
        return leafId;
    }

    public void setLeafId(int leafId) {
        this.leafId = leafId;
    }

    public String getHighlightName() {
        if (highlightName == null) {
            return name;
        }
        return highlightName;
    }

    public void setHighlightName(String highlightName) {
        this.highlightName = highlightName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(String activityPrice) {
        this.activityPrice = activityPrice;
    }

    public int getValuation() {
        return valuation;
    }

    public void setValuation(int valuation) {
        this.valuation = valuation;
    }

    public String getProductIdSkuIdJoinStr() {
        return StringUtils.isEmpty(skuId) ? id : (id + "-" + skuId);
    }

    /** 产地 + 品牌 + 商品名 + sku 描述 */
    public String getNameSkuDescJoinStr() {
        StringBuilder sbd = new StringBuilder();
        if (StringUtils.isNotBlank(produceArea)) sbd.append(produceArea);
        if (StringUtils.isNotBlank(brandName)) sbd.append(brandName);
        if (StringUtils.isNotBlank(name)) sbd.append(name);
        if (StringUtils.isNotBlank(skuDesc)) sbd.append(" ").append(skuDesc);

        return sbd.toString();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("price", price)
                .add("name", name)
                .add("activityType", activityType)
                .add("activityPrice", activityPrice)
                .add("highlightName", highlightName)
                .add("pictureUrl", pictureUrl)
                .add("leafId", leafId)
                .add("sell", sell)
                .add("time", time)
                .add("desc", desc)
                .add("valuation", valuation)
                .toString();
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProduceArea() {
        return produceArea;
    }

    public void setProduceArea(String produceArea) {
        this.produceArea = produceArea;
    }
}
