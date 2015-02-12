package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.domain.exception.DomIdRepeatException;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.Region;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.service.CommonModuleService;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化PagePrototype的渲染上下文
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-19 上午10:24
 */
public class InitPagePrototypeRenderContext extends DomCheckRenderContext {

    private PagePrototype pagePrototype;

    private PageStructure pageStructure = new PageStructure();

    private CommonModuleService commonModuleService;

    public InitPagePrototypeRenderContext(PagePrototype pagePrototype, RenderEngine renderEngine, CommonModuleService commonModuleService) {
        super(renderEngine);
        this.pagePrototype = pagePrototype;
        this.commonModuleService = commonModuleService;
    }


    @Override
    public String render() {
        renderEngine.render(pagePrototype.getPageCode(), getAllContext());
        if (!isValid()) {
            throw new DomIdRepeatException("页面DomID 出现重复,请查看:" + getDomIdError());
        }
        return PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure);
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        throw new UnsupportedOperationException("页面原型上下文这种不能引入模板模块");
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        CommonModule commonModule = this.commonModuleService.getCommonModuleForRendering(name, version);
        if (null == commonModule) {
            throw new RenderException("模块 :" + name + " 不存在");
        }
        Module module = new Module(Integer.toString(domId), commonModule);
        module.setParams(RenderUtil.convert(commonModule.getParams()));
        switch (pagePrototype.getAreaType()) {
            case HEAD: {
                this.renderArea = RenderConstants.RenderArea.head;
                checkDomId(domId, RenderConstants.RenderArea.head);
                pageStructure.addHeadFixedRegionModule(module);
                break;
            }
            case BODY: {
                this.renderArea = RenderConstants.RenderArea.body;
                checkDomId(domId, RenderConstants.RenderArea.body);
                pageStructure.addBodyFixedRegionModule(module);
                break;
            }
            case FOOT: {
                this.renderArea = RenderConstants.RenderArea.foot;
                checkDomId(domId, RenderConstants.RenderArea.foot);
                pageStructure.addFootFixedRegionModule(module);
                break;
            }
            default:
                return "渲染的页面原型区域不合法";
        }
        return null;
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        switch (pagePrototype.getAreaType()) {
            case HEAD: {
                pageStructure.addHeadRegion(toRegion(regionName, moduleInfoList));
                break;
            }
            case BODY: {
                pageStructure.addBodyRegion(toRegion(regionName, moduleInfoList));
                break;
            }
            case FOOT: {
                pageStructure.addFootRegion(toRegion(regionName, moduleInfoList));
                break;
            }
            default:
                return "渲染的页面原型区域不合法";
        }
        return null;
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
                throw new UnsupportedOperationException("");
            } else {
                final CommonModule commonModule = commonModuleService.getCommonModuleForRendering(moduleInfo.getName(), moduleInfo
                        .getVersion());
                if (null == commonModule) {
                    throw new RenderException("模块 :" + moduleInfo.getName() + " 不存在");
                }
                module = new Module(Integer.toString(moduleInfo.getDomId()),
                        commonModule);
                module.setParams(RenderUtil.convert(commonModule.getParams()));
            }
            modules.add(module);
        }
        return new Region(regionName, modules);
    }
}
