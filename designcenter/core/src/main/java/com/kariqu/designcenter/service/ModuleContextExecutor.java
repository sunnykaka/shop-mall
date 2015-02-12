package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用Groovy脚本实现的模块执行
 *
 * @Author: Tiger
 * @Since: 11-7-16 下午4:51
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class ModuleContextExecutor {

    private static final Log logger = LogFactory.getLog(ModuleContextExecutor.class);

    private static final String EXECUTE_METHOD = "execute";

    private static final String EXECUTE_FORM_METHOD = "executeForm";

    @Autowired
    private Binding binding;

    /**
     * 缓存groovy的编译脚本
     */
    private Map<String, Script> scriptsCache = new ConcurrentHashMap<String, Script>();

    private boolean developMode = false;

    /**
     * Groovy Binding
     *
     * @param binding bind
     */
    public void setBinding(Binding binding) {
        this.binding = binding;
        this.binding.setVariable("log", LogFactory.getLog("GroovyScript"));
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, Object> execute(Map<String, Object> context, Module module) {
        preconditionCheck(module);
        Script script = getScriptFromCache(module);
        return (Map<String, Object>) script.invokeMethod(EXECUTE_METHOD, new Object[]{context, module.getContext().get(RenderConstants.MODULE_PARAM_REF)});
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> executeForm(Map<String, Object> context, Module module) {
        preconditionCheck(module);
        Script script = getScriptFromCache(module);
        return (Map<String, Object>) script.invokeMethod(EXECUTE_FORM_METHOD, new Object[]{context, module.getContext().get(RenderConstants.MODULE_PARAM_REF)});
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, Object> executeForDebug(Map<String, Object> context, CommonModule commonModule) {
        Script script = parseScript(commonModule.getName(), commonModule.getEditLogicCode());
        return (Map<String, Object>) script.invokeMethod(EXECUTE_METHOD, new Object[]{context, commonModule.getContext().get(RenderConstants.MODULE_PARAM_REF)});
    }


    public void resetModuleScript(CommonModule commonModule) {
        if (null == commonModule || StringUtils.isEmpty(commonModule.getLogicCode())) {
            return;
        }
        final String cachekey = getCommonModuleCachekey(commonModule.getName(), commonModule.getVersion());
        Script script = parseScript(cachekey, commonModule.getLogicCode());
        if (script != null) {
            this.scriptsCache.put(cachekey, script);
        }
    }

    private String getCommonModuleCachekey(String name, String version) {
        return name + "." + version;
    }

    private Script parseScript(String key, String scriptCode) {
        try {
            GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
            Class scriptClass;
            if (StringUtils.isBlank(key)) {
                scriptClass = groovyClassLoader.parseClass(scriptCode);
            } else {
                scriptClass = groovyClassLoader.parseClass(scriptCode, key);
            }
            return InvokerHelper.createScript(scriptClass, binding);
        } catch (Throwable e) {
            logger.error("解析groovy模块失败，cachekey=" + key, e);
            return null;
        }
    }

    private void preconditionCheck(Module module) {
        if (!module.getModulePrototype().getClass().equals(CommonModule.class)) {
            throw new UnsupportedOperationException("不支持非公共模块调用ModuleContextMediatorImpl.execute方法");
        }
    }

    private Script getScriptFromCache(Module module) {
        final CommonModule commonModule = (CommonModule) module.getModulePrototype();
        final String moduleName = module.getName();
        final String version = module.getModulePrototype().getVersion();
        final String cachekey = getCommonModuleCachekey(moduleName, version);
        //如果是开发模式就每次解析
        if (developMode) {
            return parseScript(cachekey, commonModule.getLogicCode());
        }
        if (!scriptsCache.containsKey(cachekey)) {
            scriptsCache.put(cachekey, parseScript(cachekey, commonModule.getLogicCode()));
        }
        return scriptsCache.get(cachekey);
    }

    public void setDevelopMode(boolean developMode) {
        this.developMode = developMode;
    }
}

