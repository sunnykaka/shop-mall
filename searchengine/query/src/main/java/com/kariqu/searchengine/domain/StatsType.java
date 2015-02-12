package com.kariqu.searchengine.domain;

/**
 * 搜索引擎统计类型
 *
 * @Author: Tiger
 * @Since: 11-6-26 下午3:11
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public enum StatsType {

    /** 按照商品所属的后台类目进行统计,比如笔记本 */
    LEAFID("leafId"),

    /** 按照商品的属性进行统计，比如(品牌：三星) */
    PIDVID("pidvid");

    private String value;
    StatsType(String value) {
        this.value = value;
    }

    public String toField() {
        return value;
    }


}
