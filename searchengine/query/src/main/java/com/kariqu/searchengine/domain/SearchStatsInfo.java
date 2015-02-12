package com.kariqu.searchengine.domain;

import com.google.common.collect.Maps;
import com.kariqu.categorycenter.client.domain.PropertyValueStatsInfo;
import com.kariqu.categorycenter.domain.model.Property;

import java.util.*;

/**
 * 分析搜索引擎返回结果得到的对象
 * 搜索引擎返回的数据有：
 * 1，商品列表
 * 2，按照后台叶子类目的统计，比如笔记本有20个
 * 3，按照商品属性的统计，比如颜色红色的10个
 * 如果搜到的商品在同一个类目下，则有显示属性统计的必要，如果是多个类目，则没有
 * 如果是按照前台类目来计算，在出现多个类目的时候要判断这多个类目是否被一个前台类目聚合，如果是也可显示
 * 这个时候属性统计是这多个后台类目的公共属性
 * User: Asion
 * Date: 11-8-8
 * Time: 下午4:38
 */
public class SearchStatsInfo {

    /** 表示是否搜到的商品都在同一个类目下 */
    private boolean singleCategory = false;

    /** key是类目属性名称，比如品牌,值是一个列表比如三星(100),苹果(20) */
    private Map<Property, List<PropertyValueStatsInfo>> searchFilterMap = Maps.newLinkedHashMap();

    public void putPropertyValueStaticInfo(Property property, PropertyValueStatsInfo propertyValueStatInfo) {
        if (searchFilterMap.get(property) == null) {
            searchFilterMap.put(property, new LinkedList<PropertyValueStatsInfo>());
        }
        List<PropertyValueStatsInfo> propertyValueStatsInfos = searchFilterMap.get(property);
        propertyValueStatsInfos.add(propertyValueStatInfo);
    }

    /**
     * 统计属性值
     *
     * @param propertyMaxNumber 最大属性个数
     * @param valueMaxNumber 最大属性值个数
     * @param propertyArrayName 强制要求出现(若有)的属性(以传入的先后进行排序)
     * @return
     */
    public SearchStatsInfo selectPropertyAndValue(int propertyMaxNumber, int valueMaxNumber, List<String> propertyArrayName) {
        if (propertyMaxNumber <= 0) propertyMaxNumber = 6;
        if (valueMaxNumber <= 0) valueMaxNumber = 10;

        Map<Property, List<PropertyValueStatsInfo>> pvsiMap = Maps.newLinkedHashMap();
        // 先是强制要求出现的属性
        for (String propertyName : propertyArrayName) {
            for (Map.Entry<Property, List<PropertyValueStatsInfo>> entry : searchFilterMap.entrySet()) {
                if (propertyName.equals(entry.getKey().getName())) {
                    List<PropertyValueStatsInfo> pvsi = entry.getValue();
                    if (pvsi.size() > valueMaxNumber) {
                        pvsi = pvsi.subList(0, valueMaxNumber);
                    }

                    pvsiMap.put(entry.getKey(), pvsi);
                }
            }
        }
        // 补充剩下的属性
        for (Map.Entry<Property, List<PropertyValueStatsInfo>> entry : searchFilterMap.entrySet()) {
            // 不包含在一定要出现的属性里, 且总属性没有达到指定的个数就加进去
            if (!propertyArrayName.contains(entry.getKey().getName()) && pvsiMap.size() < propertyMaxNumber) {
                List<PropertyValueStatsInfo> pvsi = entry.getValue();
                if (pvsi.size() > valueMaxNumber) {
                    pvsi = pvsi.subList(0, valueMaxNumber);
                }

                pvsiMap.put(entry.getKey(), pvsi);
            }
        }
        searchFilterMap = pvsiMap;
        return this;
    }

    public boolean isSingleCategory() { return singleCategory; }
    public void setSingleCategory(boolean singleCategory) {
        this.singleCategory = singleCategory;
    }

    public Map<Property, List<PropertyValueStatsInfo>> getSearchFilterMap() {
        return searchFilterMap;
    }

}
