package com.kariqu.designcenter.domain.model.prototype;

import com.kariqu.designcenter.domain.model.Renderable;

import java.io.Serializable;

/**
 * 模板对应的模块领域对象，此类型的模块只能在某一个模板中使用，不能
 * 在所有模板公用
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-16 下午05:16:20
 */
public class TemplateModule extends ModulePrototype implements Renderable, Serializable {

    private static final long serialVersionUID = -1739005957837970018L;

    /**
     * 模块所属模板版本
     */
    private int templateVersionId;


    public TemplateModule() {
    }

    /**
     * 根据参数中的模块更新自己
     * @param templateModule
     */
    public void updateFrom(TemplateModule templateModule) {
        this.setEditModuleContent(templateModule.getEditModuleContent());
        this.setModuleConfig(templateModule.getModuleConfig());
    }


    /**
     * @param prototypeId
     * @param name
     */
    public TemplateModule(int prototypeId, String name) {
        super(prototypeId, name);
    }

    /**
     * @return the templateVersionId
     */
    public int getTemplateVersionId() {
        return templateVersionId;
    }


    /**
     * @param templateVersionId the templateVersionId to set
     */
    public void setTemplateVersionId(int templateVersionId) {
        this.templateVersionId = templateVersionId;
    }


    @Override
    public String getContent() {
        return this.getModuleContent();
    }

}
