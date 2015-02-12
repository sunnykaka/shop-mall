package com.kariqu.designcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;

import java.util.List;

/**
 * @author Tiger
 * @since 2011-4-4 下午07:54:18
 * @version 1.0
 */
public interface TemplateModuleRepository{

    List<TemplateModule> queryTemplateModulesByTemplateVersionId(int templateVersionId);
    
    Page<TemplateModule> queryTemplateModulesByTemplateVersionIdAndPage(int templateVersionId,Page<TemplateModule> page);
    
    List<TemplateModule> queryTemplateModulesByTemplateVersionIdAndName(String name,int templateVersionId);

    void deleteByTemplateVersionId(int templateVersionId);

    void createTemplateModule(TemplateModule templateModule);

    void deleteTemplateModuleById(int id);

    TemplateModule getTemplateModuleById(int id);

    List<TemplateModule> queryAllTemplateModules();

    void updateTemplateModule(TemplateModule templateModule);
}
