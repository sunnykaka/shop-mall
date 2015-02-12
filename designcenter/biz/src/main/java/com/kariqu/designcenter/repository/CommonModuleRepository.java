package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.prototype.CommonModule;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-16 下午08:33:26
 * @version 1.0.0
 */
public interface CommonModuleRepository {
    
    CommonModule queryCommonModuleByNameAndVersion(String name, String version);

    CommonModule queryCommonModuleByName(String name);
    
    List<CommonModule> queryCommonModuleByIds(List<Integer> ids);

    List<CommonModule> queryAllGlobalCommonModules();

    void createCommonModule(CommonModule commonModule);

    void deleteCommonModuleById(int id);

    CommonModule getCommonModuleById(int id);

    List<CommonModule> queryAllCommonModules();

    void updateCommonModule(CommonModule commonModule);
}
