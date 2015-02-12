package com.kariqu.designcenter.domain.model;

import com.kariqu.designcenter.domain.model.prototype.ModulePrototype;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * 模块实例对象，代表页面上的一个模块
 * 模块实例的ID必须唯一，模块实例可能是同一个模块在同一位置以不同DomId存在
 * 或者在不同位置出现
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2010-12-15 下午07:18:48
 */
public class Module implements Renderable, Serializable {

    private static final long serialVersionUID = 929012736556598741L;

    private String moduleInstanceId;

    private ModulePrototype modulePrototype;

    private Toolbar toolbar;

    private List<ModuleInstanceParam> params = new ArrayList<ModuleInstanceParam>();

    public Module(String moduleInstanceId, ModulePrototype modulePrototype) {
        this.modulePrototype = modulePrototype;
        this.moduleInstanceId = moduleInstanceId;
    }

    @Override
    public String getContent() {
        return MessageFormat.format("<div class=\"e_module\" $!TOOLBAR>{0}</div>", modulePrototype.getModuleContent());
    }

    @Override
    public Map<String, Object> getContext() {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (params != null) {
            for (ModuleInstanceParam param : params) {
                paramsMap.put(param.getParamName(), param.getParamValue());
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RenderConstants.MODULE_PARAM_REF, paramsMap);
        return result;
    }

    /**
     * @return 模块名称
     */
    public String getName() {
        return modulePrototype.getName();
    }

    /**
     * @return the modulePrototype
     */
    public ModulePrototype getModulePrototype() {
        return modulePrototype;
    }

    /**
     * @param modulePrototype the modulePrototype to set
     */
    public void setModulePrototype(ModulePrototype modulePrototype) {
        this.modulePrototype = modulePrototype;
    }


    /**
     * @return the params
     */
    public List<ModuleInstanceParam> getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(List<ModuleInstanceParam> params) {
        this.params = params;
    }

    /**
     * @param param
     */
    public void addParam(ModuleInstanceParam param) {
        this.params.add(param);
    }

    /**
     * 判断模块原型是否是模板模块
     *
     * @return
     */
    public boolean isTemplateModule() {
        return modulePrototype.getClass().isAssignableFrom(TemplateModule.class);
    }

    /**
     * 获取需要及时渲染的模块占位符
     *
     * @return
     */
    public String getInTimeRenderPlaceHolder() {
        return "<inTimeRender moduleId=\"" + this.getModuleInstanceId() + "\"/>";
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }


    public String getModuleInstanceId() {
        return moduleInstanceId;
    }

    public void setModuleInstanceId(String moduleInstanceId) {
        this.moduleInstanceId = moduleInstanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Module module = (Module) o;

        if (moduleInstanceId != null ? !moduleInstanceId.equals(module.moduleInstanceId) : module.moduleInstanceId != null)
            return false;
        if (modulePrototype != null ? !modulePrototype.equals(module.modulePrototype) : module.modulePrototype != null)
            return false;
        if (params != null ? !params.equals(module.params) : module.params != null) return false;
        if (toolbar != null ? !toolbar.equals(module.toolbar) : module.toolbar != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleInstanceId != null ? moduleInstanceId.hashCode() : 0;
        result = 31 * result + (modulePrototype != null ? modulePrototype.hashCode() : 0);
        result = 31 * result + (toolbar != null ? toolbar.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }

    public void addParams(List<ModuleInstanceParam> moduleInstanceParams) {
        this.params.addAll(moduleInstanceParams);
    }

    /**
     * 用于装修和预览模式的时候，模块渲染出错的时候，仅仅输出toolbar和异常信息，不输出实际的内容
     *
     * @return
     */
    public String getOnlyModuleToolbarContent(String wishedModuleContent) {
        return MessageFormat.format("<div class=\"e_module\" $!TOOLBAR><div style=\"text-align:center;height:100px\">{0}</div></div>",wishedModuleContent);

    }
}
