package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.exception.DomIdRepeatException;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.Renderable;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.service.CommonModuleService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 初始页面尾部配置文件渲染上下文
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午09:10:52
 */
public class InitFootRenderContext extends InitRenderContext {

    protected final Log logger = LogFactory.getLog(getClass());


    private TemplateVersion templateVersion;

    public InitFootRenderContext(RenderEngine renderEngine, TemplateVersion templateVersion,
                                 List<TemplateModule> templateModules,CommonModuleService commonModuleService) {
        super(renderEngine, templateModules,commonModuleService);
        this.templateVersion = templateVersion;
        this.renderArea = RenderConstants.RenderArea.foot;
    }

    @Override
    public String render() {
        renderEngine.render(new Renderable() {

                    @Override
                    public String getContent() {
                        return templateVersion.getFootContent();
                    }

                    @Override
                    public Map<String, Object> getContext() {
                        return Collections.emptyMap();
                    }

                }, getAllContext());
        if (!isValid()) {
            throw new DomIdRepeatException("页面DomID出现重复，请查看:" + getDomIdError());
        }
        return PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure);
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        checkDomId(domId, RenderConstants.RenderArea.foot);
        CommonModule commonModule = this.commonModuleService.getCommonModuleForRendering(name, version);
        if (null == commonModule) {
            throw new RenderException("模块 :" + name + " 不存在");
        }
        Module module = new Module(Integer.toString(domId), commonModule);
        module.setParams(convert(commonModule.getParams()));
        pageStructure.addFootFixedRegionModule(module);
        return null;
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        pageStructure.addFootRegion(toRegion(regionName, moduleInfoList));
        return null;
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        checkDomId(domId, RenderConstants.RenderArea.foot);
        if (templateModules.containsKey(name)) {
            Module module = new Module(Integer.toString(domId), templateModules
                    .get(name));
            module.setParams(convert(templateModules.get(name).getParams()));
            pageStructure.addFootFixedRegionModule(module);
        }
        return null;
    }
}
