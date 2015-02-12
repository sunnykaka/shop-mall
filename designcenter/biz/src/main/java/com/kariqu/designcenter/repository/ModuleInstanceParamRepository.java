package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.ModuleInstanceParam;

import java.util.List;

/**
 * 模块实例参数的存储仓库，必须支持以shopId分表
 * @author Asion
 * @since 2011-4-7 下午05:54:23
 * @version 1.0.0
 */
public interface ModuleInstanceParamRepository {

    void createModuleInstanceParam(ModuleInstanceParam moduleInstanceParam);

    ModuleInstanceParam getModuleInstanceParamById(Long id);

    void updateModuleInstanceParam(ModuleInstanceParam moduleInstanceParam);

    List<ModuleInstanceParam> queryModuleParamsByModuleInstanceId(String moduleInstanceId);

    void deleteModuleInstanceParamById(Long id);

    void deletePageParamsByPageId(long pageId);
    
    void deleteShopHeadAndFootParamByShopId(int shopId);

    void deleteShopHeadParamByShopId(int shopId);

    void deleteShopFootParamByShopId(int shopId);

    void deleteModuleInstanceParamOfSingleModule(String moduleInstanceId);
}
