package com.kariqu.buyer.web.helper;

import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;
import com.kariqu.productcenter.domain.Money;

import java.util.List;

/**
 * 套餐渲染对象
 *
 * @author Athens(刘杰)
 * @Time 13-5-5 下午2:49
 */
public class MealView {

    private MealSet meal;
    private List<MealItemView> mealItemViewList;

    /** sku 对应的积分 */
    private String integral;

    public MealSet getMeal() {
        return meal;
    }

    public void setMeal(MealSet meal) {
        this.meal = meal;
    }

    public List<MealItemView> getMealItemViewList() {
        return mealItemViewList;
    }

    public void setMealItemViewList(List<MealItemView> mealItemViewList) {
        this.mealItemViewList = mealItemViewList;
    }

    /** sku 对应的积分 */
    public String getIntegral() {
        return integral;
    }

    /** sku 对应的积分 */
    public void setIntegral(String integral) {
        this.integral = integral;
    }

    /** sku 总价 */
    public String getTotalPrice() {
        return Money.getMoneyString(skuTotalPrice());
    }

    private long skuTotalPrice() {
        long totalPrice = 0l;
        for (MealItemView itemView : mealItemViewList) {
            totalPrice += itemView.skuPrice() * itemView.getMealItem().getNumber();
        }
        return totalPrice;
    }

    /** 套餐总价 */
    public String getMealTotalPrice() {
        return Money.getMoneyString(mealTotalPrice());
    }

    public long mealTotalPrice() {
        long totalPrice = 0l;
        for (MealItemView itemView : mealItemViewList) {
            totalPrice += itemView.mealPrice() * itemView.getMealItem().getNumber();
        }
        return totalPrice;
    }


    /** 节省的价格 */
    public String getStintPrice() {
        String price = Money.getMoneyString(skuTotalPrice() - mealTotalPrice());
        return price.substring(0, price.indexOf("."));
    }

    public static class MealItemView {
        private MealItem mealItem;
        private MealSkuInfo skuInfo;

        public MealItem getMealItem() {
            return mealItem;
        }

        public void setMealItem(MealItem mealItem) {
            this.mealItem = mealItem;
        }

        public MealSkuInfo getSkuInfo() {
            return skuInfo;
        }

        public void setSkuInfo(MealSkuInfo skuInfo) {
            this.skuInfo = skuInfo;
        }

        /** sku 原价 */
        private long skuPrice() {
            return Money.YuanToCent(skuInfo.getSkuPrice());
        }

        /** 套餐价 */
        public String getMealPrice() {
            return Money.getMoneyString(mealPrice());
        }

        private long mealPrice() {
            return mealItem.getSkuPrice();
        }

        public static class MealSkuInfo {
            private String productAndSkuId;
            private String name;
            private String imageUrl;
            private String skuPrice;

            public String getProductAndSkuId() {
                return productAndSkuId;
            }

            public void setProductAndSkuId(String productAndSkuId) {
                this.productAndSkuId = productAndSkuId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getSkuPrice() {
                return skuPrice;
            }

            public void setSkuPrice(String skuPrice) {
                this.skuPrice = skuPrice;
            }
        }
    }

}
