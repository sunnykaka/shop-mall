package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.repository.TemplateModuleRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateModuleRepositoryImpl extends SqlMapClientDaoSupport implements TemplateModuleRepository {

    @Override
    public List<TemplateModule> queryTemplateModulesByTemplateVersionId(int templateVersionId) {
        return this.getSqlMapClientTemplate().queryForList("queryTemplateModulesByTemplateVersionId", templateVersionId);
    }

    @Override
    public Page<TemplateModule> queryTemplateModulesByTemplateVersionIdAndPage(int templateVersionId,
                                                                               Page<TemplateModule> page) {
        Map param = new HashMap();
        param.put("templateVersionId", templateVersionId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<TemplateModule> result = this.getSqlMapClientTemplate().queryForList("queryTemplateModulesByTemplateVersionIdAndPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForTemplateModule", templateVersionId));
        return page;
    }

    @Override
    public void createTemplateModule(TemplateModule templateModule) {
        this.getSqlMapClientTemplate().insert("insertTemplateModule", templateModule);
    }

    @Override
    public void deleteTemplateModuleById(int id) {
        this.getSqlMapClientTemplate().delete("deleteTemplateModule", id);
    }

    @Override
    public TemplateModule getTemplateModuleById(int id) {
        return (TemplateModule) getSqlMapClientTemplate().queryForObject("selectTemplateModule", id);
    }

    @Override
    public List<TemplateModule> queryAllTemplateModules() {
        return this.getSqlMapClientTemplate().queryForList("selectAllTemplateModules");
    }

    @Override
    public void updateTemplateModule(TemplateModule templateModule) {
        this.getSqlMapClientTemplate().update("updateTemplateModule", templateModule);
    }

    @Override
    public List<TemplateModule> queryTemplateModulesByTemplateVersionIdAndName(String name, int templateVersionId) {
        Map param = new HashMap();
        param.put("templateVersionId", templateVersionId);
        param.put("name", name);
        return getSqlMapClientTemplate().queryForList("queryTemplateModulesByTemplateVersionIdAndName", param);
    }

    @Override
    public void deleteByTemplateVersionId(int templateVersionId) {
        this.getSqlMapClientTemplate().delete("deleteTemplateModuleByTemplateVersionId", templateVersionId);
    }

}
