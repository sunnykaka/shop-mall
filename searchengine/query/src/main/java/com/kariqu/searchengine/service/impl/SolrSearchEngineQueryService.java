package com.kariqu.searchengine.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kariqu.searchengine.domain.*;
import com.kariqu.searchengine.exception.SearchException;
import com.kariqu.searchengine.repository.BrandRespository;
import com.kariqu.searchengine.repository.CategoryRespository;
import com.kariqu.searchengine.service.SearchEngineQueryService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.util.NamedList;
import org.nlpcn.commons.lang.index.MemoryIndex;
import org.nlpcn.commons.lang.pinyin.Pinyin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索引擎服务solr实现
 *
 * @Author: Tiger, Asion
 * @Since: 11-6-26 下午2:58
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class SolrSearchEngineQueryService implements SearchEngineQueryService {

    private static final Log logger = LogFactory.getLog(SolrSearchEngineQueryService.class);

    private String searchUrl;

    private String suggestUrl;

    private CategoryRespository categoryRespository;

    private BrandRespository brandRespository;

    private static final String DEFAULT_MEMORY_INDEX_SUGGEST_KEY = "default_memory_suggest_index";
    private static final String BRAND_MEMORY_INDEX_SUGGEST_KEY = "brand_memory_suggest_index";

    private String[] defaultTipTerms = new String[]{"不粘锅", "平底锅", "水果刀", "炒锅"};//作配置,运营人员自行添加


    private ConcurrentHashMap<String, MemoryIndex<String>> suggestIndexMap = new ConcurrentHashMap<String, MemoryIndex<String>>();

    @Override
    public SearchResult queryProductByAllMatch(ProductQuery productQuery) {
        if (productQuery.hasKeyWordSearch()) {
            SolrQuery query = new SolrQuery();
            try {
                productQuery.setKeyword(escape(productQuery.getKeyword()));
                SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
                query.setQuery("originalName:" + productQuery.getKeyword());
                setQueryInfo(productQuery, query);
                QueryResponse response = server.query(query);
                List<ProductInfo> productInfos = response.getBeans(ProductInfo.class);
                return buildSearchResult(productQuery, response, productInfos);
            } catch (SolrServerException e) {
                logger.error("搜索发生异常, 此时的参数是:" + productQuery.toString() + ", 查询参数是:" + query.toString(), e);
            }
        }
        return new SearchResult(false);
    }

    @Override
    public SearchResult queryProducts(ProductQuery productQuery) {
        //这里不用去查queryProductByAllMatch 不知道之前查的意义是什么
        //  SearchResult searchResult = queryProductByAllMatch(productQuery);
        // if (searchResult == null || searchResult.getProducts().size() == 0) {
        return queryProductsByQuery(productQuery);
        // }
        //  return searchResult;
    }

    @Override
    public SearchResult queryProductsByQuery(ProductQuery productQuery) {
        if (productQuery.validate()) {
            if (logger.isErrorEnabled())
                logger.error("关键字为空或者没有传入前台类目, 也可能前台类目没有挂上对应的后台类目. 此时的参数: " + productQuery.toString());

            return new SearchResult(false);
        }
        if (productQuery.hasKeyWordSearch()) {
            productQuery.setKeyword(escape(productQuery.getKeyword()));
        }
        SolrQuery query = new SolrQuery();
        try {
            SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
            setQueryString(query, productQuery);
            setFilter(query, productQuery);
            setQueryInfo(productQuery, query);
            if (!StringUtils.isEmpty(productQuery.getKeyword())) {
                setQueryBoost(query);
            }
            //  query.setShowDebugInfo(true);
            QueryResponse response = server.query(query);
            Map<String, Object> debugMap = response.getDebugMap();
            /*for (Object o : debugMap.values()) {
                System.out.println(o);
            }*/
            List<ProductInfo> productInfos = null;
            if(productQuery.getGroupField()== null) {
                 productInfos = response.getBeans(ProductInfo.class);
            }else {
                 productInfos = buildSearchResultIfHasGroup(response,server);
            }
            return buildSearchResult(productQuery, response, productInfos);
        } catch (SolrServerException e) {
            if (logger.isErrorEnabled())
                logger.error("搜索发生异常, 此时的参数是:" + productQuery.toString() + ", 查询参数是:" + query.toString(), e);
        }
        return new SearchResult(false);
    }

    private List<ProductInfo> buildSearchResultIfHasGroup(QueryResponse response,SolrServer solrServer){
        List<ProductInfo> list = Lists.newArrayList();

        GroupResponse groupResponse = response.getGroupResponse();
        if(groupResponse != null) {
            List<GroupCommand> groupList = groupResponse.getValues();
            for(GroupCommand groupCommand : groupList) {
                List<Group> groups = groupCommand.getValues();
                for(Group group : groups) {
                    list.addAll(solrServer.getBinder().getBeans(ProductInfo.class,group.getResult()));
                }
            }
        }
        for (ProductInfo productInfo : list) {
            productInfo.setSkuId(null);
        }
        return list;
    }

    /**
     * @param solrQuery
     */
    private void setQueryBoost(SolrQuery solrQuery) {
        solrQuery.setParam("qf", "categoryName^2.0 brandName^2.0 name^0.5");//设置权重 当类目名字有搜索的关键时分值更高，其实品牌名 再其次商品名
        solrQuery.setParam("defType", "edismax");
    }

    /**
     * 设置搜索的一些信息，比如高亮，分页，统计，排序等
     *
     * @param productQuery
     * @param query
     */
    private void setQueryInfo(ProductQuery productQuery, SolrQuery query) {
        //目前发现高亮相当消耗性能
        setHighlight(productQuery, query);
        setStart(query, productQuery);
        setRows(query, productQuery);
        setSortInfo(query, productQuery);
        setFacet(query, productQuery);
        setGroupBy(query,productQuery);
    }

    private void setGroupBy(SolrQuery query, ProductQuery productQuery) {

        if(productQuery.getGroupField() != null){
            query.setParam("group",true);
            query.setParam("group.field",productQuery.getGroupField());
        }

    }

    /**
     * 构建搜索结果
     *
     * @param productQuery
     * @param response
     * @param productInfos
     * @return
     */
    private SearchResult buildSearchResult(ProductQuery productQuery, QueryResponse response, List<ProductInfo> productInfos) {
        SearchResult searchResult = new SearchResult();
         if(productQuery.getGroupField()==null) {
             doLeafIdStats(response, searchResult);
             doPidVidStats(response, searchResult);

             processHighlight(productQuery, response, productInfos);
             processProductSort(productInfos);
             searchResult.setTotalHits((int) response.getResults().getNumFound());
         }
        searchResult.setProducts(productInfos);


        return searchResult;
    }

    /**
     * 这里插入排序规则，目前如果商品参加活动则拍在前面
     *
     * @param productInfos
     * @return
     */
    private void processProductSort(List<ProductInfo> productInfos) {
        Collections.sort(productInfos, new Comparator<ProductInfo>() {
            @Override
            public int compare(ProductInfo o1, ProductInfo o2) {

                if (o1.getActivityType() != null && o2.getActivityType() == null) {
                    return -1;
                }

                if (o1.getActivityType() == null && o2.getActivityType() != null) {
                    return 1;
                }

                return 0;
            }
        });
    }

    /**
     * 按照属性和值统计
     *
     * @param response
     * @param searchResult
     */
    private void doPidVidStats(QueryResponse response, SearchResult searchResult) {
        FacetField pidvid = response.getFacetField("pidvid");
        if (pidvid != null && pidvid.getValues() != null) {
            for (FacetField.Count count : pidvid.getValues()) {
                searchResult.addPidVidStats(new CountStatsNode<Long>(Long.valueOf(count.getName()), count.getCount()));
            }
        }
    }

    /**
     * 按照商品发布的后台类目来统计
     *
     * @param response
     * @param searchResult
     */
    private void doLeafIdStats(QueryResponse response, SearchResult searchResult) {
        FacetField leafId = response.getFacetField("leafId");
        if (leafId != null && leafId.getValues() != null) {
            for (FacetField.Count count : leafId.getValues()) {
                searchResult.addLeafStats(new CountStatsNode<Integer>(Integer.valueOf(count.getName()), count.getCount()));
            }
        }
    }

    /**
     * 处理高亮
     *
     * @param productQuery
     * @param response
     * @param productInfos
     */
    private void processHighlight(ProductQuery productQuery, QueryResponse response, List<ProductInfo> productInfos) {
        if (productQuery.isHighlight()) {
            Map<String, ProductInfo> stringProductInfoMap = convertToMap(productInfos);
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            for (String docId : highlighting.keySet()) {
                ProductInfo productInfo = stringProductInfoMap.get(docId);
                Map<String, List<String>> stringListMap = highlighting.get(docId);
                if (!stringListMap.isEmpty())
                    productInfo.setHighlightName(stringListMap.get("name").get(0));
            }
        }
    }


    @Override
    public List<SuggestResult> querySuggest(String keyword) {
        try {
            SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
            SolrQuery query = new SolrQuery();
            query.setQueryType("/terms");
            query.setTerms(true);
            query.setTermsPrefix(keyword);
            query.addTermsField("name");
            //返回商品量大的前10个词
            query.setTermsLimit(10);
            QueryResponse response = server.query(query);
            List<SuggestResult> resultList = new LinkedList<SuggestResult>();
            TermsResponse termsResponse = response.getTermsResponse();
            List<TermsResponse.Term> terms = termsResponse.getTerms("name");
            for (TermsResponse.Term term : terms) {
                resultList.add(new SuggestResult(term.getTerm(), term.getFrequency()));
            }
            return resultList;
        } catch (SolrServerException e) {
            logger.error("搜索发生异常", e);
            logger.error("发生搜索异常时的参数:" + keyword);
            return Collections.emptyList();
        }
    }

    @Override
    public List<CategoryResult> queryCategory(String keyword) {
        try {
            SolrServer server = SolrServerHolder.getCategorySearchServer(suggestUrl);
            SolrQuery query = new SolrQuery();
            query.setQuery(keyword);
            return server.query(query).getBeans(CategoryResult.class);
        } catch (SolrServerException e) {
            logger.error("搜索发生异常", e);
            logger.error("发生搜索异常时的参数:" + keyword);
            return Collections.emptyList();
        }
    }

    private Map<String, ProductInfo> convertToMap(List<ProductInfo> productInfoList) {
        Map<String, ProductInfo> map = new HashMap<String, ProductInfo>();
        for (ProductInfo productInfo : productInfoList) {
            map.put(productInfo.getId(), productInfo);
        }
        return map;
    }

    /**
     * 构建搜索条件
     *
     * @param query
     * @param productQuery
     */
    private void setQueryString(SolrQuery query, ProductQuery productQuery) {
        StringBuilder sbd = new StringBuilder();

        // 关键字
        if (StringUtils.isNotBlank(productQuery.getKeyword())) {
            sbd.append(excludeStopWords(productQuery.getKeyword()));
        }

        // 所在类目
        if (productQuery.getCategoryIds() != null && productQuery.getCategoryIds().size() > 0) {
            if (sbd.toString().trim().length() > 0) sbd.append(" AND ");

            sbd.append("(");
            List<Integer> categoryIds = productQuery.getCategoryIds();
            for (int i = 0; i < categoryIds.size(); i++) {
                if (i > 0) sbd.append(" OR ");

                sbd.append("categoryId:").append(categoryIds.get(i));
            }
            sbd.append(")");
        }

        // 价格区间
        if (productQuery.getLowPrice() <= productQuery.getHighPrice() && productQuery.getHighPrice() > 0) {
            if (sbd.toString().trim().length() > 0) sbd.append(" AND ");

            sbd.append("price:[").append(productQuery.getLowPrice())
                    .append(" TO ").append(productQuery.getHighPrice()).append("]");
        }

        // 属性值
        for (Long pidvid : productQuery.getPidvids()) {
            if (sbd.toString().trim().length() > 0) sbd.append(" AND ");

            sbd.append("pidvid:").append(pidvid);
        }

        // 是否有参加活动
        if (productQuery.isJoinActivity()) {
            if (sbd.toString().trim().length() > 0) sbd.append(" AND ");

            sbd.append("activityType:['' TO *]");
        }
        query.setQuery(sbd.toString());
    }

    private void setFilter(SolrQuery query, ProductQuery productQuery) {
        if (productQuery.getExcludeProductIds() != null && productQuery.getExcludeProductIds().size() > 0) {
            Joiner joiner = Joiner.on(" ");
            String filterQuery = joiner.join("-id:(", joiner.join(productQuery.getExcludeProductIds()), ")");
            logger.debug(filterQuery);
            query.setFilterQueries(filterQuery);
        }
    }

    private void setStart(SolrQuery query, ProductQuery productQuery) {
        int pageNum = NumberUtils.toInt(productQuery.getPage());
        if (pageNum < 1) pageNum = 1;

        query.setStart((pageNum - 1) * getRows(productQuery));
    }

    private void setRows(SolrQuery query, ProductQuery productQuery) {
        query.setRows(getRows(productQuery));
    }

    private int getRows(ProductQuery productQuery) {
        int pageSize = NumberUtils.toInt(productQuery.getPageSize());
        return pageSize > 0 ? pageSize : 10;
    }

    private void setSortInfo(SolrQuery query, ProductQuery productQuery) {
        query.setSortField(getSortField(productQuery), getOrder(productQuery));
    }

    private String getSortField(ProductQuery productQuery) {
        return productQuery.getSortBy().toFiled();
    }

    private SolrQuery.ORDER getOrder(ProductQuery productQuery) {
        if (productQuery.getOrderBy() == OrderBy.asc) {
            return SolrQuery.ORDER.asc;
        }
        return SolrQuery.ORDER.desc;
    }

    private void setFacet(SolrQuery query, ProductQuery productQuery) {
        Map<StatsType, Integer> statsInfo = productQuery.getStatsInfo();
        query.setFacet(null != statsInfo && !statsInfo.isEmpty());
        if (null == statsInfo || statsInfo.isEmpty()) {
            return;
        }
        for (Map.Entry<StatsType, Integer> statsType : statsInfo.entrySet()) {
            query.addFacetField(statsType.getKey().toField());
            query.setFacetLimit(statsType.getValue());
            query.setFacetMinCount(1);
        }
    }

    private void setHighlight(ProductQuery productQuery, SolrQuery query) {
        if (productQuery.isHighlight()) {
            query.addHighlightField("name");
            query.setHighlightSimplePre("<font color=\"red\">");
            query.setHighlightSimplePost("</font>");
        }
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public void setSuggestUrl(String suggestUrl) {
        this.suggestUrl = suggestUrl;
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
                    || c == '?' || c == '|' || c == '&') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
        //return QueryParser.escape(s);
    }

    /**
     * 自动补全(输入联想,目前取用的是品牌名，类目名)
     *
     * @param keyword
     * @return
     */
    public AutoCompeleteResult suggest(String keyword, int facetSize, int commonTermSize) {

        if (StringUtils.isEmpty(keyword)) {
            return new AutoCompeleteResult(defaultTipTerms);
        }


        AutoCompeleteResult autoCompeleteResult = new AutoCompeleteResult();
        //查询所有的类目名称
        //end 建立联想词索引

        try {
            //类目统计
            keyword = keyword.replaceAll("^\\s*","");//去掉两边空格
            MemoryIndex<String> defaultMemoryIndex = suggestIndexMap.get(DEFAULT_MEMORY_INDEX_SUGGEST_KEY);
            keyword = QueryParser.escape(keyword).toLowerCase();//转义并转换成小写
          /*  Iterable<String> keywords = Splitter.on(" ").omitEmptyStrings().split(keyword);*/
            keyword = keyword.replaceAll("\\s+", " ");
            long stime = System.currentTimeMillis();
            //List<Long> categoryIds = facetCategoryId(keyword,facetSize);
            List<Long> categoryIds = Lists.newArrayList();//因类目关联关系求解决，暂进不进类目提示
            logger.debug("facet cost time:" + (System.currentTimeMillis() - stime));
            Set<String> suggestText = Sets.newHashSet();//去重作用
            stime = System.currentTimeMillis();
            // for (String word : keywords) {
            suggestText.addAll(defaultMemoryIndex.smartSuggest(keyword));
            // }
            if (suggestText.size() == 0) {
                MemoryIndex<String> brandMemoryIndex = suggestIndexMap.get(BRAND_MEMORY_INDEX_SUGGEST_KEY);
                suggestText.addAll(brandMemoryIndex.smartSuggest(keyword));
            }
            if (suggestText.size() == 0) {//如果还没有suggest到 用或关系找
                Iterable<String> keywords = Splitter.on(" ").omitEmptyStrings().split(keyword);
                for (String word : keywords) {
                    suggestText.addAll(defaultMemoryIndex.smartSuggest(word));
                    if(suggestText.size()>=commonTermSize){
                        break;
                    }
                }

            }
            logger.debug("memory search cost time:" + (System.currentTimeMillis() - stime));
            List<SuggestResult> suggestResults = Lists.newArrayList();
            for (String term : suggestText) {
                SuggestResult suggestResult = new SuggestResult(term, 0);//目前还没有统计数量
                suggestResults.add(suggestResult);
            }
            if (suggestResults.size() > commonTermSize) {
                suggestResults = suggestResults.subList(0, commonTermSize);//截取
            }
            autoCompeleteResult.setCategoryIdSuggestResults(categoryIds);
            autoCompeleteResult.setTextSuggestResults(suggestResults);
        } catch (Exception e) {
            logger.error("输入提示发生异常", e);
            logger.error("输入提示发生异常时参数:" + keyword);
            return autoCompeleteResult;
        }
        return autoCompeleteResult;
    }

    /**
     * 关键字查询按类目统计
     *
     * @param keyword
     * @return
     * @throws SolrServerException
     */
    private List<Long> facetCategoryId(String keyword, int limit) throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.addFacetField("categoryId");
        solrQuery.setQuery("name:" + keyword);
        solrQuery.setParam("q.op", "AND");
        solrQuery.setFacet(true);
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetLimit(limit);
        SolrServer server = SolrServerHolder.getProductSearchServer(searchUrl);
        QueryResponse response = server.query(solrQuery);
        List<FacetField.Count> facetResult = response.getFacetField("categoryId").getValues();
        List<Long> categoryIds = Lists.newArrayList();
        if (facetResult != null) {
            for (FacetField.Count count : facetResult) {
                categoryIds.add(Long.valueOf(count.getName()));
            }
        }
        return categoryIds;

    }

    /**
     * 此方法为初始化memory suggest的方法  spring会在bean初始化后立即调用
     */
    public void reIndexMemorySuggest() {

        logger.debug("初始化词条联想内存索引");
        logger.debug("最大可用内存" + (Runtime.getRuntime().maxMemory() / 1000000));
        logger.debug("当前JVM空闲内存" + (Runtime.getRuntime().freeMemory() / 1000000));
        logger.debug("当前JVM占用的内存总数" + (Runtime.getRuntime().totalMemory() / 1000000));

        long begin = (Runtime.getRuntime().freeMemory() / 1000000);

        MemoryIndex<String> defaultMemoryIndex = new MemoryIndex<String>();
        MemoryIndex<String> brandMemoryIndex = new MemoryIndex<String>();

        List<String> categoryNames = categoryRespository.loadAllCategoryName();
        //查询所有的品牌名
        List<String> brandNames = brandRespository.loadAllBrandName();
        // 建立联想词索引
        /**类目名**/
        for (String categoryName : categoryNames) {
            if (StringUtils.isEmpty(categoryName) || categoryName.contains("其它") || "abcd".equals(categoryName)) {
                continue;
            }
            String[] names = categoryName.replaceAll("\\\\", " ").replaceAll("/", " ").replaceAll(",", " ").replaceAll("、", " ").split(" ");
            for (String name : names) {
                String orignalName = name;
                name = name.toLowerCase();//转换成小写
                String quanpin = defaultMemoryIndex.str2QP(name); //全拼
                String jianpinpin = new String(Pinyin.str2FirstCharArr(name)); //简拼
                defaultMemoryIndex.addItem(orignalName, name, quanpin, jianpinpin);
            }

        }
        /*品牌名*/
        for (String brandName : brandNames) {
            if (StringUtils.isEmpty(brandName) || brandName.contains("其它")) {
                continue;
            }
            String[] names = brandName.replaceAll("\\\\", " ").replaceAll("/", " ").replaceAll(",", " ").replaceAll("、", " ").split(" ");
            for (String name : names) {
                String orignalName = name;
                name = name.toLowerCase();//转换成小写
                String quanpin = defaultMemoryIndex.str2QP(name); //全拼
                String jianpinpin = new String(Pinyin.str2FirstCharArr(name)); //简拼
                defaultMemoryIndex.addItem(orignalName, name, quanpin, jianpinpin);
            }

        }
        List<String> categoryBrandName = getAllCategoryBrandMapping();
        //暂时没有想到更好的办法来实现...先满足功能
        for (String name : categoryBrandName) {
            if (StringUtils.isEmpty(name) || name.contains("其它")) {
                continue;
            }
            String[] cbName = name.split("\\^");

            String keys[] = cbName[0].replaceAll("\\\\", " ").replaceAll("/", " ").replaceAll(",", " ").replaceAll("、", " ").split(" ");
            if (keys.length < 2 || StringUtils.isEmpty(cbName[0]) || StringUtils.isEmpty(cbName[1])) {
                continue;
            }
            for (String key : keys) {
                String orignalKey = key;
                 /* 类目名 品牌名*/
                String orignalName = key + " " + cbName[1];
                //  System.out.println(orignalName);
                key = (key + " " + cbName[1]).toLowerCase();//转换成小写
                String quanpin = defaultMemoryIndex.str2QP(key); //全拼
                String jianpinpin = new String(Pinyin.str2FirstCharArr(key)); //简拼
                defaultMemoryIndex.addItem(orignalName, key, quanpin, jianpinpin);

                 /*品牌名 类目名*/
                String reverseOrignalName = cbName[1] + " " + orignalKey;
                //  System.out.println(orignalName);
                String reverseKey = (cbName[1] + " " + orignalKey).toLowerCase();//转换成小写
                String reverseQuanpin = defaultMemoryIndex.str2QP(reverseKey); //全拼
                String reverseJianpinpin = new String(Pinyin.str2FirstCharArr(reverseKey)); //简拼
                brandMemoryIndex.addItem(reverseOrignalName, reverseKey, reverseQuanpin, reverseJianpinpin);
            }


        }
        suggestIndexMap.put(BRAND_MEMORY_INDEX_SUGGEST_KEY, brandMemoryIndex);
        suggestIndexMap.put(DEFAULT_MEMORY_INDEX_SUGGEST_KEY, defaultMemoryIndex);
        logger.debug("内存自动补全使用了的内存" + (begin - Runtime.getRuntime().freeMemory() / 1000000));
        logger.debug("最大可用内存" + (Runtime.getRuntime().maxMemory() / 1000000));
        logger.debug("当前JVM空闲内存" + (Runtime.getRuntime().freeMemory() / 1000000));
        logger.debug("当前JVM占用的内存总数" + (Runtime.getRuntime().totalMemory() / 1000000));
    }

    private List<String> getAllCategoryBrandMapping() {

        return categoryRespository.loadAllCategoryBrandMapping();
    }

    public void setCategoryRespository(CategoryRespository categoryRespository) {
        this.categoryRespository = categoryRespository;
    }

    private String excludeStopWords(String keyword) {
        if (keyword != null) {
            keyword = keyword.replaceAll("子", "");
        }
        return keyword;
    }

    public void setBrandRespository(BrandRespository brandRespository) {
        this.brandRespository = brandRespository;
    }

    /**
     * 持有一个solr搜索服务，SolrServer是线程安全的，重用实例，不然会出现连接泄露
     * CommonsHttpSolrServer is thread-safe and if you are using the following constructor,
     * you *MUST* re-use the same instance for all requests.  If instances are created on
     * the fly, it can cause a connection leak. The recommended practice is to keep a
     * static instance of CommonsHttpSolrServer per solr server url and share it for all requests.
     * See https://issues.apache.org/jira/browse/SOLR-861 for more details
     * <p/>
     * User: Asion
     * Date: 12-5-5
     * Time: 上午10:34
     */
    public static class SolrServerHolder {

        private static SolrServer productSearchServer;

        private static SolrServer categorySearchServer;

        public static synchronized SolrServer getProductSearchServer(String url) {
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

        public static synchronized SolrServer getCategorySearchServer(String url) {
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
}
