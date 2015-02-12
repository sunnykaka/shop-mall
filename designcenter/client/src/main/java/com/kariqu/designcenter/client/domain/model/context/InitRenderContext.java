package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.Region;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.Parameter;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.service.CommonModuleService;

import java.util.*;

/**
 * 初始化渲染场景，用于渲染配置文件
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午10:13:16
 */
public abstract class InitRenderContext extends DomCheckRenderContext {

    protected PageStructure pageStructure = new PageStructure();

    protected Map<String, TemplateModule> templateModules;

    protected CommonModuleService commonModuleService;

    protected InitRenderContext(RenderEngine renderEngine, List<TemplateModule> templateModules, CommonModuleService commonModuleService) {
        super(renderEngine);
        this.templateModules = convertToMap(templateModules);
        this.commonModuleService = commonModuleService;
    }

    /**
     * 页面结构文件由坑组成，这里提供生成坑的方法
     *
     * @param regionName
     * @param moduleInfoList
     * @return
     */
    protected Region toRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        List<Module> modules = new ArrayList<Module>(moduleInfoList.size());
        for (ModuleInfo moduleInfo : moduleInfoList) {
            checkDomId(moduleInfo.getDomId(), renderArea);
            Module module;
            if (moduleInfo.isTemplateModule()) {
                if (templateModules.containsKey(moduleInfo.getName())) {
                    module = new Module(Integer.toString(moduleInfo.getDomId()),
                            templateModules.get(moduleInfo.getName()));
                    module.setParams(convert(templateModules.get(moduleInfo.getName()).getParams()));
                } else {
                    throw new RenderException("模块 :" + moduleInfo.getName() + " 不存在");
                }
            } else {
                final CommonModule commonModule = commonModuleService.getCommonModuleForRendering(moduleInfo.getName(), moduleInfo
                        .getVersion());
                if (null == commonModule) {
                    throw new RenderException("模块 :" + moduleInfo.getName() + " 不存在");
                }
                module = new Module(Integer.toString(moduleInfo.getDomId()),
                        commonModule);
                module.setParams(convert(commonModule.getParams()));
            }
            modules.add(module);
        }
        return new Region(regionName, modules);
    }


    /**
     * @param templateModules
     * @return
     */
    private Map<String, TemplateModule> convertToMap(List<TemplateModule> templateModules) {
        Map<String, TemplateModule> map = new HashMap<String, TemplateModule>();
        for (TemplateModule templateModule : templateModules)
            map.put(templateModule.getName(), templateModule);
        return map;
    }

    /**
     * @param params
     * @return
     */
    protected List<ModuleInstanceParam> convert(List<Parameter> params) {
        return RenderUtil.convert(params);
    }

}
