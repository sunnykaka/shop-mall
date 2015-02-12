package com.kariqu.designcenter.domain.model;

import java.io.Serializable;

/**
 * 页面类型
 * 一个电子商务网站的主要页面就是首页，搜索结果页和产品详情页，他们在系统中只有一个
 * 其他页面则可能很多
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-10 上午11:05:11
 */
public enum PageType implements Serializable {

    /** 首页 */
    index("首页"),

    /** 搜索结果页 */
    searchList("列表页"),

    /** 产品详情页面 */
    detail("详情页"),

    /** 产品详情页面 */
    detailIntegral("积分兑换详情页"),

    /** 频道页 */
    channel("频道页"),

    /** 活动首页 */
    marketing("活动首页"),

    /** 套餐详情页 */
    meal_detail("套餐详情页"),

    /** 其他类型的页面 */
    other("其他页面");

    private String value;
    private PageType(String value) {
        this.value = value;
    }

    public String toDesc() {
        return value;
    }

}
