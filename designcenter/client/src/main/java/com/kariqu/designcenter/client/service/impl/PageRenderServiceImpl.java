package com.kariqu.designcenter.client.service.impl;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.factory.RenderContextFactory;
import com.kariqu.designcenter.client.domain.model.RenderContext;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.*;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;
import com.kariqu.designcenter.service.InitRenderService;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午07:55:29
 */

public class PageRenderServiceImpl implements InitRenderService, DebugRenderPageService, EditRenderPageService, PreviewRenderPageService, ProdRenderPageService, HeadFootRenderService {

    private final Log logger = LogFactory.getLog(PageRenderServiceImpl.class);

    @Autowired
    private RenderContextFactory renderContextFactory;

    @Autowired
    private RenderEngine renderEngine;

    @Autowired
    private ModuleContextExecutor moduleContextExecutor;


    @Autowired
    private URLBrokerFactory urlBrokerFactory;


    @Override
    public String initRenderPage(TemplatePage templatePage) {
        RenderContext initRenderContext = renderContextFactory.createInitPageRenderContext(templatePage);
        return initRenderContext.render();
    }

    @Override
    public String initRenderPagePrototype(PagePrototype pagePrototype) {
        RenderContext renderContext = renderContextFactory.createInitPagePrototypeRenderContext(pagePrototype);
        return renderContext.render();
    }

    @Override
    public String initRenderTemplateFooter(TemplateVersion templateVersion) {
        RenderContext initFooterContext = renderContextFactory.createInitFootRendContext(templateVersion);
        return initFooterContext.render();
    }

    @Override
    public String initRenderTemplateHeader(TemplateVersion templateVersion) {
        RenderContext initHeaderContext = renderContextFactory.createInitHeadRendContext(templateVersion);
        return initHeaderContext.render();
    }

    @Override
    public RenderResult debugRenderPage(int pageId, Map<String, Object> context) {
        RenderContext debugRenderContext = renderContextFactory.createDebugTemplatePageRenderContext(pageId, context);
        return new RenderResult(debugRenderContext.render(), debugRenderContext.getResultParams());
    }

    @Override
    public RenderResult debugPagePrototype(int pagePrototypeId, Map<String, Object> context) {
        RenderContext renderContext = renderContextFactory.createDebugPagePrototypeRenderContext(pagePrototypeId, context);
        return new RenderResult(renderContext.render(), renderContext.getResultParams());
    }

    @Override
    public RenderResult prodRenderPage(long pageId, Map<String, Object> context) {
        RenderContext prodRenderContext = renderContextFactory.createProdRenderContext(pageId, context);
        return new RenderResult(prodRenderContext.render(), prodRenderContext.getResultParams());
    }

    @Override
    public RenderResult prodRenderGlobalCommonModule(String moduleName, Map<String, Object> context) {
        RenderContext renderContext = renderContextFactory.createGlobalCommonModuleRenderContext(moduleName, context);
        return new RenderResult(renderContext.render(), renderContext.getResultParams());
    }


    @Override
    public RenderResult previewRenderPage(long pageId, Map<String, Object> context) {
        RenderContext previewRenderContext = renderContextFactory.createPreviewRenderContext(pageId, context);
        return new RenderResult(previewRenderContext.render(), previewRenderContext.getResultParams());
    }


    @Override
    public RenderResult editRenderPage(long pageId, Map<String, Object> context) {
        RenderContext editRenderContext = renderContextFactory.createEditRenderContext(pageId, context);
        return new RenderResult(editRenderContext.render(), editRenderContext.getResultParams());
    }

    @Override
    public String renderCommonModuleEditForm(Map<String, Object> context, Module module) {
        CommonModule commonModule = (CommonModule) module.getModulePrototype();
        String formVm = commonModule.getFormContent();
        Map<String, String> kv = new HashMap<String, String>();
        for (ModuleInstanceParam param : module.getParams()) {
            kv.put(param.getParamName(), param.getParamValue());
        }
        Map<String, Object> executorContext = moduleContextExecutor.executeForm(new HashMap<String, Object>(), module);
        if (executorContext == null) {
            executorContext = new HashMap<String, Object>();
            logger.warn("模块:" + module.getName() + "的脚本执行返回空map");
        }
        executorContext.putAll(kv);
        executorContext.putAll(context);
        executorContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        return renderEngine.render(formVm, executorContext);
    }

    @Override
    public String renderCommonModuleWithToolbar(String moduleInstanceId, int prototypeId, Map<String, Object> context, RenderConstants.RenderArea renderArea) {
        RenderContext renderModuleContext = renderContextFactory.createEditRenderCommonModuleContext(moduleInstanceId, prototypeId, context, renderArea);
        return renderModuleContext.render();
    }

    @Override
    public RenderResult renderHead(int shopId, Map<String, Object> context) {
        RenderContext headRenderContext = renderContextFactory.createHeadRenderContext(shopId, context);
        return new RenderResult(headRenderContext.render(), headRenderContext.getResultParams());
    }

    @Override
    public RenderResult renderFoot(int shopId, Map<String, Object> context) {
        RenderContext footRenderContext = renderContextFactory.createFootRenderContext(shopId, context);
        return new RenderResult(footRenderContext.render(), footRenderContext.getResultParams());
    }
}
