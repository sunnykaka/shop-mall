package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.repository.TemplatePageRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplatePageRepositoryImpl extends SqlMapClientDaoSupport implements TemplatePageRepository {

    @Override
    public TemplatePage queryIndexTemplatePage(int templateVersionId) {
        return (TemplatePage) this.getSqlMapClientTemplate().queryForObject("queryIndexTemplatePage", templateVersionId);
    }

    @Override
    public TemplatePage querySearchListTemplatePage(int templateVersionId) {
        return (TemplatePage) this.getSqlMapClientTemplate().queryForObject("querySearchListTemplatePage", templateVersionId);
    }

    @Override
    public TemplatePage queryDetailTemplatePage(int templateVersionId) {
        return (TemplatePage) this.getSqlMapClientTemplate().queryForObject("queryDetailTemplatePage", templateVersionId);
    }

    @Override
    public TemplatePage queryTemplatePageByPageNameAndTemplateVersionId(String pageName, int templateVersionId) {
        Map param = new HashMap();
        param.put("pageName", pageName);
        param.put("templateVersionId", templateVersionId);
        return (TemplatePage) this.getSqlMapClientTemplate().queryForObject("queryTemplatePageByPageNameAndTemplateVersionId", param);
    }

    @Override
    public List<TemplatePage> queryTemplatePagesByTemplateVersionId(int templateVersionId) {
        return this.getSqlMapClientTemplate().queryForList("queryTemplatePagesByTemplateVersionId", templateVersionId);
    }

    @Override
    public Page<TemplatePage> queryTemplatePagesByTemplateVersionIdAndPage(int templateVersionId,
                                                                           Page<TemplatePage> page) {
        Map param = new HashMap();
        param.put("templateVersionId", templateVersionId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<TemplatePage> result = this.getSqlMapClientTemplate().queryForList("queryTemplatePagesByTemplateVersionIdAndPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForTemplatePage", templateVersionId));
        return page;
    }

    @Override
    public void createTemplatePage(TemplatePage templatePage) {
        this.getSqlMapClientTemplate().insert("insertTemplatePage", templatePage);
    }

    @Override
    public void deleteTemplatePageById(int id) {
        getSqlMapClientTemplate().delete("deleteTemplatePage", id);
    }

    @Override
    public TemplatePage getTemplatePageById(int id) {
        return (TemplatePage) this.getSqlMapClientTemplate().queryForObject("selectTemplatePage", id);
    }

    @Override
    public List<TemplatePage> queryAllTemplatePages() {
        return this.getSqlMapClientTemplate().queryForList("selectAllTemplatePages");
    }

    @Override
    public void updateTemplatePage(TemplatePage templatePage) {
        this.getSqlMapClientTemplate().update("updateTemplatePage", templatePage);
    }

    @Override
    public void deleteByTemplateVersionId(int templateVersionId) {
        this.getSqlMapClientTemplate().delete("deleteTemplatePageByTemplateVersionId", templateVersionId);
    }

}
