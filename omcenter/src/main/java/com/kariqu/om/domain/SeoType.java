package com.kariqu.om.domain;

/**
 * SEO推广类型
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:15
 */
public enum SeoType {
    PAGE {
        @Override
        public String toDesc() {
            return "页面";
        }
    },
    CATEGORY {
        @Override
        public String toDesc() {
            return "类目";
        }
    },
    CHANNEL {
        @Override
        public String toDesc() {
            return "频道";
        }
    },
    PRODUCT {
        @Override
        public String toDesc() {
            return "商品";
        }
    };

    public abstract String toDesc();
}
