package com.kariqu.searchengine.service;

import com.kariqu.searchengine.domain.*;
import com.kariqu.searchengine.exception.SearchException;
import org.apache.solr.client.solrj.SolrServerException;

import java.net.MalformedURLException;
import java.util.List;

/**
 * 搜索引擎查询服务
 *
 * @Author: Tiger
 * @Since: 11-6-26 下午2:48
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface SearchEngineQueryService {

    /**
     * 全匹配搜索一个商品
     *
     * @param productQuery
     * @return
     * @throws SearchException
     */
    SearchResult queryProductByAllMatch(ProductQuery productQuery);

    SearchResult queryProducts(ProductQuery productQuery);

    /**
     * 搜索商品，得到商品和统计信息
     *
     * @param productQuery
     * @return
     * @throws MalformedURLException
     * @throws SolrServerException
     */
    SearchResult queryProductsByQuery(ProductQuery productQuery);

    /**
     * 根据用户输入的关键字瞬间自动建议，比如输入联想可建议出联想笔记本
     * 并返回建议结果大概有多少个商品
     *
     * @param keyword
     * @return
     * @throws MalformedURLException
     * @throws SolrServerException
     */
    List<SuggestResult> querySuggest(String keyword);


    /**
     * 搜索类目
     *
     * @param keyword
     * @return
     * @throws MalformedURLException
     * @throws SolrServerException
     */
    List<CategoryResult> queryCategory(String keyword);
    /**
     * 输入联想，自动补全
     * @Author play
     * @param keyword 用户输入的关键字
     * @param facetSize 类目提示最大返回条数,参考值---京东此值为:2
     * @param commonTermCount 联想词条最大返回条数,参考值--京东此值是:9
     * @return 自动补全结果
     */
    AutoCompeleteResult suggest(String keyword,int facetSize,int commonTermCount);


    /**
     * 重建搜索提示索引
     */
    void reIndexMemorySuggest();
}
