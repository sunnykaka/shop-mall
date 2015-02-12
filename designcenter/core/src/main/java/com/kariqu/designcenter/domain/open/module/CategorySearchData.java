package com.kariqu.designcenter.domain.open.module;

import com.kariqu.categorycenter.domain.model.Property;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 列表页筛选数据包装
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-10-23
 *        Time: 上午11:22
 */
public class CategorySearchData {

    private Map<Property, List<SearchValue>> searchFilterMap = new LinkedHashMap<Property, List<SearchValue>>();

    public Map<Property, List<SearchValue>> getSearchFilterMap() {
        return searchFilterMap;
    }

    /**
     * 添加一个属性和值的映射
     *
     * @param property
     * @param searchValueList
     */
    public void putCategorySearchInfo(Property property, List<SearchValue> searchValueList) {
        searchFilterMap.put(property, searchValueList);
    }

    /**
     * 可搜属性值
     */
    public static class SearchValue {

        public SearchValue(String value, long pidvid) {
            this.value = value;
            this.pidvid = pidvid;
        }

        String value;
        
        long pidvid;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public long getPidvid() {
            return pidvid;
        }

        public void setPidvid(long pidvid) {
            this.pidvid = pidvid;
        }
    }


}
