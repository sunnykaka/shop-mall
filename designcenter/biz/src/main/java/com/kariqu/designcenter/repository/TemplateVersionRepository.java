package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-10 下午04:36:15
 * @version 1.0.0
 */
public interface TemplateVersionRepository{
    
    List<TemplateVersion> queryTemplateVersionsByTemplateId(int templateId);

    void updateTemplateVersion(TemplateVersion templateVersion);

    List<TemplateVersion> queryAllTemplateVersions();

    TemplateVersion getTemplateVersionById(int id);

    void deleteTemplateVersionById(int id);

    void createTemplateVersion(TemplateVersion templateVersion);
}
