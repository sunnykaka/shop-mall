package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.ResourceType;
import com.kariqu.designcenter.domain.model.prototype.TemplateResource;
import com.kariqu.designcenter.repository.TemplateResourceRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateResourceRepositoryImpl extends SqlMapClientDaoSupport implements TemplateResourceRepository {

    @Override
    public List<TemplateResource> queryTemplateResourcesByResourceType(int templateVersionId, ResourceType resourceType) {
        Map param = new HashMap();
        param.put("templateVersionId", templateVersionId);
        param.put("resourceType", resourceType);
        return this.getSqlMapClientTemplate().queryForList("queryTemplateResourcesByResourceType", param);
    }

    @Override
    public List<TemplateResource> queryTemplateResourcesByTemplateVersionId(int templateVersionId) {
        return getSqlMapClientTemplate().queryForList("queryTemplateResourcesByTemplateVersionId", templateVersionId);
    }

    @Override
    public Page<TemplateResource> queryTemplateResourcesByTemplateVersionIdAndPage(int templateVersionId,
                                                                                   Page<TemplateResource> page) {
        Map param = new HashMap();
        param.put("templateVersionId", templateVersionId);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<TemplateResource> result = this.getSqlMapClientTemplate().queryForList("queryTemplateResourcesByTemplateVersionIdAndPage", param);
        page.setResult(result);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForTemplateResource", templateVersionId));
        return page;
    }

    @Override
    public void createTemplateResource(TemplateResource templateResource) {
        this.getSqlMapClientTemplate().insert("insertTemplateResource", templateResource);
    }

    @Override
    public void deleteTemplateResourceById(int id) {
        this.getSqlMapClientTemplate().delete("deleteTemplateResource", id);
    }

    @Override
    public TemplateResource getTemplateResourceById(int id) {
        return (TemplateResource) getSqlMapClientTemplate().queryForObject("selectTemplateResource", id);
    }

    @Override
    public List<TemplateResource> queryAllTemplateResources() {
        return this.getSqlMapClientTemplate().queryForList("selectAllTemplateResources");
    }

    @Override
    public void updateTemplateResource(TemplateResource templateResource) {
        getSqlMapClientTemplate().update("updateTemplateResource", templateResource);
    }

    @Override
    public void deleteByTemplateVersionId(int templateVersionId) {
        this.getSqlMapClientTemplate().delete("deleteTemplateResourceByTemplateVersionId", templateVersionId);
    }

}
