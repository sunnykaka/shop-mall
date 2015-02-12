package com.kariqu.designcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;

import java.util.List;

/**
 * @author Tiger
 * @since 2011-4-4 下午07:53:07
 * @version 1.0
 */
public interface TemplatePageRepository {
    
    TemplatePage queryTemplatePageByPageNameAndTemplateVersionId(String pageName, int templateVersionId);
    
    List<TemplatePage> queryTemplatePagesByTemplateVersionId(int templateVersionId);
    
    Page<TemplatePage> queryTemplatePagesByTemplateVersionIdAndPage(int templateVersionId,Page<TemplatePage> page);
    
    TemplatePage queryIndexTemplatePage(int templateVersionId);

    TemplatePage querySearchListTemplatePage(int templateVersionId);

    TemplatePage queryDetailTemplatePage(int templateVersionId);

    void deleteByTemplateVersionId(int templateVersionId);


    void createTemplatePage(TemplatePage templatePage);

    void deleteTemplatePageById(int id);

    TemplatePage getTemplatePageById(int id);

    List<TemplatePage> queryAllTemplatePages();

    void updateTemplatePage(TemplatePage templatePage);
}
