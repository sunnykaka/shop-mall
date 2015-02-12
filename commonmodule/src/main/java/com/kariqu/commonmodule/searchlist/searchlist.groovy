import com.google.common.base.Splitter
import com.kariqu.common.pagenavigator.Page
import com.kariqu.common.pagenavigator.PageProcessor
import com.kariqu.designcenter.domain.model.RenderConstants
import com.kariqu.searchengine.domain.ProductInfo
import com.kariqu.searchengine.domain.ProductQuery
import com.kariqu.searchengine.domain.SearchResult
import com.kariqu.searchengine.domain.SortBy
import com.kariqu.searchengine.domain.OrderBy
import com.kariqu.searchengine.domain.StatsType
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils

/**
 * 搜索列表模块的脚本
 * 会在当前上下文中查看是否有用户输入的关键字，如果没有就请求所有记录
 * 搜到的商品按照所在的后台类目和类目属性和值来统计
 * 如果有前台类目的搜索条件，会用这个前台类目找到所关联的后台类目来建立OR查询
 * @param context
 * @param params
 * @return
 */
def execute(context, params) {
    def map = new HashMap()
    ProductQuery productQuery = context.get("productQuery")
    if (context.get(RenderConstants.RENDER_MOD) != RenderConstants.RenderMod.product) {
        // 非产品渲染模式时查询所有商品
        productQuery = new ProductQuery()
        productQuery.setKeyword("*")
    } else if (productQuery == null) {
        productQuery = new ProductQuery()
        productQuery.setCid("248")
    } else if ("*".equals(productQuery.getKeyword())) {
        // 否则不允许搜索所有商品
        productQuery.setKeyword("")
    }

    // 筛选属性值
    productQuery.addStatsInfo(StatsType.LEAFID, NumberUtils.toInt(params.get('leafIdCount')))
    productQuery.addStatsInfo(StatsType.PIDVID, NumberUtils.toInt(params.get('pidvidCount')))

    // 后台搜索关联的后台类目(查询时实际用到的条件)
    def categoryTree = categoryCenterContainer.getCategoryTree()
    def queryResult = search(productQuery, categoryTree, params)
    def noSearchResult = queryResult.get("noSearchResult")
    def searchResult = queryResult.get("searchResult")

    def imgCount = NumberUtils.toInt(params.get("imgCount"))
    if (imgCount <= 0) imgCount = 4
    for (ProductInfo productInfo : searchResult.getProducts()) {
        // 最多显示四张图
        def picUrlList = productInfo.getPictureUrlList()
        if (picUrlList != null && picUrlList.size() > imgCount) {
            productInfo.setPictureUrlList(picUrlList.subList(0, imgCount))
        }

    }

    Page<ProductInfo> productPage = new Page<ProductInfo>(NumberUtils.toInt(productQuery.getPage()), NumberUtils.toInt(productQuery.getPageSize()))
    productPage.setTotalCount(searchResult.getTotalHits())
    productPage.setResult(searchResult.getProducts())

    map.put("categorySearch", productQuery.isCategorySearch())
    map.put("productPage", productPage)
    map.put("noSearchResult", noSearchResult)

    // 当搜索有结果时
    if (!noSearchResult) {
        map.put("cssPageBar", PageProcessor.process(productPage))

        // 如果是从类目搜索过来的
        if (productQuery.isCategorySearch()) {
            def categoryService = context.get('categoryService')
            def cid = productQuery.getCid()
            map.put("cid", cid)
            // 类目的筛选属性
            map.put("displayPropertyAndValue", categoryService.getNavigateCategorySearchableInfo(cid))

            def firstCid = productQuery.getCidList().get(0)
            // 当前位置, 以第一个 cid 为主
            map.put("currentCategoryPoint", categoryService.categoryNameTree(firstCid))
            // 左边的类目树, 以第一个 cid 为主
            map.put("categoryList", categoryService.buildCategory(firstCid, true, "y".equalsIgnoreCase(params.get("categoryListSort"))))
            // 父类目的纵向广告图
            map.put("parentCategory", categoryService.getParentCategory(firstCid))

            def showHotProductCount = NumberUtils.toInt(params.get("showHotProductCount"))
            if (showHotProductCount <= 0) showHotProductCount = 10

            // 是否出现热销推荐?
            ProductQuery hotProQuery = new ProductQuery()
            hotProQuery.setCategoryIds(categoryTree.queryNavigateCategoryAssociation(firstCid))
            def hotProSearchResult = searchEngineQuery.queryProductsByQuery(hotProQuery)
            if (hotProSearchResult.getTotalHits() > showHotProductCount) {
                // 热销推荐
                ProductQuery hotQuery = new ProductQuery()
                hotQuery.setCategoryIds(categoryTree.queryNavigateCategoryAssociation(firstCid))
                hotQuery.setSort(SortBy.valuation.toFiled())
                hotQuery.setOrder(OrderBy.desc.toString())
                hotQuery.setGroupField(ProductQuery.SearchSchemaField.ID)
                hotQuery.setPageSize("3")
                map.put("hotProductList", searchEngineQuery.queryProductsByQuery(hotQuery).getProducts())
            }
        } else {
            // 关键字搜索过来的
            def propertyMaxNumber = NumberUtils.toInt(params.get('propertyMaxNumber'))
            def valueMaxNumber = NumberUtils.toInt(params.get('valueMaxNumber'))
            def propertyArrayName = []
            if (StringUtils.isNotBlank(params.get('propertyArrayName'))) {
                for (
                        def propertyName : Splitter.on(',').trimResults().omitEmptyStrings().split(params.get('propertyArrayName'))) {
                    propertyArrayName << propertyName
                }
            }

            // 搜索的筛选属性
            map.put("statsPv", searchResultParserService.statsPv(searchResult.getLeafStatsResult().size(),
                    searchResult.getPidVidStatsResult(), propertyMaxNumber, valueMaxNumber, propertyArrayName))

            // 建议的商品
        }
    }
    map
}

