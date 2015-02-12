package com.kariqu.productcenter.domain;

import com.kariqu.common.json.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 限时折扣.<br/><br/>
 *   限时折扣是基于商品的. 并提供了更颗粒度的 sku 价格修改.<br/>
 *   限时折扣有类型(优惠百分比还是直接优惠金额), 并设置有开始结束时间(与 db 的创建更新时间不同).<br/>
 *   sku 价格的相关信息在创建时计算好, 以 json 的形式保存在 skuDetailsJson 数据库字段中.<br/>
 *   内部有提供 json 转换实体类的方法.<br/><br/>
 *   在显示 sku价格 及提交订单时, 若限时折扣信息有进行过修改(isUpdate()), 则有必要去获取最新的设置过的 sku 价格.
 *
 * @author Athens(刘杰)
 * @Time 2013-03-28 16:26
 * @since 1.0.0
 */
public class LimitedTimeDiscount {

    private long id;

    /**
     * 商品Id
     */
    private int productId;

    /**
     * 商品下的所有 SKU 对应的 id 及价格. 以 json 数据进行存储
     */
    private String skuDetailsJson;

    /**
     * 折扣类型
     */
    private DiscountType discountType;

    /**
     * 折扣数值
     */
    private long discount;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    public static enum DiscountType {
        /**
         * 比例
         */
        Ratio {
            @Override
            long calculatePrice(long price, long discount) {
                if (discount >= 100) {
                    throw new RuntimeException("比例若设置此值, 价格会高于原价, no!");
                }
                if (discount <= 0) {
                    throw new RuntimeException("比例若设置此值, 价格会出现负数. 别这样!");
                }
                return precision(price * discount / 100);
            }
        },

        /**
         * 钱
         */
        Money {
            @Override
            long calculatePrice(long price, long discount) {
                if ((price - discount) <= 0) {
                    throw new RuntimeException("设置此值后, 价格会出现负数, 表酱紫!");
                }
                return precision(price - discount);
            }
        };

        /**
         * 限时折扣精度, 到 元 则将价格先除以100再乘以100, 若精确到 角 则先除以10再乘以10即可.
         *
         * @param price
         * @return
         */
        private static long precision(long price) {
            return price;
        }

        /**
         * 计算价格
         *
         * @param price
         * @param discount
         * @return
         */
        abstract long calculatePrice(long price, long discount);
    }

    public static class SkuDetail {
        private long skuId;
        private long skuPrice;

        public long getSkuId() {
            return skuId;
        }

        public void setSkuId(long skuId) {
            this.skuId = skuId;
        }

        public long getSkuPrice() {
            return skuPrice;
        }

        public void setSkuPrice(long skuPrice) {
            this.skuPrice = skuPrice;
        }
    }


    /**
     * 返回SKU列表中最小的价格
     *
     * @return
     */
    public String getSkuMinPrice() {
        List<SkuDetail> skuDetails = json2Details();
        long find = skuDetails.get(0).getSkuPrice();
        if (skuDetails.size() > 1) {
            for (int i = 1; i < skuDetails.size(); i++) {
                SkuDetail skuDetail = skuDetails.get(i);
                if (skuDetail.getSkuPrice() < find) {
                    find = skuDetail.getSkuPrice();
                }
            }
        }
        return Money.getMoneyString(find);
    }

    /**
     * 计算折扣后的价格, 若出现非正数则抛出 NullPointException
     *
     * @param oldPrice
     * @return
     */
    public long calculatePrice(long oldPrice) {
        return discountType.calculatePrice(oldPrice, discount);
    }

    /**
     * 计算折扣后的价格, 若不正常则返回原值
     *
     * @param oldPrice
     * @return
     */
    public String calculatePriceWithStr(long oldPrice) {
        try {
            oldPrice = calculatePrice(oldPrice);
        } catch (NullPointerException e) {
            // 忽略. 若异常则使用原值
        }
        return Money.getMoneyString(oldPrice);
    }

    /**
     * json 字符串转换成 sku 对应的 Id 和 价格
     */
    public List<SkuDetail> json2Details() {
        return Arrays.asList(JsonUtil.json2Object(skuDetailsJson, SkuDetail[].class));
    }

    /**
     * sku 对应的 Id 和 价格 转换成 json 字符串
     */
    public String details2Json(List<SkuDetail> skuDetails) {
        return JsonUtil.objectToJson(skuDetails);
    }

    /**
     * 获取设置的 sku 限时折扣价
     */
    public long skuDiscountPrice(long skuId) {
        for (SkuDetail skuDetail : json2Details()) {
            if (skuId == skuDetail.getSkuId())
                return skuDetail.getSkuPrice();
        }
        return 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * 商品下的所有 SKU 对应的 id 及价格. 以 json 数据进行存储
     */
    public String getSkuDetailsJson() {
        return skuDetailsJson;
    }

    /**
     * 商品下的所有 SKU 对应的 id 及价格. 以 json 数据进行存储
     */
    public void setSkuDetailsJson(String skuDetailsJson) {
        this.skuDetailsJson = skuDetailsJson;
    }

    public void setSkuDetailsJsonByDetail(List<SkuDetail> skuDetailList) {
        this.skuDetailsJson = details2Json(skuDetailList);
    }

    public void setSkuDetailsJsonBySku(List<StockKeepingUnit> skuList) {
        List<LimitedTimeDiscount.SkuDetail> skuDetailList = new ArrayList<SkuDetail>();
        for (StockKeepingUnit sku : skuList) {
            LimitedTimeDiscount.SkuDetail skuDetail = new LimitedTimeDiscount.SkuDetail();
            skuDetail.setSkuId(sku.getId());
            // 创建时计算折扣后的价格
            skuDetail.setSkuPrice(calculatePrice(sku.getPrice()));

            skuDetailList.add(skuDetail);
        }
        setSkuDetailsJsonByDetail(skuDetailList);
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public long getDiscount() {
        return discount;
    }

    public void setDiscount(long discount) {
        this.discount = discount;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isUpdate() {
        return updateDate.after(createDate);
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

}
