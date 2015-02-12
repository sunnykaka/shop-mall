package com.kariqu.productcenter.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品对象，商品是一个抽象的对象，真正对应物理商品的是SKU
 *
 * @Author: Tiger
 * @Since: 11-6-26 下午1:49
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class Product {

    private int id;

    private int customerId;

    private int brandId;

    private String productCode;

    /**
     * 所在的后台类目
     */
    private int categoryId;

    private String name;

    /** 推荐理由 */
    private String description;

    /**
     * 一个商品对应一个或者多个SKU对象,一个SKU对应一个物理单品
     */
    private List<StockKeepingUnit> stockKeepingUnits;

    /**
     * 库存策略(0表示普通模式, 1表示付款成功后扣减模式), 默认为 0
     */
    private StoreStrategy storeStrategy;

    /**
     * 新品或热销
     */
    private TagType tagType;

    //该商品是否上架
    private boolean online;


    //创建时间
    private Date createTime;

    //上架时间
    private Date onlineTime;

    //上架时间的long型表示，搜索引擎读取
    private long onLineTimeLong;

    //下架时间
    private Date offlineTime;

    //更新时间可用于搜索引擎重dump数据
    private Date updateTime;

    /**
     * 筛选某个SKU
     *
     * @param skuId
     * @return
     */
    public StockKeepingUnit getStockKeepingUnit(long skuId) {
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            if (stockKeepingUnit.getId() == skuId) {
                return stockKeepingUnit;
            }
        }
        return null;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

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

    /** 推荐理由 */
    public String getDescription() {
        return description;
    }

    /** 推荐理由 */
    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<StockKeepingUnit> getStockKeepingUnits() {
        return stockKeepingUnits;
    }

    public void setStockKeepingUnits(List<StockKeepingUnit> stockKeepingUnits) {
        this.stockKeepingUnits = stockKeepingUnits;
    }

    /**
     * 库存策略(0表示普通模式, 1表示付款成功后扣减模式), 默认为 0
     */
    public StoreStrategy getStoreStrategy() {
        return storeStrategy;
    }

    /**
     * 库存策略(0表示普通模式, 1表示付款成功后扣减模式), 默认为 0
     */
    public void setStoreStrategy(StoreStrategy storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Date onlineTime) {
        this.onlineTime = onlineTime;
        if (this.onlineTime != null) {
            this.onLineTimeLong = this.onlineTime.getTime();
        }
    }

    public long getOnLineTimeLong() {
        return onLineTimeLong;
    }

    public void setOnLineTimeLong(long onLineTimeLong) {
        this.onLineTimeLong = onLineTimeLong;
    }

    public Date getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(Date offlineTime) {
        this.offlineTime = offlineTime;
    }

    public enum TagType {
        /**
         * 默认 既不是新品也不是热销
         */
        DEFAULT("无"),
        /**
         * 新品
         */
        NEW("新品"),
        /**
         * 热销
         */
        HOT("热销");
        private static Map<String, TagType> mapping = new HashMap<String, TagType>();

        static {
            mapping.put("无", DEFAULT);
            mapping.put("新品", NEW);
            mapping.put("热销", HOT);
        }

        private String value;

        public static TagType get(String name) {
           return mapping.get(name);
        }
        TagType(String value) {
            this.value = value;
        }

        public String toDesc() {
            return value;
        }
    }

}