def executeForm(context, params) {
    new HashMap()
}

def search(ProductQuery productQuery, categoryTree, params) {
    def map = new HashMap()
    map.put("noSearchResult", false)
    if (productQuery.hasKeyWordSearch()) {
        ProductQuery pq = new ProductQuery()
        pq.setKeyword(productQuery.getKeyword())
        SearchResult searchResult = searchEngineQuery.queryProducts(pq)
        // 如果只拿搜索关键字查询不出来结果, 则使用推荐商品
        if (searchResult == null || searchResult.getProducts().size() == 0) {
            ProductQuery recommendQuery = new ProductQuery()
            recommendQuery.setKeyword("*")
            recommendQuery.setPageSize("8")
            recommendQuery.setSort(SortBy.valuation.toFiled())
            recommendQuery.setOrder(OrderBy.desc.toString())
            recommendQuery.setGroupField(ProductQuery.SearchSchemaField.ID)
            map.put("searchResult", searchEngineQuery.queryProductsByQuery(recommendQuery))
            map.put("noSearchResult", true)
            return map
        }

        // 是否高亮
        // productQuery.setHighlight("y".equalsIgnoreCase(params.get("highlight")))

        // 关键字搜索的页面显示商品的条数
        def pageSize = NumberUtils.toInt(params.get("keyPageSize"))
        productQuery.setPageSize(pageSize > 0 ? String.valueOf(pageSize) : "20")
        map.put("searchResult", searchEngineQuery.queryProducts(productQuery))
        return map
    }

    // 类目页面显示商品的条数
    def pageSize = NumberUtils.toInt(params.get('pageSize'))
    productQuery.setPageSize(pageSize > 0 ? String.valueOf(pageSize) : "12")
    def cidList = []
    for (Integer cid : productQuery.getCidList()) {
        cidList.addAll(categoryTree.queryNavigateCategoryAssociation(cid))
    }
   // productQuery.setSort(SortBy.valuation.toFiled())//类目进去按评价数排序
    //productQuery.setOrder(OrderBy.desc.toString())
    productQuery.setCategoryIds(cidList)

    map.put("searchResult", searchEngineQuery.queryProducts(productQuery))
    map
}
