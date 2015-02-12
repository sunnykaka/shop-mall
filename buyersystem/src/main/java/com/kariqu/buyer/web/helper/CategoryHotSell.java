package com.kariqu.buyer.web.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类目热销排行榜
 *
 * create by Athens on 14-4-21
 */
public class CategoryHotSell {

    /** 前台类目 */
    //private NavigateCategory navigateCategory;

    /** 前台类目id */
    private int navId;

    /** 前台类目名 */
    private String navName;

    /** 类目对应的商品信息 */
    private List<Map<String, String>> hotSellProductList = new ArrayList<Map<String, String>>();

    public int getNavId() {
        return navId;
    }

    public void setNavId(int navId) {
        this.navId = navId;
    }

    public String getNavName() {
        return navName;
    }

    public void setNavName(String navName) {
        this.navName = navName;
    }

    public List<Map<String, String>> getHotSellProductList() {
        return hotSellProductList;
    }

    public void addHotSellProductList(Map<String, String> hotSellProduct) {
        hotSellProductList.add(hotSellProduct);
    }
}
