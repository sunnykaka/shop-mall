package com.kariqu.designcenter.domain.model.prototype;

import com.kariqu.designcenter.domain.exception.ModuleConfigException;
import com.kariqu.designcenter.domain.model.ModelConfig;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.util.ModuleParamAndXmlConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 模块原型
 * 即模块还没被实例化时的初始状态对象
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-16 上午10:12:48
 */
public abstract class ModulePrototype extends ModelConfig {

    private Log logger = LogFactory.getLog(this.getClass());

    private int id;

    /**
     * 模块的内容，如果是vm模块则是vm,PHP模块，则是php
     */
    private String moduleContent;

    /**
     * 编辑模式，可用来修复bug以及在测试环境使用，这时产品渲染不会看到这个内容
     */
    private String editModuleContent;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 模块语言，可以是JSP,VM,PHP
     */
    private String language;

    /**
     * 模块版本
     */
    private String version;

    /**
     * 模块的xml配置，其中包括参数和配置
     */
    private String moduleConfig;

    private String moduleConfigKey;

    private String moduleCssContent;

    /**
     * 模块参数
     */
    private List<Parameter> params;


    public ModulePrototype() {

    }

    public ModulePrototype(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Map<String, Object> getContext() {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        for (Parameter param : getParams()) {
            paramsMap.put(param.getName(), param.getValue());
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RenderConstants.MODULE_PARAM_REF, paramsMap);
        return result;
    }

    /**
     * 根据模块的配置文件解析出参数
     *
     * @return the params
     */
    public List<Parameter> getParams() {
        if (params != null) {
            return params;
        } else if (StringUtils.isNotEmpty(moduleConfig)) {
            try {
                this.params = ModuleParamAndXmlConverter.convertXmlToParameters(moduleConfig);
            } catch (ModuleConfigException e) {
                logger.error("不能解析模块配置文件，系统将返回空参数配置", e);
                params = Collections.emptyList();
            }
        } else {
            params = Collections.emptyList();
        }
        return params;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    public String getEditModuleContent() {
        return editModuleContent;
    }

    public void setEditModuleContent(String editModuleContent) {
        this.editModuleContent = editModuleContent;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the moduleContent
     */
    public String getModuleContent() {
        return moduleContent;
    }

    /**
     * @param moduleContent the moduleContent to set
     */
    public void setModuleContent(String moduleContent) {
        this.moduleContent = moduleContent;
        this.editModuleContent = moduleContent;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the moduleConfig
     */
    public String getModuleConfig() {
        return moduleConfig;
    }

    /**
     * @param moduleConfig the moduleConfig to set
     */
    public void setModuleConfig(String moduleConfig) {
        this.moduleConfig = moduleConfig;
    }

    /**
     * @return the moduleConfigKey
     */
    public String getModuleConfigKey() {
        return moduleConfigKey;
    }

    /**
     * @param moduleConfigKey the moduleConfigKey to set
     */
    public void setModuleConfigKey(String moduleConfigKey) {
        this.moduleConfigKey = moduleConfigKey;
    }

    /**
     * @return the moduleCssContent
     */
    public String getModuleCssContent() {
        return moduleCssContent;
    }

    /**
     * @param moduleCssContent the moduleCssContent to set
     */
    public void setModuleCssContent(String moduleCssContent) {
        this.moduleCssContent = moduleCssContent;
    }

    /**
     * @return
     */
    public boolean isCacheable() {
        String configValue = getConfigValue(RenderConstants.IS_CACHEABLE);
        return StringUtils.isBlank(configValue) || configValue.equals("true");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModulePrototype that = (ModulePrototype) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
