package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.shop.ShopTemplate;

/**
 * 店铺模板服务
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-7 上午12:50:10
 */
public interface ShopTemplateService {

    ShopTemplate getShopTemplateById(int id);

    ShopTemplate getShopTemplateByShopId(int shopId);

    int createShopTemplate(ShopTemplate shopTemplate);

    void updateShopTemplate(ShopTemplate shopTemplate);

    void deleteShopTemplate(int id);

    /**
     * 发布所有的全局模块，这样其实就是把所有编辑模式的模块参数全部build成一个pageStructure的xml结构
     * 存储到shopTemplate的globalModuleInfoConfig中
     *
     * @param shopTemplateId
     */
    void releaseAllGlobalCommonModule(int shopTemplateId);

}
