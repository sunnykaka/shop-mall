package com.kariqu.cmscenter.domain;

/**
 * 栏目模板
 * <p/>
 * 可用来渲染菜单和栏目的首页
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-24
 *        Time: 上午10:07
 */
public class RenderTemplate {

    private int id;

    private String name;

    private String templateContent;

    private TemplateType templateType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TemplateType getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }
}
