package com.kariqu.searchengine.service.impl;

import com.google.common.base.Splitter;
import com.kariqu.searchengine.domain.*;
import com.kariqu.searchengine.service.SearchEngineQueryService;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.net.MalformedURLException;
import java.util.List;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 下午12:23
 * <p/>
 * 此测试要服务启动后方可测试
 */
@SpringApplicationContext({"classpath:search.xml"})
public class SolrSearchEngineQueryServiceTest  extends UnitilsJUnit4 {
    @SpringBean("searchEngineQuery")
    private SearchEngineQueryService searchEngineQueryService;

    @Test
    public void testSuggest(){
        long stime = System.currentTimeMillis();
        AutoCompeleteResult result = searchEngineQueryService.suggest("平底 刀",2,10);
        System.out.println("用时:" + (System.currentTimeMillis() - stime));
        System.out.println(result);

    }

    @Test
    public void testQueryProducts() throws Exception {
        SolrSearchEngineQueryService searchEngineQueryService = new SolrSearchEngineQueryService();
        searchEngineQueryService.setSearchUrl("http://172.16.0.4:8034/solr");
        ProductQuery productQuery = new ProductQuery();
        productQuery.setKeyword("*");
        //productQuery.setPageSize(2);
        //productQuery.setPageNumber(1);
        //productQuery.setNavCategoryId(3);
        //List<Integer> categoryIds = new LinkedList<Integer>();
        //categoryIds.add(34);
        //categoryIds.add(7);
        //categoryIds.add(8);
        //categoryIds.add(9);
        //productQuery.setCategoryIds(categoryIds);
        //productQuery.setHighPrice(5000);
        //productQuery.sort(SortBy.PRICE);
        //productQuery.orderBy(OrderBy.ASC);
        //productQuery.addPropertyIdAndValueId(3, 3);
        //productQuery.addPropertyIdAndValueId(3, 8);
        productQuery.addStatsInfo(StatsType.LEAFID, 20);
        productQuery.addStatsInfo(StatsType.PIDVID, 20);
        //productQuery.addPidvid(8589944594l);
        //productQuery.addPidvid(4294977296l);
        //productQuery.setHighlight(true);
        SearchResult searchResult = searchEngineQueryService.queryProducts(productQuery);
        List<ProductInfo> products = searchResult.getProducts();
        System.out.println(products);
        System.out.println(searchResult.getLeafStatsResult());
        System.out.println(searchResult.getPidVidStatsResult());
    }

    @Test
    public void testTermSuggest() throws MalformedURLException, SolrServerException {
        SolrSearchEngineQueryService searchEngineQueryService = new SolrSearchEngineQueryService();
        searchEngineQueryService.setSearchUrl("http://172.16.0.4:8034/solr");
        List<SuggestResult> resultList = searchEngineQueryService.querySuggest("炒");
        System.out.println(resultList);
    }

    @Test
    public void testCategory() throws MalformedURLException, SolrServerException {
        SolrSearchEngineQueryService searchEngineQueryService = new SolrSearchEngineQueryService();
        searchEngineQueryService.setSuggestUrl("http://172.16.0.4:8034/solr/category");
        List<CategoryResult> resultList = searchEngineQueryService.queryCategory("做饭");
        System.out.println(resultList);
    }
    @Test
    public void testSpliter(){

        Iterable<String> keywords = Splitter.on(" ").omitEmptyStrings().split("中国 人民      生活");
        for (String keyword : keywords) {
            System.out.println(keyword);
        }

    }
}
