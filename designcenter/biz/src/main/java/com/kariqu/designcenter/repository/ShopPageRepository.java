package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopPageType;

import java.util.List;

/**
 * 店铺页面资源库
 * 
 * @author Tiger
 * @author Asion
 * @since 2010-12-21 下午03:25:00
 * @version 1.0
 */

public interface ShopPageRepository{

    List<ShopPage> queryShopPageWithShopPageType(int shopId, ShopPageType shopPageType);
    
    List<ShopPage> queryShopPagesByShopId(int shopId);
    
    ShopPage queryIndexShopPage(int shopId);

    ShopPage querySearchListShopPage(int shopId);

    ShopPage queryDetailShopPage(int shopId);

    ShopPage queryDetailIntegralShopPage(int shopId);

    ShopPage queryChannelShopPage(int shopId);

    ShopPage queryMealSetShopPage(int shopId);

    void createShopPage(ShopPage shopPage);

    void deleteShopPageById(Long id);

    ShopPage getShopPageById(Long id);

    List<ShopPage> queryAllShopPages();

    void updateShopPage(ShopPage shopPage);
}
