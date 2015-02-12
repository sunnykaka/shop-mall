package com.kariqu.searchengine.domain;


import java.util.LinkedList;
import java.util.List;

/**
 * 搜索结果
 *
 * @Author: Tiger
 * @Since: 11-6-26 下午3:15
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class SearchResult {

    private int totalHits;

    private List<ProductInfo> products = new LinkedList<ProductInfo>();

    private List<CountStatsNode<Integer>> leafStatsResult = new LinkedList<CountStatsNode<Integer>>();

    private List<CountStatsNode<Long>> pidvidStatsResult = new LinkedList<CountStatsNode<Long>>();

    private boolean success = true;

    public SearchResult() {
    }

    public SearchResult(boolean success) {
        this.success = success;
    }

    public void addLeafStats(CountStatsNode<Integer> leafStats) {
        this.leafStatsResult.add(leafStats);
    }

    public void addPidVidStats(CountStatsNode<Long> pidvidStats) {
        this.pidvidStatsResult.add(pidvidStats);
    }

    public List<CountStatsNode<Integer>> getLeafStatsResult() {
        return leafStatsResult;
    }

    public List<CountStatsNode<Long>> getPidVidStatsResult() {
        return pidvidStatsResult;
    }

    public List<ProductInfo> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInfo> products) {
        this.products = products;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
