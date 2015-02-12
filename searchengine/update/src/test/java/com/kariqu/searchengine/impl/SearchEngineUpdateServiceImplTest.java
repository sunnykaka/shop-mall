package com.kariqu.searchengine.impl;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import java.io.IOException;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午11:44
 * <p/>
 * 此测试要服务启动后方可测试
 */
public class SearchEngineUpdateServiceImplTest {

    @Test
    public void testCategoryDataDump() throws IOException, SolrServerException {
        SearchEngineUpdateServiceImpl searchEngineUpdateService = new SearchEngineUpdateServiceImpl();
        searchEngineUpdateService.setSuggestUrl("http://172.16.0.90:8034/solr/category");
        searchEngineUpdateService.doCategoryDataDump();
    }

    @Test
    public void testDeleteAllCategoryIndex() throws IOException, SolrServerException {
        SearchEngineUpdateServiceImpl searchEngineUpdateService = new SearchEngineUpdateServiceImpl();
        searchEngineUpdateService.setSuggestUrl("http://localhost:8983/solr/category");
        searchEngineUpdateService.deleteAllCategoryIndex();
    }


    @Test
    public void testDoProductDataDump() throws Exception {
        SearchEngineUpdateServiceImpl searchEngineUpdateService = new SearchEngineUpdateServiceImpl();
        searchEngineUpdateService.setSearchUrl("http://localhost:8983/solr");
        searchEngineUpdateService.doProductDataDump();
    }

    @Test
    public void testDeleteAllProductIndex() throws IOException, SolrServerException {
        SearchEngineUpdateServiceImpl searchEngineUpdateService = new SearchEngineUpdateServiceImpl();
        searchEngineUpdateService.setSearchUrl("http://localhost:8983/solr");
        searchEngineUpdateService.deleteAllProductIndex();
    }

    @Test
    public void doRefresh() throws IOException, SolrServerException {
        SearchEngineUpdateServiceImpl searchEngineUpdateService = new SearchEngineUpdateServiceImpl();
        searchEngineUpdateService.setSearchUrl("http://localhost:8983/solr");
        searchEngineUpdateService.doProductRefresh();
        searchEngineUpdateService.setSuggestUrl("http://localhost:8983/solr/category");
        searchEngineUpdateService.doCategoryRefresh();
    }
}
