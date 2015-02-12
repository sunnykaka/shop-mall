package com.kariqu.designcenter.domain.open.module;

import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.productcenter.domain.*;

import java.util.*;

/**
 * 开放接口商品对象，封装了商品中心的商品对象，它是商品信息的聚合体，整合了各种数据,类似搜索引擎的dump聚合体
 * 注意：商品Product是一个抽象概念，每个SKU才真正对应物理商品
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-3 下午08:43:39
 */
public class Product extends BasicProduct {

    /** 关键属性对 */
    private List<PropertyValuePair> keyProperty = new LinkedList<PropertyValuePair>();

    /** 存储多值的销售属性，这样在详情页可以显示和筛选出具体SKU */
    private Map<Property, List<SKuValue>> skuPickMap;

    /** 根据属性笛卡尔定位一个SKU{'5325252532532,523523532,52352353252,523532523':{id:122122,price:20.00,stock:20}} */
    private Map<String, Product.SKU> stockKeepingUnitMap = new LinkedHashMap<String, SKU>();

    /** 用sku id为key的map,保存product.sku */
    private Map<Long, Product.SKU> skuDetailHolder = new LinkedHashMap<Long, SKU>();

    /** 商品图片对象 */
    protected PictureDesc pictureDesc;

    /** 商品品牌 */
    private String brandName;

    /** 图文混排描述 */
    protected HtmlDesc htmlDesc;

    /** 商品的保养注意事项 */
    private List<String> maintainInfo = new LinkedList<String>();

    /** 商品的使用注意事项 */
    private List<String> useInfo = new LinkedList<String>();

    public Product(com.kariqu.productcenter.domain.Product originalProduct, PictureDesc pictureDesc, HtmlDesc htmlDesc) {
        super(originalProduct, pictureDesc.getMainPicture().getPictureUrl());
        this.htmlDesc = htmlDesc;
        this.pictureDesc = pictureDesc;
    }

    /** 商品的主要大图列表 */
    public List<ProductPicture> getProductPictureList() {
        return pictureDesc.getPictures();
    }

    /** 商品品牌 */
    public String getBrandName() {
        return brandName;
    }

    /** 商品品牌 */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void addUseInfo(String info) {
        this.useInfo.add(info);
    }

    public void addMaintainInfo(String info) {
        this.maintainInfo.add(info);
    }


    /**
     * 从商品的sku里选一个缺省的sku作为商品详情页的默认商品选定
     *
     * @return
     */
    public String getDefaultSkuJson() {
        return JsonUtil.objectToJson(defaultSkuObject);
    }


    /**
     * 商品描述
     *
     * @return
     */
    public String getHtml() {
        List<Html> htmlList = htmlDesc.getHtmlList();
        if (htmlList != null && htmlList.size() > 0) {
            return htmlList.get(0).getContent();
        }
        return "没有商品描述";
    }

    /**
     * skuJson
     *
     * @return
     */
    public String getSkuJson() {
        return JsonUtil.objectToJson(this.stockKeepingUnitMap);
    }

    /**
     * 提供一个合并pidvid的方法供velocity访问
     *
     * @param pid
     * @param vid
     * @return
     */
    public long mergeSkuPicPV(int pid, int vid) {
        return PropertyValueUtil.mergePidVidToLong(pid, vid);
    }


    public void addKeyPropertyValue(String property, String value) {
        keyProperty.add(new PropertyValuePair(property, value));
    }


    public void putSku(SKU sku) {
        String key = Arrays.toString(sku.getPvList()).replace(" ","").replace("[", "").replace("]", "");

        this.stockKeepingUnitMap.put(key, sku);
        this.skuDetailHolder.put(sku.getId(), sku);
    }

    public SKU getSkuDetail(long id) {
        return skuDetailHolder.get(id);
    }

    /**
     * sku筛选位置的临时值对象
     */
    public static class SKuValue extends Value {

        private String imgUrl;

        private String description;

        private int priority;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }

    /**
     * 属性对
     */
    public static class PropertyValuePair {

        private String property;

        private String value;

        public PropertyValuePair(String property, String value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    public List<PropertyValuePair> getKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(List<PropertyValuePair> keyProperty) {
        this.keyProperty = keyProperty;
    }

    public Map<Property, List<SKuValue>> getSkuPickMap() {
        return skuPickMap;
    }

    public void setSkuPickMap(Map<Property, List<SKuValue>> skuPickMap) {
        this.skuPickMap = skuPickMap;
    }


    public Map<String, SKU> getStockKeepingUnitMap() {
        return stockKeepingUnitMap;
    }

    public void setHtmlDesc(HtmlDesc htmlDesc) {
        this.htmlDesc = htmlDesc;
    }

    public List<String> getMaintainInfo() {
        return maintainInfo;
    }

    public void setMaintainInfo(List<String> maintainInfo) {
        this.maintainInfo = maintainInfo;
    }

    public List<String> getUseInfo() {
        return useInfo;
    }

    public void setUseInfo(List<String> useInfo) {
        this.useInfo = useInfo;
    }


}
