package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;
import com.kariqu.designcenter.repository.TemplateVersionRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

public class TemplateVersionRepositoryImpl extends SqlMapClientDaoSupport implements TemplateVersionRepository {

    @Override
    public List<TemplateVersion> queryTemplateVersionsByTemplateId(int templateId) {
        return this.getSqlMapClientTemplate().queryForList("queryTemplateVersionsByTemplateId", templateId);
    }

    @Override
    public void createTemplateVersion(TemplateVersion templateVersion) {
        this.getSqlMapClientTemplate().insert("insertTemplateVersion",templateVersion);
    }

    @Override
    public void deleteTemplateVersionById(int id) {
        this.getSqlMapClientTemplate().delete("deleteTemplateVersion",id);
    }

    @Override
    public TemplateVersion getTemplateVersionById(int id) {
        return (TemplateVersion) getSqlMapClientTemplate().queryForObject("selectTemplateVersion", id);
    }

    @Override
    public List<TemplateVersion> queryAllTemplateVersions() {
        return this.getSqlMapClientTemplate().queryForList("selectAllTemplateVersions");
    }

    @Override
    public void updateTemplateVersion(TemplateVersion templateVersion) {
        this.getSqlMapClientTemplate().update("updateTemplateVersion", templateVersion);
    }

}
