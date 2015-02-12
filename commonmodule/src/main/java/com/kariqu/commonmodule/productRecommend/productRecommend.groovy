import com.kariqu.designcenter.domain.model.RenderConstants
import com.kariqu.productcenter.domain.Money
import com.kariqu.searchengine.domain.OrderBy
import com.kariqu.searchengine.domain.ProductQuery
import com.kariqu.searchengine.domain.SortBy
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    def map = new HashMap()

    def product = context.get(RenderConstants.PRODUCT_CONTEXT_KEY)
    if (product != null) {
        // 同价位商品推荐
        def productSellPrice = Money.YuanToCent(product.getDefaultSkuObject().getSellPrice())
        def priceInterval = NumberUtils.toInt(params.get("priceInterval"))
        if (priceInterval <= 0) priceInterval = 10
        def interval = Money.YuanToCent(String.valueOf(priceInterval))

        def low = Money.getMoneyString(productSellPrice - interval)
        def high = Money.getMoneyString(productSellPrice + interval)

        ProductQuery samePriceQuery = new ProductQuery()
        //samePriceQuery.setKeyword("*")
        samePriceQuery.setCategoryIds(Arrays.asList(product.getCategoryId()))
        samePriceQuery.setExcludeProductIds(Arrays.asList(product.id))
        samePriceQuery.setLow(low)
        samePriceQuery.setHigh(high)
        samePriceQuery.setSort(SortBy.valuation.toString())
        samePriceQuery.setOrder(OrderBy.desc.toString())
        def samePriceNum = NumberUtils.toInt(params.get("samePriceNum"))
        samePriceQuery.setPageSize(String.valueOf(samePriceNum > 0 ? samePriceNum : 4))

        def recommendProductList = searchEngineQuery.queryProductsByQuery(samePriceQuery).getProducts()
        map.put("recommendProductList", recommendProductList)

        // 根据历史记录推荐的商品
        ProductQuery historyPriceQuery = new ProductQuery()
        historyPriceQuery.setCategoryIds(Arrays.asList(product.getCategoryId()))
        historyPriceQuery.setExcludeProductIds(Arrays.asList(product.id))
        historyPriceQuery.setSort(SortBy.score.toString())
        historyPriceQuery.setOrder(OrderBy.desc.toString())
        def historyRecommendNum = NumberUtils.toInt(params.get("historyRecommendNum"))
        historyPriceQuery.setPageSize(String.valueOf(historyRecommendNum > 0 ? historyRecommendNum : 4))

        def historyProductList = searchEngineQuery.queryProductsByQuery(historyPriceQuery).getProducts()
        map.put("historyProductList", historyProductList)
    }

    map
}

def executeForm(context, params) {
    new HashMap()
}
