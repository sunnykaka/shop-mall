package com.kariqu.designcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.ResourceType;
import com.kariqu.designcenter.domain.model.prototype.TemplateResource;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-10 下午04:35:24
 * @version 1.0.0
 */
public interface TemplateResourceRepository{
    
    List<TemplateResource> queryTemplateResourcesByTemplateVersionId(int templateVersionId);
    
    Page<TemplateResource> queryTemplateResourcesByTemplateVersionIdAndPage(int templateVersionId,Page<TemplateResource> page);
    
    List<TemplateResource> queryTemplateResourcesByResourceType(int templateVersionId,ResourceType resourceType);

    void deleteByTemplateVersionId(int templateVersionId);

    List<TemplateResource> queryAllTemplateResources();

    void updateTemplateResource(TemplateResource templateResource);

    TemplateResource getTemplateResourceById(int id);

    void deleteTemplateResourceById(int id);

    void createTemplateResource(TemplateResource templateResource);
}
