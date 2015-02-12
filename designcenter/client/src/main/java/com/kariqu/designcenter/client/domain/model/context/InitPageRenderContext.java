package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.exception.DomIdRepeatException;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.service.CommonModuleService;

import java.util.List;

/**
 * 初始化页面配置文件渲染上下文
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-4 下午08:30:56
 */
public class InitPageRenderContext extends InitRenderContext {


    private TemplatePage templatePage;

    public InitPageRenderContext(RenderEngine renderEngine, TemplatePage templatePage,
                                 List<TemplateModule> templateModules, CommonModuleService commonModuleService) {
        super(renderEngine, templateModules, commonModuleService);
        this.templatePage = templatePage;
        this.renderArea = RenderConstants.RenderArea.body;
    }

    @Override
    public String render() {
        renderEngine.render(templatePage, getAllContext());
        if (!isValid()) {
            throw new DomIdRepeatException("页面DomID出现重复，请查看:" + getDomIdError());
        }
        pageStructure.setPrototypeId(templatePage.getId());
        return PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure);
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        checkDomId(domId, RenderConstants.RenderArea.body);
        CommonModule commonModule = this.commonModuleService.getCommonModuleForRendering(name, version);
        if (null == commonModule) {
            throw new RenderException("模块 :" + name + " 不存在");
        }
        Module module = new Module(Integer.toString(domId), commonModule);
        module.setParams(convert(commonModule.getParams()));
        pageStructure.addBodyFixedRegionModule(module);
        return null;
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        pageStructure.addBodyRegion(toRegion(regionName, moduleInfoList));
        return null;
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        checkDomId(domId, RenderConstants.RenderArea.body);
        if (templateModules.containsKey(name)) {
            Module module = new Module(Integer.toString(domId), templateModules.get(name));
            module.setParams(convert(templateModules.get(name).getParams()));
            pageStructure.addBodyFixedRegionModule(module);
        }
        return null;
    }

}
