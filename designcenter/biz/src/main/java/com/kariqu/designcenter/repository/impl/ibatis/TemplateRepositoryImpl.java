package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.Template;
import com.kariqu.designcenter.repository.TemplateRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

public class  TemplateRepositoryImpl extends SqlMapClientDaoSupport implements TemplateRepository {
    @Override
    public Template queryTemplateByName(String name) {
        return (Template) getSqlMapClientTemplate().queryForObject("queryTemplateByName",name);
    }

    @Override
    public void createTemplate(Template template) {
        getSqlMapClientTemplate().insert("insertTemplate",template);
    }

    @Override
    public void deleteTemplateById(int id) {
        getSqlMapClientTemplate().delete("deleteTemplate", id);
    }

    @Override
    public Template getTemplateById(int id) {
        return (Template)getSqlMapClientTemplate().queryForObject("selectTemplate", id);
    }

    @Override
    public List<Template> queryAllTemplates() {
        return getSqlMapClientTemplate().queryForList("selectAllTemplates");
    }

    @Override
    public void updateTemplate(Template template) {
        getSqlMapClientTemplate().update("updateTemplate", template);
    }

}
