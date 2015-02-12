package com.kariqu.categorycenter.domain.model;

/**
 * 类目属性类型，它表示某个属性在特定类目下是什么类型，有
 * 关键属性，销售属性等
 * <p/>
 * 销售属性一般是多值，用来在商品详情页筛选SKU
 * 关键属性就是表示商品的参数，也有可能多值，比如锅的适用炉灶：电磁炉，煤气灶
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:10
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public enum PropertyType {

    /* 销售属性 */
    SELL_PROPERTY {
        @Override
        public String getDescription() {
            return "销售属性";
        }
    },

    /* 关键属性 */
    KEY_PROPERTY {
        @Override
        public String getDescription() {
            return "关键属性";
        }
    };

    public abstract String getDescription();

}
