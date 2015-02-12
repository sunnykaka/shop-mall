package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.designcenter.client.domain.model.AbstractRenderContext;
import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceIdFactory;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.RenderConstants.RenderArea;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.List;
import java.util.Map;

/**
 * 预览模式渲染上下文
 * 预览模式是在用户的装修后台的渲染模式，所有数据多从编辑模式以及数据库中读取
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-5 下午01:34:09
 */
public class PreviewRenderContext extends AbstractRenderContext {


    protected PageStructure pageStructure;

    protected ShopTemplate shopTemplate;

    protected ShopPage shopPage;

    protected RenderArea rendArea;

    protected ModuleContextExecutor moduleContextExecutor;


    public PreviewRenderContext(RenderEngine renderEngine, PageStructure pageStructure, ShopTemplate shopTemplate,
                                ShopPage shopPage) {
        super(renderEngine);
        this.pageStructure = pageStructure;
        this.shopTemplate = shopTemplate;
        this.shopPage = shopPage;
    }

    @Override
    public String doRender() {
        Map<String, Object> allContext = getAllContext();
        String headContent = shopTemplate.getEditHeadContent();
        String footContent = shopTemplate.getEditFootContent();
        String bodyContent = shopPage.getEditPageContent();
        StringBuilder pageContent = new StringBuilder();
        rendArea = RenderArea.head;
        pageContent.append(renderEngine.render(headContent, allContext));
        rendArea = RenderArea.body;
        pageContent.append(renderEngine.render(bodyContent, allContext));
        rendArea = RenderArea.foot;
        pageContent.append(renderEngine.render(footContent, allContext));
        return pageContent.toString() + renderPageJavaScript();
    }

    private String renderPageJavaScript() {
        String js = RenderUtil.buildPageJavaScript(pageStructure);
        if (StringUtils.isNotBlank(js))
            return "<script type=\"text/javascript\">" + js + "</script>";

        return StringUtils.EMPTY;
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        try {
            Map<String, Object> allContext = getAllContext();
            switch (rendArea) {
                case head: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfHead(shopPage.getShopId(), Integer.toString(domId));
                    Module module = pageStructure.getFixedHeadModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                case body: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfBody(shopPage.getId(), Integer.toString(domId), shopPage.getShopId());
                    Module module = pageStructure.getFixedBodyModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                case foot: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfFoot(shopPage.getShopId(), Integer.toString(domId));
                    Module module = pageStructure.getFixedFootModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
            }
        } catch (Exception e) {
            logger.error("渲染公共模块出错，模块名称=" + name + "模块版本=" + version + "domId" + domId, e);
        }
        return StringUtils.EMPTY;

    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        switch (rendArea) {
            case head: {
                List<Module> modules = pageStructure.getModulesFromRegionOfHead(regionName);
                return wrapperRegion(renderModules(modules));
            }
            case body: {
                List<Module> modules = pageStructure.getModulesFromRegionOfBody(regionName);
                return wrapperRegion(renderModules(modules));
            }
            case foot: {
                List<Module> modules = pageStructure.getModulesFromRegionOfFoot(regionName);
                return wrapperRegion(renderModules(modules));
            }
            default:
                return "renderRegion-渲染区域未指定";
        }
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        try {
            Map<String, Object> allContext = getAllContext();
            switch (rendArea) {
                case head: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfHead(shopPage.getShopId(), Integer.toString(domId));
                    Module module = pageStructure.getFixedHeadModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                case body: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfBody(shopPage.getId(), Integer.toString(domId), shopPage.getShopId());
                    Module module = pageStructure.getFixedBodyModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                case foot: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfFoot(shopPage.getShopId(), Integer.toString(domId));
                    Module module = pageStructure.getFixedFootModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                default:
                    return "未指定渲染位置";
            }
        } catch (Exception e) {
            logger.error("渲染模板模块，模板名称=" + name + "domId=" + domId, e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 子类可以覆盖此方法
     *
     * @param context
     * @param module
     * @return
     */
    protected String renderModule(Map<String, Object> context, Module module) {
        try {
            if (module.isTemplateModule()) {
                return renderEngine.render(module, context);
            } else {
                Map<String, Object> moduleContext = moduleContextExecutor.execute(context, module);
                if (moduleContext != null) {
                    moduleContext.putAll(context);
                }
                return renderEngine.render(module, moduleContext);
            }
        } catch (Exception e) {
            logger.error("渲染模块出错", e);
            String content = renderEngine.render(module.getOnlyModuleToolbarContent("模块渲染出错，可能是参数格式错误，" +
                    "请检查参数。如果最终不能解决问题，请联系技术部门，谢谢"), context);

            return content + "<div style=\"display:none\">" + ExceptionUtils.getFullStackTrace(e) + "</div>";
        }
    }

    protected final String renderModules(List<Module> modules) {
        Map<String, Object> allContext = getAllContext();
        StringBuilder regionContent = new StringBuilder();
        for (Module module : modules) {
            regionContent.append(renderModule(allContext, module));
        }
        return regionContent.toString();
    }

    public void setModuleContextExecutor(ModuleContextExecutor moduleContextExecutor) {
        this.moduleContextExecutor = moduleContextExecutor;
    }

}
