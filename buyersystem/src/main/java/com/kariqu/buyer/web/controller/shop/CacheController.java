package com.kariqu.buyer.web.controller.shop;

import com.kariqu.common.cache.CacheService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.util.VersionUtil;
import com.kariqu.designcenter.service.CacheKeyFactory;
import com.kariqu.designcenter.service.ShopPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 缓存控制器，用于管理缓存，有这样几类缓存：
 * 1，Domain对象，ShopPage,我们有三种页面，首页，列表页，详情页，这三个对象分别用两种key进行了缓存
 * 一是店铺，一是对象ID
 * 2,渲染出来的页面，头尾和body，头尾，首页和列表页多是用店铺为key来缓存的
 * 3,渲染每个模板页用的PageStructure缓存,缓存键是ShopPage对象的Id
 * 4,详情页的缓存通过版本号进行缓存
 * <p/>
 * 所有缓存会在装修完毕发布店铺的时候被清空，这些代码在ShopPageService中
 * <p/>
 * User: Asion
 * Date: 11-11-10
 * Time: 下午4:37
 */
@Controller
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ShopPageService shopPageService;


    /**
     * 转到缓存控制页
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache")
    public String toCacheVm(Model model) {
        return "cache";
    }

    /**
     * 移除全部缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/clearAll")
    public
    @ResponseBody
    String removeAllCache() {
        cacheService.removeAll();
        return "success";
    }

    /**
     * 清除首页的ShopPage对象缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/index/shopPage")
    public
    @ResponseBody
    String removeIndexShopPage() {
        String cacheKey = CacheKeyFactory.createIndexShopPageKey(RenderConstants.shopId);
        cacheService.remove(cacheKey);
        ShopPage shopPage = shopPageService.queryIndexShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()));
        return "success";
    }

    /**
     * 清除列表页的ShopPage对象缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/search/shopPage")
    public
    @ResponseBody
    String removeSearchShopPage() {
        String cacheKey = CacheKeyFactory.createSearchListShopPageKey(RenderConstants.shopId);
        cacheService.remove(cacheKey);
        ShopPage shopPage = shopPageService.queryIndexShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()));
        return "success";
    }

    /**
     * 清除详情页的ShopPage对象缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/detail/shopPage")
    public
    @ResponseBody
    String removeDetailShopPage() {
        String cacheKey = CacheKeyFactory.createDetailShopPageKey(RenderConstants.shopId);
        cacheService.remove(cacheKey);
        ShopPage shopPage = shopPageService.queryDetailShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()));
        return "success";
    }

    /**
     * 清除头尾的内容缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/content/headfoot")
    public
    @ResponseBody
    String removeHeadAndFootContent() {
        String cacheKey = CacheKeyFactory.createShopHeadAndFootContentKey(RenderConstants.shopId);
        cacheService.remove(cacheKey);
        return "success";
    }

    /**
     * 清除首页内容缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/content/index")
    public
    @ResponseBody
    String removeIndexContent() {
        ShopPage shopPage = shopPageService.queryIndexShopPage(RenderConstants.shopId);
        String cacheKey = CacheKeyFactory.createShopPageContentKey(shopPage.getId());
        cacheService.remove(cacheKey);
        return "success";
    }

    /**
     * 清除列表内容缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/content/search")
    public
    @ResponseBody
    String removeSearchContent() {
        ShopPage shopPage = shopPageService.querySearchListShopPage(RenderConstants.shopId);
        String cacheKey = CacheKeyFactory.createShopPageContentKey(shopPage.getId());
        cacheService.remove(cacheKey);
        return "success";
    }

    /**
     * 清除频道页内容缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/content/channel")
    public
    @ResponseBody
    String removeChannelContent() {
        ShopPage shopPage = shopPageService.queryChannelShopPage(RenderConstants.shopId);
        String cacheKey = CacheKeyFactory.createShopPageContentKey(shopPage.getId());
        cacheService.remove(cacheKey);
        return "success";
    }

    /**
     * 清除详情页内容缓存
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/content/detail")
    public
    @ResponseBody
    String removeDetailContent() {
        ShopPage shopPage = shopPageService.queryDetailShopPage(RenderConstants.shopId);
        shopPage.setConfigValue(RenderConstants.CACHE_VERSION, VersionUtil.upgradeVersion(shopPage.getConfigValue(RenderConstants.CACHE_VERSION)));
        return "success";
    }

    /**
     * 清除首页的结构配置对象
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/pageStructure/index")
    public
    @ResponseBody
    String removeIndexPageStructure() {
        ShopPage shopPage = shopPageService.queryIndexShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createWholePageStructureKey(shopPage.getId()));
        return "success";
    }

    /**
     * 清除列表的结构配置对象
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/pageStructure/search")
    public
    @ResponseBody
    String removeSearchStructure() {
        ShopPage shopPage = shopPageService.querySearchListShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createWholePageStructureKey(shopPage.getId()));
        return "success";
    }

    /**
     * 清除列表的结构配置对象
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/pageStructure/channel")
    public
    @ResponseBody
    String removeChannelStructure() {
        ShopPage shopPage = shopPageService.queryChannelShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createWholePageStructureKey(shopPage.getId()));
        return "success";
    }

    /**
     * 清除详情页的结构配置对象
     *
     * @return
     */
    @RequestMapping(value = "/shop/cache/pageStructure/detail")
    public
    @ResponseBody
    String removeDetailStructure() {
        ShopPage shopPage = shopPageService.queryDetailShopPage(RenderConstants.shopId);
        cacheService.remove(CacheKeyFactory.createWholePageStructureKey(shopPage.getId()));
        return "success";
    }


}
