package com.kariqu.tradecenter.domain;


import com.kariqu.usercenter.domain.Currency;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Date;

/**
 * 抽奖轮盘里的奖品项
 *
 * @author Athens(刘杰)
 */
public class RotaryMeed {

    private int id;

    /**
     * 抽奖活动 Id
     */
    private int rotaryId;

    /**
     * 奖品类型(积分现金券还是商品)
     */
    private MeedType meedType;

    /**
     * 奖品值(类型是商品则 proId-skuId, 现金券则是面值-最小使用金额, 积分则是数值)
     */
    private String meedValue;

    /**
     * 图片
     */
    private String imageUrl;

    /**
     * 描述
     */
    private String description;

    /**
     * 序号(同一个抽奖活动的序号不能重复)
     */
    private int meedIndex;

    /**
     * 中奖概率(万分之比例, 比如: 在 10000 里面抽中 100 次, 则此值设置为 100)
     */
    private int meedProbability;

    private Date createDate;
    private Date updateDate;

    public enum MeedType {
        /**
         * 无奖
         */
        Null("无奖") {
            @Override
            String descValue(String value) {
                return "无";
            }
        },
        /**
         * 商品
         */
        Product("商品") {
            @Override
            String descValue(String value) {
                String[] proArr = value.split("\\-");
                return "商品Id: " + proArr[0] + (proArr.length > 1 ? ", skuId: " + proArr[1] : "");
            }
        },
        /**
         * 现金券
         */
        Coupon("现金券") {
            @Override
            String descValue(String value) {
                String[] couArr = value.split("\\-");
                return "面值: ¥" + couArr[0] + (couArr.length > 1 ? ", 最小使用金额: ¥" + couArr[1] : "");
            }
        },
        /**
         * 积分
         */
        Integral("积分") {
            @Override
            String descValue(String value) {
                // 容错，如果不能转换直接输出
                if (NumberUtils.isNumber(value)) {
                    return "积分: " + Currency.IntegralToCurrency(Long.parseLong(value.trim()));
                } else {
                    return "积分: " + value;
                }
            }
        };

        private String value;

        MeedType(String info) {
            value = info;
        }

        public String toStr() {
            return value;
        }

        abstract String descValue(String value);
    }

    /**
     * 奖品值数据
     */
    public String getValue() {
        return meedType.descValue(meedValue);
    }

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

    public MeedType getMeedType() {
        return meedType;
    }

    public void setMeedType(MeedType meedType) {
        this.meedType = meedType;
    }

    /**
     * 获取时如果积分，转为积分
     */
    public String getCurrency() {
        if (MeedType.Integral.compareTo(meedType) == 0 && NumberUtils.isNumber(meedValue)) {
            return Currency.IntegralToCurrency(Long.parseLong(meedValue));
        } else {
            return meedValue;
        }
    }

    public String getMeedValue() {
        return meedValue;
    }

    /**
     * 如果为积分，转换为积分元
     *
     * @param meedValue
     */
    public void setValue(String meedValue) {
        if (MeedType.Integral.compareTo(meedType) == 0 && NumberUtils.isNumber(meedValue)) {
            this.meedValue = Currency.CurrencyToIntegral(meedValue) + "";
        } else {
            this.meedValue = meedValue;
        }
    }

    public void setMeedValue(String meedValue) {
        this.meedValue = meedValue;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMeedIndex() {
        return meedIndex;
    }

    public void setMeedIndex(int meedIndex) {
        this.meedIndex = meedIndex;
    }

    public int getMeedProbability() {
        return meedProbability;
    }

    /**
     * 中奖概率(万分之比例, 比如: 在 10000 里面抽中 100 次, 则此值设置为 100)
     */
    public void setMeedProbability(int meedProbability) {
        this.meedProbability = meedProbability;
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
