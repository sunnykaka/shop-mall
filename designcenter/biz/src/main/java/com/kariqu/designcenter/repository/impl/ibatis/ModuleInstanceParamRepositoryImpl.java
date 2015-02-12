package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.ParamType;
import com.kariqu.designcenter.repository.ModuleInstanceParamRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-30 下午07:24:42
 */
public class ModuleInstanceParamRepositoryImpl extends SqlMapClientDaoSupport implements ModuleInstanceParamRepository {


    @Override
    public List<ModuleInstanceParam> queryModuleParamsByModuleInstanceId(String moduleInstanceId) {
        return getSqlMapClientTemplate().queryForList("queryModuleParamsByModuleInstanceId", moduleInstanceId);
    }

    @Override
    public void deleteShopHeadParamByShopId(int shopId) {
        Map param = new HashMap();
        param.put("shopId", shopId);
        param.put("paramType", ParamType.HEAD);
        this.getSqlMapClientTemplate().delete("deleteShopParamsByShopIdAndParamType", param);
    }

    @Override
    public void deleteShopFootParamByShopId(int shopId) {
        Map param = new HashMap();
        param.put("shopId", shopId);
        param.put("paramType", ParamType.FOOT);
        this.getSqlMapClientTemplate().delete("deleteShopParamsByShopIdAndParamType", param);
    }

    @Override
    public void deleteModuleInstanceParamOfSingleModule(String moduleInstanceId) {
        this.getSqlMapClientTemplate().delete("deleteModuleInstanceParamOfSingleModule", moduleInstanceId);
    }

    @Override
    public void createModuleInstanceParam(ModuleInstanceParam moduleInstanceParam) {
        this.getSqlMapClientTemplate().insert("insertModuleInstanceParam", moduleInstanceParam);
    }

    @Override
    public void deletePageParamsByPageId(long pageId) {
        this.getSqlMapClientTemplate().delete("deletePageParamsByPageId", pageId);
    }

    @Override
    public void deleteShopHeadAndFootParamByShopId(int shopId) {
        this.getSqlMapClientTemplate().delete("deleteShopHeadAndFootParamByShopId", shopId);
    }

    @Override
    public void deleteModuleInstanceParamById(Long id) {
        this.getSqlMapClientTemplate().delete("deleteModuleInstanceParam", id);
    }

    @Override
    public ModuleInstanceParam getModuleInstanceParamById(Long id) {
        return (ModuleInstanceParam) this.getSqlMapClientTemplate().queryForObject("selectModuleInstanceParamById", id);
    }

    @Override
    public void updateModuleInstanceParam(ModuleInstanceParam moduleInstanceParam) {
        this.getSqlMapClientTemplate().update("updateModuleInstanceParam", moduleInstanceParam);
    }

}
