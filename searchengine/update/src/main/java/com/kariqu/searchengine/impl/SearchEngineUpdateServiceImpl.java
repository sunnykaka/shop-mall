package com.kariqu.searchengine.impl;

import com.kariqu.searchengine.SearchEngineUpdateService;
import com.kariqu.searchengine.exception.SearchException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午11:29
 */
public class SearchEngineUpdateServiceImpl implements SearchEngineUpdateService {

    private final Log logger = LogFactory.getLog(SearchEngineUpdateServiceImpl.class);

    private String searchUrl;

    private String suggestUrl;


    @Override
    public void doProductRefresh() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
        try {
            server.commit();
        } catch (SolrServerException e) {
            logger.error("刷新[" + searchUrl + "]搜索服务器出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void deleteAllProductIndex() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
        try {
            server.deleteByQuery("*:*");
            server.commit();
        } catch (SolrServerException e) {
            logger.error("删除[" + searchUrl + "]索引出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void deleteProductIndexByIds(List<String> ids) {
        SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
        try {
            server.deleteById(ids);
            server.commit();
        } catch (SolrServerException e) {
            logger.error("删除[" + searchUrl + "]索引出现异常", e);
            throw new SearchException(e);
        } catch (IOException e) {
            logger.error("删除[" + searchUrl + "]索引出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void doProductDataDump() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
        SolrRequest solrRequest = new SolrRequest(SolrRequest.METHOD.GET, "/dataimport") {
            @Override
            public SolrParams getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("command", "full-import");
                map.put("debug", "false");
                map.put("commit", "true");
                return new MapSolrParams(map);
            }

            @Override
            public Collection<ContentStream> getContentStreams() throws IOException {
                return null;
            }

            @Override
            public SolrResponse process(SolrServer server) throws SolrServerException, IOException {
                return null;
            }
        };
        try {
            NamedList<Object> request = server.request(solrRequest);
            logger.info("全量dump[" + searchUrl + "]索引:" + request);
        } catch (SolrServerException e) {
            logger.error("全量dump[" + searchUrl + "]索引出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void doDataProductIncrementDump() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
        SolrRequest solrRequest = new SolrRequest(SolrRequest.METHOD.GET, "/dataimport") {
            @Override
            public SolrParams getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("command", "delta-import");
                map.put("debug", "false");
                map.put("commit", "true");
                return new MapSolrParams(map);
            }

            @Override
            public Collection<ContentStream> getContentStreams() throws IOException {
                return null;
            }

            @Override
            public SolrResponse process(SolrServer server) throws SolrServerException, IOException {
                return null;
            }
        };
        try {
            NamedList<Object> request = server.request(solrRequest);
            logger.info("增量dump[" + searchUrl + "]索引:" + request);
        } catch (SolrServerException e) {
            logger.error("增量dump[" + searchUrl + "]索引的时候出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void doCategoryRefresh() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getCategorySearchServer(suggestUrl);
        try {
            server.commit();
        } catch (SolrServerException e) {
            logger.error("刷新[" + suggestUrl + "]搜索服务器出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void doCategoryDataDump() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getCategorySearchServer(suggestUrl);
        SolrRequest solrRequest = new SolrRequest(SolrRequest.METHOD.GET, "/dataimport") {
            @Override
            public SolrParams getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("command", "full-import");
                map.put("debug", "false");
                map.put("commit", "true");
                return new MapSolrParams(map);
            }

            @Override
            public Collection<ContentStream> getContentStreams() throws IOException {
                return null;
            }

            @Override
            public SolrResponse process(SolrServer server) throws SolrServerException, IOException {
                return null;
            }
        };
        try {
            NamedList<Object> request = server.request(solrRequest);
            logger.info("全量dump[" + suggestUrl + "]索引:" + request);
        } catch (SolrServerException e) {
            logger.error("全量dump[" + suggestUrl + "]索引出现异常", e);
            throw new SearchException(e);
        }
    }

    @Override
    public void deleteAllCategoryIndex() throws IOException, SearchException {
        SolrServer server = SolrServerHolder.getCategorySearchServer(suggestUrl);
        try {
            server.deleteByQuery("*:*");
            server.commit();
        } catch (SolrServerException e) {
            logger.error("删除[" + suggestUrl + "]索引出现异常", e);
            throw new SearchException(e);
        }
    }


    public static class SolrServerHolder {

        private static SolrServer productSearchServer;

        private static SolrServer categorySearchServer;

        public synchronized static SolrServer getProductSearchServer(String url) {
            if (StringUtils.isEmpty(url)) {
                throw new SearchException("搜索服务器的地址没有配置");
            }
            if (productSearchServer != null) {
                return productSearchServer;
            } else {
                productSearchServer = new HttpSolrServer(url);
            }
            return productSearchServer;
        }

        public synchronized static SolrServer getCategorySearchServer(String url) {
            if (StringUtils.isEmpty(url)) {
                throw new SearchException("搜索服务器的地址没有配置");
            }
            if (categorySearchServer != null) {
                return categorySearchServer;
            } else {
                categorySearchServer = new HttpSolrServer(url);
            }
            return categorySearchServer;
        }


    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public void setSuggestUrl(String suggestUrl) {
        this.suggestUrl = suggestUrl;
    }
}
