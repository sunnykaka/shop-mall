package com.kariqu.designcenter.domain.model.prototype;


import com.kariqu.designcenter.domain.model.Renderable;

/**
 * 通用模块对象，此模块可以在不同的模板中使用
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2010-12-16 下午05:27:45
 */

public class CommonModule extends ModulePrototype implements Renderable {

    /** 模块描述 */
    private String caption;

    /**
     * 模块编辑界面对应的html内容
     */
    private String formContent;

    private String editFormContent;

    /**
     * 动态执行脚本
     */
    private String logicCode;

    /**
     * 编辑模式的模块逻辑代码，用户模块测试
     */
    private String editLogicCode;

    /**
     * 模块的js
     */
    private String moduleJs;

    /**
     * 编辑模式的模块js
     */
    private String editModuleJs;

    /**
     * 公共模块的类型
     */
    private ModuleGranularity moduleGranularity = ModuleGranularity.DEFAULT;

    public CommonModule() {
    }

    /**
     * 根据参数中的模块更新自己
     *
     * @param from
     */
    public void updateFrom(CommonModule from) {
        setCaption(from.getCaption());
        setEditFormContent(from.getEditFormContent());
        setEditLogicCode(from.getEditLogicCode());
        setModuleConfig(from.getModuleConfig());
        setEditModuleContent(from.getEditModuleContent());
        setEditModuleJs(from.getEditModuleJs());
        setModuleCssContent(from.getModuleCssContent());
        setModuleGranularity(from.getModuleGranularity());
    }

    /**
     * @param prototypeId
     * @param name
     */
    public CommonModule(int prototypeId, String name) {
        super(prototypeId, name);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String getContent() {
        return this.getModuleContent();
    }

    public String getFormContent() {
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
        this.editFormContent = formContent;
    }

    public String getLogicCode() {
        return logicCode;
    }

    public void setLogicCode(String logicCode) {
        this.logicCode = logicCode;
        this.editLogicCode = logicCode;
    }

    public String getEditFormContent() {
        return editFormContent;
    }

    public void setEditFormContent(String editFormContent) {
        this.editFormContent = editFormContent;
    }

    public String getEditLogicCode() {
        return editLogicCode;
    }

    public void setEditLogicCode(String editLogicCode) {
        this.editLogicCode = editLogicCode;
    }

    public String getModuleJs() {
        return moduleJs;
    }

    public void setModuleJs(String moduleJs) {
        this.moduleJs = moduleJs;
    }

    public String getEditModuleJs() {
        return editModuleJs;
    }

    public void setEditModuleJs(String editModuleJs) {
        this.editModuleJs = editModuleJs;
    }


    public void setModuleGranularity(ModuleGranularity moduleGranularity) {
        this.moduleGranularity = moduleGranularity;
    }


    public ModuleGranularity getModuleGranularity() {
        return moduleGranularity;
    }

    public boolean isGlobalGranularity() {
        return ModuleGranularity.GLOBAL.equals(moduleGranularity);
    }
}
