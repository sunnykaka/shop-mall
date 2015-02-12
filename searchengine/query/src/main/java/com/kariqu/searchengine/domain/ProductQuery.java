package com.kariqu.searchengine.domain;


import com.google.common.base.Objects;
import com.kariqu.productcenter.domain.Money;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品查询对象
 *
 * @Author: Tiger
 * @Since: 11-6-26 下午2:41
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class ProductQuery {

    /** 要查询的 关键字 */
    private String keyword;

    /** 要查询的 前台类目id */
    private String cid;

    /** 要查询的 属性值 */
    private String pv;

    /** 页数 */
    private String page;

    /** 每页条数 */
    private String pageSize;

    /** 排序规则, 默认是相关度 */
    private String sort;

    /** 排序顺序, 默认是正序 */
    private String order;

    /** 价格区间起始位置 */
    private String low;

    /** 价格区间结束位置 */
    private String high;

    private String groupField;

    // ============= 上面的参数从前台传入 =============

    /** 后台类目集合(只为搜索提供功能, 不需要从前台注入参数) */
    private List<Integer> categoryIds;

    /** 运营设定的查询条件 (pidvid 最大个数) 及 (后台类目的最大个数) */
    private Map<StatsType, Integer> statsInfo = new HashMap<StatsType, Integer>();

    /** 是否高亮搜索结果 */
    private boolean isHighlight = false;

    /** 是否有参加活动 */
    private boolean joinActivity = false;

    private List<Long> excludeProductIds;

    /**
     * 验证搜索条件，后台类目和关键字不能两个都为空，因为用户的搜索行为不外乎三种<br>
     * 1. 关键字
     * 2. 前台类目 --> 要转换成相应的后台类目
     * 3. 前台类目(要转换成相应的后台类目)加属性值
     *
     * @return 未若传入关键字或后台类目, 则返回 true
     */
    public boolean validate() {
        return StringUtils.isBlank(keyword) && (categoryIds == null || categoryIds.size() == 0);
    }

    /**
     * 是否通过关键字搜索
     *
     * @return 是通过关键字在搜索则返回 true, 否则返回 false
     */
    public boolean hasKeyWordSearch() {
        return StringUtils.isNotEmpty(keyword);
    }

    /**
     * 是否是通过类目查询(无关键字, 且前台类目只传了一个并正常)
     *
     * @return 是则返回 true, 否则返回 false
     */
    public boolean isCategorySearch() {
        return !hasKeyWordSearch() && getCidList().size() > 0;
    }

    /**
     * 增加统计类型
     *
     * @param statsType 统计类型，目前支持pidvid和leafId
     * @param count     统计结果的数量
     * @return
     */
    public ProductQuery addStatsInfo(StatsType statsType, int count) {
        this.statsInfo.put(statsType, count);
        return this;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public List<Integer> getCidList() {
        List<Integer> cidList = new ArrayList<Integer>();
        if (StringUtils.isNotBlank(cid)) {
            for (String str : cid.split(",")) {
                int categoryId = NumberUtils.toInt(str);

                if (categoryId > 0) cidList.add(categoryId);
            }
        }
        return cidList;
    }

    public List<Long> getPidvids() {
        List<Long> pvs = new ArrayList<Long>();
        if (StringUtils.isNotBlank(pv)) {
            for (String pidvid : pv.split(",")) {
                long pv = NumberUtils.toLong(pidvid);

                if (pv > 0) pvs.add(pv);
            }
        }
        return pvs;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = NumberUtils.toInt(page) > 0 ? page : "1";
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = NumberUtils.toInt(pageSize) > 0 ? pageSize : "12";
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public SortBy getSortBy() {
        try {
            return SortBy.valueOf(sort);
        } catch (Exception e) {
            return SortBy.score;
        }
    }

    public OrderBy getOrderBy() {
        try {
            return OrderBy.valueOf(order);
        } catch (Exception e) {
            return OrderBy.desc;
        }
    }

    public String getOrder() {
        return StringUtils.isNotBlank(order) ? order : OrderBy.asc.toString();
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    /** 价格区间起始位置 */
    public long getLowPrice() {
        return NumberUtils.isNumber(low) ? Money.YuanToCent(low) : 0;
    }

    /** 价格区间结束位置 */
    public long getHighPrice() {
        return NumberUtils.isNumber(high) ? Money.YuanToCent(high) : 0;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Map<StatsType, Integer> getStatsInfo() {
        return statsInfo;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }

    public boolean isJoinActivity() {
        return joinActivity;
    }

    public void setJoinActivity(boolean joinActivity) {
        this.joinActivity = joinActivity;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("keyword", keyword)
                .add("cid", cid)
                .add("pv", pv)
                .add("page", page)
                .add("pageSize", pageSize)
                .add("sort", sort)
                .add("order", order)
                .add("low", low)
                .add("high", high)
                .add("categoryIds", categoryIds)
                .add("statsInfo", statsInfo)
                .add("isHighlight", isHighlight)
                .add("joinActivity", joinActivity)
                .toString();
    }

    public List<Long> getExcludeProductIds() {
        return excludeProductIds;
    }

    public void setExcludeProductIds(List<Long> excludeProductIds) {
        this.excludeProductIds = excludeProductIds;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

   public static class SearchSchemaField{

         public static String ID = "id";//产品id
         public static String SKUID = "skuId";//skuid
         public static String VALUATION = "valuation";//评论
         public static String SKUSELL = "sell";//SKU的销量
         public  static String PRODUCTSELL = "productSell";//产品的销量


     }
}
