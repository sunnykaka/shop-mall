package com.kariqu.searchengine;

import com.kariqu.searchengine.exception.SearchException;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午11:28
 */
public interface SearchEngineUpdateService {

    /**
     * 刷新商品索引服务器，以便重新打开索引
     *
     * @throws IOException
     * @throws SolrServerException
     */
    void doProductRefresh() throws IOException, SearchException;


    /**
     * 刷新类目索引服务器
     * @throws IOException
     * @throws SearchException
     */
    void doCategoryRefresh() throws IOException, SearchException;

    /**
     * 做商品全量Dump
     *
     * @throws IOException
     * @throws SolrServerException
     */
    void doProductDataDump() throws IOException, SearchException;


    /**
     * 做类目全量Dump
     * @throws IOException
     * @throws SearchException
     */
    void doCategoryDataDump() throws IOException, SearchException;


    /**
     * 做增量dump
     *
     * @throws IOException
     * @throws SearchException
     */
    void doDataProductIncrementDump() throws IOException, SearchException;


    /**
     * 按照商品ID来删除索引
     *
     * @param ids
     */
    void deleteProductIndexByIds(List<String> ids);

    /**
     * 删除全部商品索引
     *
     * @throws IOException
     * @throws SolrServerException
     */
    void deleteAllProductIndex() throws IOException, SearchException;


    /**
     * 删除全部类目索引
     * @throws IOException
     * @throws SearchException
     */
    void deleteAllCategoryIndex() throws IOException, SearchException;

}
