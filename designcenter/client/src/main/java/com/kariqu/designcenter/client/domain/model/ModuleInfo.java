package com.kariqu.designcenter.client.domain.model;

/**
 * 模块信息，主要用于在各种渲染模式下统一模块渲染时的信息
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午07:56:07
 */
public class ModuleInfo {

    private String name;

    private String version;

    private int domId;

    private boolean isTemplateModule;

    public ModuleInfo(String moduleName, String version, int domId) {
        this.isTemplateModule = false;
        this.name = moduleName;
        this.version = version;
        this.domId = domId;
    }

    public ModuleInfo(String moduleName, int domId) {
        this.isTemplateModule = true;
        this.name = moduleName;
        this.domId = domId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getDomId() {
        return domId;
    }

    public void setDomId(int domId) {
        this.domId = domId;
    }

    public boolean isTemplateModule() {
        return isTemplateModule;
    }

    public void setTemplateModule(boolean isTemplateModule) {
        this.isTemplateModule = isTemplateModule;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[模块名称=");
        result.append(name);
        if (null != version) {
            result.append("模块版本=");
            result.append(version);
        }
        result.append("domId=");
        result.append(domId);
        result.append("]");
        return result.toString();
    }
}
