import com.kariqu.categorycenter.client.service.CategoryPropertyQueryService
import com.kariqu.categorycenter.domain.model.Property
import com.kariqu.categorycenter.domain.model.Value
import com.kariqu.categorycenter.domain.util.PropertyValueUtil
import com.kariqu.designcenter.domain.model.RenderConstants
import com.kariqu.searchengine.domain.OrderBy
import com.kariqu.searchengine.domain.ProductQuery
import com.kariqu.searchengine.domain.SortBy
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    def map = new HashMap()
    def product = context.get(RenderConstants.PRODUCT_CONTEXT_KEY)
    if (product != null) {
        def pv = calPv("品牌", product.brandId)
        if (pv > 0) {
            // 品牌推荐商品
            ProductQuery recommendQuery = new ProductQuery()
            recommendQuery.setKeyword("*")
            recommendQuery.setExcludeProductIds(Arrays.asList(product.id))
            recommendQuery.setPv(String.valueOf(pv))
            recommendQuery.setSort(SortBy.valuation.toString())
            recommendQuery.setOrder(OrderBy.desc.toString())
            recommendQuery.setGroupField(ProductQuery.SearchSchemaField.ID);

            def brandRecommendNum = NumberUtils.toInt(params.get("brandRecommendNum"))
            recommendQuery.setPageSize(String.valueOf(brandRecommendNum > 0 ? brandRecommendNum : 5))
            def recommendProductList = searchEngineQuery.queryProductsByQuery(recommendQuery).getProducts()
            map.put("recommendProductList", recommendProductList)
        }

        def categoryService = context.get('categoryService')
        map.put("brand", categoryService.queryBrandById(product.brandId))
    }
    map
}

def executeForm(context, params) {
    new HashMap()
}

/**
 * 计算 PV
 *
 * @param proName
 * @param valueId
 * @return
 */
def calPv(proName, valueId) {
    CategoryPropertyQueryService categoryPropertyService = categoryCenterContainer.getCategoryPropertyQueryService()
    Property property = categoryPropertyService.getPropertyByName(proName)
    if (property == null) return 0

    Value value = categoryPropertyService.getValueById(valueId)
    if (value == null) return 0

    PropertyValueUtil.mergePidVidToLong(property.getId(), value.getId())
}
