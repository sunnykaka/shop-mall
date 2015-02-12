package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;

/**
 * 缓存键工厂类
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-10 下午06:27:04
 */
public class CacheKeyFactory {

    private static final String headAndFootContentKey = "shop-head-foot-content-";

    private static final String headContentKey = "shop-head-content-";

    private static final String footContentKey = "shop-foot-content-";

    private static final String pageContentKey = "shop-page-content-";

    private static final String shopSearchPage = "shop-search-page-";

    private static final String shopIndexPage = "shop-index-page-";

    private static final String shopMarketingIndexPage = "shop-marketing-page-";

    private static final String shopDetailIntegralPage = "shop-detail-integral-page-";

    private static final String shopDetailPage = "shop-detail-page-";

    private static final String shopChannelPage = "shop-channel-page-";

    private static final String shopMealSetPage = "shop-meal-page-";

    public static String createShopHeadAndFootContentKey(int shopId) {
        return headAndFootContentKey + shopId;
    }

    public static String createShopHeadContentKey(int shopId) {
        return headContentKey + shopId;
    }

    public static String createShopFootContentKey(int shopId) {
        return footContentKey + shopId;
    }

    public static String createShopPageContentKey(long shopPageId) {
        return pageContentKey + shopPageId;
    }

    public static String createDetailShopPageKey(int shopId) {
        return shopDetailPage + shopId;
    }

    public static String createDetailIntegralShopPageKey(int shopId) {
        return shopDetailIntegralPage + shopId;
    }

    public static String createChannelShopPageKey(int shopId) {
        return shopChannelPage + shopId;
    }

    public static String createMealSetShopPageKey(int shopId) {
        return shopMealSetPage + shopId;
    }

    public static String createWholePageStructureKey(long pageId) {
        return PageStructure.class.getName() + pageId;
    }

    public static String createShopPageCacheKey(long shopPageId) {
        return ShopPage.class.getName() + shopPageId;
    }


    public static String createShopTemplateCacheKeyWithShopId(int shopId) {
        return ShopTemplate.class.getName() + "shopId" + shopId;
    }

    public static String createShopTemplateCacheKey(int shopTemplateId) {
        return ShopTemplate.class.getName() + shopTemplateId;
    }

    public static String createSearchListShopPageKey(int shopId) {
        return shopSearchPage + shopId;
    }

    public static String createIndexShopPageKey(int shopId) {
        return shopIndexPage + shopId;
    }

    public static String createMarketingShopPageKey(int shopId) {
        return shopMarketingIndexPage + shopId;
    }

}
