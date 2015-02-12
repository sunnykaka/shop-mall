package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;

import java.util.List;
import java.util.Map;

/**
 * 模块实例化参数服务
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-6 下午11:44:09
 */
public interface ModuleInstanceParamService {

    void instanceShopPageParam(ShopPage shopPage);

    void instanceShopHeadParams(ShopTemplate shopTemplate);

    void instanceShopFootParams(ShopTemplate shopTemplate);

    /**
     * 根据店铺ID和模块实例ID查询头部参数
     *
     * @param moduleInstanceId
     * @return
     */
    List<ModuleInstanceParam> queryModuleParamsByModuleInstanceId(String moduleInstanceId);


    /**
     * 创建实例参数
     *
     * @param moduleInstanceParam
     */
    void createModuleInstanceParam(ModuleInstanceParam moduleInstanceParam);

    /**
     * 创建模块实例化参数
     *
     * @param params
     */
    void createModuleInstanceParams(List<ModuleInstanceParam> params);

    /**
     * 更新单个参数
     *
     * @param moduleInstanceParam
     */
    void updateModuleInstanceParam(ModuleInstanceParam moduleInstanceParam);

    /**
     * 批量更新参数
     *
     * @param params
     */
    void updateModuleInstanceParams(List<ModuleInstanceParam> params);


    /**
     * 根据一个Map中的参数值对更新实例参数
     *
     * @param parameterMap
     * @param modulePrototypeId
     */
    void updateModuleInstanceParams(String moduleInstanceId, Map<String, String> parameterMap, int modulePrototypeId);

    /**
     * 删除店铺头尾的参数,头尾参数pageId为null
     *
     * @param shopId
     */

    void deleteShopHeadAndFootParamByShopId(int shopId);


    /**
     * 删除页面body部分的参数
     *
     * @param id
     */
    void deletePageParamsByPageId(long id);

    /**
     * 删除店铺头部参数
     *
     * @param shopId
     */
    void deleteShopHeadParamByShopId(int shopId);

    /**
     * 删除店铺尾部参数，根据店铺ID
     *
     * @param shopId
     */
    void deleteShopFootParamByShopId(int shopId);

    /**
     * 删除单个模块的模块实例参数
     *
     * @param moduleInstanceId
     */
    void deleteModuleInstanceParamOfSingleModule(String moduleInstanceId);

    /**
     * 删除模块参数
     *
     * @param waitToDeleteParam
     */
    void deleteModuleInstanceParam(ModuleInstanceParam waitToDeleteParam);

    /**
     * 批量删除模块参数
     *
     * @param waitToDeleteParams
     */
    void deleteModuleInstanceParams(List<ModuleInstanceParam> waitToDeleteParams);
}
