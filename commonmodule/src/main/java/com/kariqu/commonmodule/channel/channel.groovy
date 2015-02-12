package com.kariqu.commonmodule.channel

import com.kariqu.categorycenter.domain.service.CategoryAssociationService
import com.kariqu.searchengine.domain.OrderBy
import com.kariqu.searchengine.domain.ProductQuery
import com.kariqu.searchengine.domain.SortBy
import org.apache.commons.lang.math.NumberUtils

def execute(context, params) {
    def map = new HashMap()
    def categoryService = context.get('categoryService')

    def cid = context.get('cid')
    if (cid == null) cid = 262

    // 当前类目的基本数据
    map.put("channelData", categoryService.queryNavigateCategoryById(cid))

    // 以二级类目为根节点的层级
    map.put("categoryList", categoryService.buildCategory(cid, false, "y".equalsIgnoreCase(params.get("categorySort"))))

    // 当前类目包括的品牌信息
    map.put("brandData", categoryService.getBrandUrlByChannel(cid))

    // 当前前台类目对应的所有后台类目
    CategoryAssociationService categoryAssociationService = categoryCenterContainer.getCategoryAssociationService()
    def cids = categoryAssociationService.queryAssociationByNavCategoryId(cid)

    // 热销推荐
    def hotNum = NumberUtils.toInt(params.get("hotNum"))
    ProductQuery hotQuery = new ProductQuery()
    hotQuery.setCategoryIds(cids)
    hotQuery.setSort(SortBy.valuation.toString())
    hotQuery.setOrder(OrderBy.desc.toString())
    hotQuery.setGroupField(ProductQuery.SearchSchemaField.ID)
    hotQuery.setPageSize(String.valueOf(hotNum > 0 ? hotNum : 8))
    def hotProductList = searchEngineQuery.queryProductsByQuery(hotQuery).getProducts()
    map.put("hotProductList", hotProductList)

    // 限时优惠
    def limitNum = NumberUtils.toInt(params.get("limitNum"))
    ProductQuery limitQuery = new ProductQuery()
    limitQuery.setCategoryIds(cids)
    limitQuery.setJoinActivity(true)
    limitQuery.setGroupField(ProductQuery.SearchSchemaField.ID)
    limitQuery.setPageSize(String.valueOf(limitNum > 0 ? limitNum : 4))
    def limitProductList = searchEngineQuery.queryProductsByQuery(limitQuery).getProducts()
    map.put("limitProductList", limitProductList)

    // 新品上市
    def newNum = NumberUtils.toInt(params.get("newNum"))
    ProductQuery newQuery = new ProductQuery()
    newQuery.setCategoryIds(cids)
    newQuery.setSort(SortBy.time.toString())
    newQuery.setOrder(OrderBy.desc.toString())
    newQuery.setGroupField(ProductQuery.SearchSchemaField.ID)
    newQuery.setPageSize(String.valueOf(newNum > 0 ? newNum : 4))
    def newProductList = searchEngineQuery.queryProductsByQuery(newQuery).getProducts()
    map.put("newProductList", newProductList)

    map
}

def executeForm(context, params) {
    new HashMap()
}
