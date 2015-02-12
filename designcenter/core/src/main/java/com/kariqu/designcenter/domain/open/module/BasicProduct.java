package com.kariqu.designcenter.domain.open.module;

import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 商品基本对象
 * User: Asion
 * Date: 13-5-20
 * Time: 下午12:36
 */
public class BasicProduct {

    //商品中心的商品模型
    private com.kariqu.productcenter.domain.Product originalProduct;

    /**
     * 是否在做活动
     */
    private boolean marketing;


    /**
     * 销售价格，取缺省SKU的价格
     */
    private String sellPrice;


    /**
     * 原价，取缺省SKU的原价
     */
    private String money;


    /**
     * 评价数量
     */
    private int valuation;


    /**
     * 是否这个商品有库存，通过分析这个商品的SKU得到
     */
    private boolean hasStock = false;

    //缺省的SKU,当通过商品ID进入详情页的时候，默认挑选这个缺省sku来显示
    protected SKU defaultSkuObject;


    /**
     * 判断是否是单SKU，两种情况：没有多值，和只有一个多值
     */
    private boolean singleSku;

    /**
     * 这个商品的所有SKU
     */
    private List<StockKeepingUnit> stockKeepingUnitList;

    //主图地址
    private String mainImg;


    public BasicProduct(com.kariqu.productcenter.domain.Product originalProduct, String mainImg) {
        this.originalProduct = originalProduct;
        this.mainImg = mainImg;
    }


    public int getId() {
        return originalProduct.getId();
    }


    public String getName() {
        return originalProduct.getName();
    }

    public int getCategoryId() {
        return originalProduct.getCategoryId();
    }

    public int getBrandId() {
        return originalProduct.getBrandId();
    }


    public String getProductCode() {
        return originalProduct.getProductCode();
    }

    public String getDescription() {
        return originalProduct.getDescription();
    }


    public SKU getDefaultSkuObject() {
        return defaultSkuObject;
    }

    public void setDefaultSkuObject(SKU sku) {
        this.defaultSkuObject = sku;
        this.marketing = sku.marketing;
        this.sellPrice = sku.getSellPrice();
        this.money = sku.getMoney();
        // 当 sku 有设定图时, 主图使用 sku 上设定的
        if (sku.getSkuImageList() != null && sku.getSkuImageList().size() > 0
                && StringUtils.isNotBlank(sku.getSkuImageList().get(0))) {
            this.mainImg = sku.getSkuImageList().get(0);
        }
    }

    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }


    public boolean isOnline() {
        return originalProduct.isOnline();
    }

    public boolean isMarketing() {
        return marketing;
    }

    public void setMarketing(boolean marketing) {
        this.marketing = marketing;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getValuation() {
        return valuation;
    }

    public void setValuation(int valuation) {
        this.valuation = valuation;
    }

    /**
     * 返回主图的URL
     *
     * @return
     */
    public String getImageUrl() {
        return mainImg;
    }

    public List<StockKeepingUnit> getStockKeepingUnitList() {
        return stockKeepingUnitList;
    }

    public void setStockKeepingUnitList(List<StockKeepingUnit> stockKeepingUnitList) {
        this.stockKeepingUnitList = stockKeepingUnitList;
        if (stockKeepingUnitList.size() == 1) {
            singleSku = true;
        }
        //遍历sku，如果发现有库存，则设置这个商品的库存标志为真
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnitList) {
            if (stockKeepingUnit.getStockQuantity() > 0) {
                hasStock = true;
                break;
            }
        }
    }


    public boolean isSingleSku() {
        return singleSku;
    }

    public void setSingleSku(boolean singleSku) {
        this.singleSku = singleSku;
    }


    public static class SKU {

        private long id;

        /** 市场价 */
        private String marketPrice;

        /** 原价 */
        private String money;

        private int stock;

        private int limit;

        /** 是否在参与活动 */
        private boolean marketing;

        /** 活动价 */
        private String marketingPrice;

        /** sku 图片, 第一张是主图 */
        private List<String> skuImageList;

        /** 活动类型 */
        private ProductActivityType activityType;

        /** 活动结束描述 */
        private String activityDateStr;

        /** sku 对应的积分 */
        private String skuIntegral;

        /** sku 描述 */
        private String skuDesc;

        private long[] pvList = new long[0];

        public String getSellPrice() {
            return marketing ? marketingPrice : money;
        }

        public String getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(String marketPrice) {
            this.marketPrice = marketPrice;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public List<String> getSkuImageList() {
            return skuImageList;
        }

        public void setSkuImageList(List<String> skuImageList) {
            this.skuImageList = skuImageList;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
            this.marketingPrice = money;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public long[] getPvList() {
            return pvList;
        }

        public void setPvList(long[] pvList) {
            this.pvList = pvList;
        }

        public String getMarketingPrice() {
            return marketingPrice;
        }

        public void setMarketingPrice(String marketingPrice) {
            this.marketingPrice = marketingPrice;
        }

        public boolean isMarketing() {
            return marketing;
        }

        public void setMarketing(boolean marketing) {
            this.marketing = marketing;
        }

        public ProductActivityType getActivityType() {
            return activityType;
        }

        public void setActivityType(ProductActivityType activityType) {
            this.activityType = activityType;
        }

        public String getActivityDateStr() {
            return activityDateStr;
        }

        public void setActivityDateStr(String activityDateStr) {
            this.activityDateStr = activityDateStr;
        }

        /** sku 描述 */
        public String getSkuDesc() {
            return skuDesc;
        }

        /** sku 描述 */
        public void setSkuDesc(String skuDesc) {
            this.skuDesc = skuDesc;
        }

        /** sku 对应的积分 */
        public String getSkuIntegral() {
            return skuIntegral;
        }

        /** sku 对应的积分 */
        public void setSkuIntegral(String skuIntegral) {
            this.skuIntegral = skuIntegral;
        }
    }
}
