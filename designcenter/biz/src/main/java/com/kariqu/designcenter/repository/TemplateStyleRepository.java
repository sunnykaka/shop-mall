package com.kariqu.designcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplateStyle;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-11 下午11:54:02
 * @version 1.0.0
 */
public interface TemplateStyleRepository{
    
    List<TemplateStyle> queryTemplateStylesByTemplateVersionId(int templateVersionId);
    
    Page<TemplateStyle> queryTemplateStylesByTemplateVersionIdAndPage(int templateVersionId,Page<TemplateStyle> page);

    void deleteByTemplateVersionId(int templateVersionId);

    void updateTemplateStyle(TemplateStyle templateStyle);

    List<TemplateStyle> queryAllTemplateStyles();

    TemplateStyle getTemplateStyleById(int id);

    void deleteTemplateStyleById(int id);

    void createTemplateStyle(TemplateStyle templateStyle);
}
