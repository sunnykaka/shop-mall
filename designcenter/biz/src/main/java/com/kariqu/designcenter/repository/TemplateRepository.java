package com.kariqu.designcenter.repository;

import com.kariqu.designcenter.domain.model.prototype.Template;

import java.util.List;

/**
 * @author Tiger
 * @author Asion
 * @since 2011-4-4 下午07:53:39
 * @version 1.0
 */
public interface TemplateRepository{

    Template queryTemplateByName(String name);

    List<Template> queryAllTemplates();

    void updateTemplate(Template template);

    Template getTemplateById(int id);

    void deleteTemplateById(int id);

    void createTemplate(Template template);
}
