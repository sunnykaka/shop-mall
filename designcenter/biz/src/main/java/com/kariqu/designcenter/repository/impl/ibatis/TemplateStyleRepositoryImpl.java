package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplateStyle;
import com.kariqu.designcenter.repository.TemplateStyleRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateStyleRepositoryImpl extends SqlMapClientDaoSupport implements TemplateStyleRepository {

    @Override
    public List<TemplateStyle> queryTemplateStylesByTemplateVersionId(int templateVersionId) {
        return this.getSqlMapClientTemplate().queryForList("queryTemplateStylesByTemplateVersionId", templateVersionId);
    }

    @Override
    public Page<TemplateStyle> queryTemplateStylesByTemplateVersionIdAndPage(int templateVersionId,
            Page<TemplateStyle> page) {
        Map param = new HashMap();
        param.put("templateVersionId", templateVersionId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<TemplateStyle> result = getSqlMapClientTemplate().queryForList("queryTemplateStylesByTemplateVersionIdAndPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForTemplateStyle", templateVersionId));
        return page;
    }

    @Override
    public void createTemplateStyle(TemplateStyle templateStyle) {
        this.getSqlMapClientTemplate().insert("insertTemplateStyle",templateStyle);
    }

    @Override
    public void deleteTemplateStyleById(int id) {
        this.getSqlMapClientTemplate().delete("deleteTemplateStyle", id);
    }

    @Override
    public TemplateStyle getTemplateStyleById(int id) {
        return (TemplateStyle) getSqlMapClientTemplate().queryForObject("selectTemplateStyle", id);
    }

    @Override
    public List<TemplateStyle> queryAllTemplateStyles() {
        return getSqlMapClientTemplate().queryForList("selectAllTemplateStyles");
    }

    @Override
    public void updateTemplateStyle(TemplateStyle templateStyle) {
        this.getSqlMapClientTemplate().update("updateTemplateStyle", templateStyle);
    }

    @Override
    public void deleteByTemplateVersionId(int templateVersionId) {
        this.getSqlMapClientTemplate().delete("deleteTemplateStyleByTemplateVersionId", templateVersionId);
    }
}
