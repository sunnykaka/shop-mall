package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.shop.ShopTemplate;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-6 下午11:07:04
 * @version 1.0.0
 */
public interface ShopTemplateRepository {
    
    ShopTemplate getShopTemplateByShopId(int shopId);

    void createShopTemplate(ShopTemplate shopTemplate);

    void deleteShopTemplateById(int id);

    ShopTemplate getShopTemplateById(int id);

    List<ShopTemplate> queryAllShopTemplates();

    void updateShopTemplate(ShopTemplate shopTemplate);
}
