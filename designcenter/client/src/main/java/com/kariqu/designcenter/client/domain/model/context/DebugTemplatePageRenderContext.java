package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.exception.DomIdRepeatException;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;
import com.kariqu.designcenter.service.CommonModuleService;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 制作模板的过程中，调试模板的渲染上下文 ,主要用来调试TemplatePage
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-4 下午09:48:02
 */
public class DebugTemplatePageRenderContext extends DomCheckRenderContext {

    protected final Log logger = LogFactory.getLog(getClass());

    private TemplatePage templatePage;

    private TemplateVersion templateVersion;

    private List<TemplateModule> templateModules;

    private CommonModuleService commonModuleService;

    private ModuleContextExecutor moduleContextExecutor;


    public DebugTemplatePageRenderContext(RenderEngine renderEngine, TemplatePage templatePage,
                                          List<TemplateModule> templateModules, TemplateVersion templateVersion) {
        super(renderEngine);
        this.templatePage = templatePage;
        this.templateModules = templateModules;
        this.templateVersion = templateVersion;
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> modules) {
        StringBuilder regionContent = new StringBuilder();
        for (ModuleInfo moduleInfo : modules) {
            if (moduleInfo.isTemplateModule()) {
                regionContent.append(renderTemplateModule(moduleInfo.getName(), moduleInfo.getDomId()));
            } else {
                regionContent.append(renderCommonModule(moduleInfo.getName(), moduleInfo.getVersion(), moduleInfo
                        .getDomId()));
            }
        }
        return wrapperRegion(regionContent.toString());
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        checkDomId(domId, renderArea);
        TemplateModule templateModule = getTemplateModuleByName(name);
        if (templateModule == null)
            return "不存在模块，moduleName=" + name;
        /**
         * DEBUG渲染模式下，渲染模块的编辑模式的字段
         */
        Map<String, Object> context = new HashMap<String, Object>(getAllContext());
        context.putAll(templateModule.getContext());
        String moduleContent;
        try {
            moduleContent = renderEngine.render(wrapperModule(templateModule.getEditModuleContent()), context);
        } catch (Exception e) {
            logger.error("渲染模板模块出现异常", e);
            moduleContent = ExceptionUtils.getFullStackTrace(e);
        }
        return moduleContent;
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        checkDomId(domId, renderArea);
        try {
            CommonModule commonModule = commonModuleService.getCommonModuleForRendering(name, version);
            Map<String, Object> context = moduleContextExecutor.executeForDebug(getAllContext(), commonModule);
            if (context == null) {
                context = new HashMap<String, Object>();
                logger.warn("模块:" + name + "的脚本执行返回空map");
            }
            context.putAll(commonModule.getContext());
            context.putAll(getAllContext());
            return renderEngine.render(wrapperModule(commonModule.getEditModuleContent()), context);
        } catch (Exception e) {
            logger.error("公共模块渲染错误", e);
            return ExceptionUtils.getFullStackTrace(e);
        }
    }

    @Override
    public String doRender() {
        Map<String, Object> allContext = getAllContext();
        this.renderArea = RenderConstants.RenderArea.head;
        String header = renderEngine.render(templateVersion.getHeadContent(), allContext);
        this.renderArea = RenderConstants.RenderArea.foot;
        String footer = renderEngine.render(templateVersion.getFootContent(), allContext);
        this.renderArea = RenderConstants.RenderArea.body;
        String body = renderEngine.render(templatePage, allContext);
        StringBuilder pageContent = new StringBuilder();
        pageContent.append(header);
        pageContent.append(body);
        pageContent.append(footer);
        if (!isValid()) {
            throw new DomIdRepeatException("页面DomID出现重复，请查看:" + getDomIdError());
        }
        return pageContent.toString();

    }

    /**
     * @param name
     * @return
     */
    private TemplateModule getTemplateModuleByName(String name) {
        for (TemplateModule templateModule : templateModules) {
            if (templateModule.getName().equals(name))
                return templateModule;
        }
        return null;
    }

    public void setCommonModuleService(CommonModuleService commonModuleService) {
        this.commonModuleService = commonModuleService;
    }

    public void setModuleContextExecutor(ModuleContextExecutor moduleContextExecutor) {
        this.moduleContextExecutor = moduleContextExecutor;
    }
}
