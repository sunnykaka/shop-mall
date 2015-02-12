package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.repository.CommonModuleRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonModuleRepositoryImpl extends SqlMapClientDaoSupport implements CommonModuleRepository {

    @Override
    public List<CommonModule> queryCommonModuleByIds(List<Integer> ids) {
        List<CommonModule> commonModules = new ArrayList<CommonModule>();
        for (Integer id : ids)
            commonModules.add(getCommonModuleById(id));
        return commonModules;
    }

    @Override
    public List<CommonModule> queryAllGlobalCommonModules() {
        return getSqlMapClientTemplate().queryForList("selectAllGlobalCommonModules");
    }

    @Override
    public CommonModule queryCommonModuleByNameAndVersion(String name, String version) {
        Map param = new HashMap();
        param.put("name", name);
        param.put("version", version);
        return (CommonModule) getSqlMapClientTemplate().queryForObject("queryCommonModuleByNameAndVersion", param);
    }

    @Override
    public CommonModule queryCommonModuleByName(String name) {
        return (CommonModule) getSqlMapClientTemplate().queryForObject("queryCommonModuleByName", name);
    }

    @Override
    public void createCommonModule(CommonModule commonModule) {
        getSqlMapClientTemplate().insert("insertCommonModule", commonModule);
    }

    @Override
    public void deleteCommonModuleById(int id) {
        getSqlMapClientTemplate().delete("deleteCommonModule", id);
    }

    @Override
    public CommonModule getCommonModuleById(int id) {
        return (CommonModule) getSqlMapClientTemplate().queryForObject("selectCommonModule", id);
    }

    @Override
    public List<CommonModule> queryAllCommonModules() {
        return getSqlMapClientTemplate().queryForList("selectAllCommonModules");
    }

    @Override
    public void updateCommonModule(CommonModule commonModule) {
        getSqlMapClientTemplate().update("updateCommonModule", commonModule);
    }

}
