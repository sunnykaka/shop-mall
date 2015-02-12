package com.kariqu.designcenter.client.domain.factory.impl;

import com.kariqu.common.cache.CacheService;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.factory.RenderContextFactory;
import com.kariqu.designcenter.client.domain.model.RenderContext;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.client.domain.model.context.*;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.prototype.*;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.service.*;
import com.kariqu.productcenter.service.ProductPictureResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author Tiger
 * @version 1.0
 * @since 2010-12-21 下午03:15:15
 */

public class RenderContextFactoryImpl implements RenderContextFactory {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private RenderEngine renderEngine;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CommonModuleService commonModuleService;

    @Autowired
    private PageStructureBuilder pageStructureBuilder;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private ShopTemplateService shopTemplateService;

    @Autowired
    private PagePrototypeService pagePrototypeService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ModuleContextExecutor moduleContextExecutor;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private CommonModuleContainer commonModuleContainer;

    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    @Autowired
    private ProductPictureResolver productPictureResolver;

    /**
     * 放置在vm里通过指令可调用的一些类，比如开放的类目服务，产品服务，类似如下配置
     * <bean id="prodOpenApiContext" class="org.springframework.beans.factory.config.MapFactoryBean">
     * <property name="targetMapClass" value="java.util.HashMap"/>
     * <property name="sourceMap">
     * <map key-type="java.lang.String">
     * <entry key="categoryService" value-ref="prodCategoryService"/>
     * <entry key="productService" value-ref="prodProductService"/>
     * </map>
     * </property>
     * </bean>
     */
    private Map<String, Object> prodOpenApiContext;

    @Override
    public RenderContext createInitPageRenderContext(TemplatePage templatePage) {
        List<TemplateModule> templateModules = templateService
                .queryTemplateModulesByTemplateVersionId(templatePage.getTemplateVersionId());
        return new InitPageRenderContext(renderEngine, templatePage, templateModules, commonModuleService);
    }


    @Override
    public RenderContext createInitFootRendContext(TemplateVersion templateVersion) {
        List<TemplateModule> templateModules = templateService.queryTemplateModulesByTemplateVersionId(templateVersion
                .getId());
        return new InitFootRenderContext(renderEngine, templateVersion, templateModules, commonModuleService);
    }


    @Override
    public RenderContext createInitHeadRendContext(TemplateVersion templateVersion) {
        List<TemplateModule> templateModules = templateService.queryTemplateModulesByTemplateVersionId(templateVersion
                .getId());
        return new InitHeadRenderContext(renderEngine, templateVersion, templateModules, commonModuleService);
    }

    @Override
    public RenderContext createInitPagePrototypeRenderContext(PagePrototype pagePrototype) {
        return new InitPagePrototypeRenderContext(pagePrototype, renderEngine, commonModuleService);
    }

    @Override
    public RenderContext createDebugTemplatePageRenderContext(int pageId, Map<String, Object> context) {
        TemplatePage templatePage = templateService.getTemplatePageById(pageId);
        if (templatePage == null) {
            String message = "创建debug渲染上下文出错，模板页面为空，模板页面Id=" + pageId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        List<TemplateModule> templateModules = templateService.queryTemplateModulesByTemplateVersionId(templatePage
                .getTemplateVersionId());
        TemplateVersion templateVersion = templateService.getTemplateVersionById(templatePage.getTemplateVersionId());
        DebugTemplatePageRenderContext debugRenderContext = new DebugTemplatePageRenderContext(renderEngine, templatePage, templateModules,
                templateVersion);

        context.put(RenderConstants.RENDER_MOD, RenderConstants.RenderMod.debug);

        debugRenderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        debugRenderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        debugRenderContext.setCommonModuleService(commonModuleService);
        debugRenderContext.setModuleContextExecutor(moduleContextExecutor);
        debugRenderContext.putGlobalParameter(context);
        debugRenderContext.setOpenApiContext(prodOpenApiContext);

        debugRenderContext.putResultParam(RenderConstants.GLOBAL_CSS_KEY, String.valueOf(templateVersion.getGlobalCssId()));
        debugRenderContext.putResultParam(RenderConstants.GLOBAL_JS_KEY, String.valueOf(templateVersion.getGlobalJsId()));
        debugRenderContext.putResultParam(RenderConstants.STYLE_KEY, String.valueOf(templateVersion.getDefaultStyleId()));

        return debugRenderContext;
    }

    @Override
    public RenderContext createEditRenderContext(long pageId, Map<String, Object> context) {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            String message = "创建编辑模式渲染上下文出错，店铺页面为空，页面Id=" + pageId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        context.put(RenderConstants.RENDER_MOD, RenderConstants.RenderMod.design);

        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopPage.getShopId());
        PageStructure pageStructure = pageStructureBuilder.buildEditModePageStructure(shopPage, shopTemplate);
        EditRenderContext editRenderContext = new EditRenderContext(renderEngine, pageStructure, shopTemplate, shopPage);

        editRenderContext.setUrlBrokerFactory(urlBrokerFactory);
        editRenderContext.setModuleContextExecutor(moduleContextExecutor);
        editRenderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        editRenderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        editRenderContext.putGlobalParameter(context);
        editRenderContext.setOpenApiContext(prodOpenApiContext);

        editRenderContext.putResultParam(RenderConstants.SHOP_ID_CONTEXT_KEY, String.valueOf(shopPage.getShopId()));
        editRenderContext.putResultParam(RenderConstants.GLOBAL_CSS_KEY, String.valueOf(shopTemplate.getId()));
        editRenderContext.putResultParam(RenderConstants.GLOBAL_JS_KEY, String.valueOf(shopTemplate.getId()));
        editRenderContext.putResultParam(RenderConstants.STYLE_KEY, String.valueOf(shopTemplate.getId()));

        // 编辑页面的 css
        editRenderContext.putResultParam(RenderConstants.CSS_ID_CONTEXT_KEY, RenderUtil.buildPageCss(pageStructure));

        return editRenderContext;
    }

    @Override
    public RenderContext createPreviewRenderContext(long pageId, Map<String, Object> context) {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            String message = "创建预览模式上下文出错，店铺页面为空，页面Id=" + pageId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        context.put(RenderConstants.RENDER_MOD, RenderConstants.RenderMod.preview);

        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopPage.getShopId());
        PageStructure pageStructure = pageStructureBuilder.buildPreviewModePageStructure(shopPage, shopTemplate);
        PreviewRenderContext previewRenderContext = new PreviewRenderContext(renderEngine,
                pageStructure, shopTemplate, shopPage);

        previewRenderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        previewRenderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        previewRenderContext.setModuleContextExecutor(moduleContextExecutor);
        previewRenderContext.putGlobalParameter(context);
        previewRenderContext.setOpenApiContext(prodOpenApiContext);

        previewRenderContext.putResultParam(RenderConstants.SHOP_ID_CONTEXT_KEY, String.valueOf(shopPage.getShopId()));
        previewRenderContext.putResultParam(RenderConstants.GLOBAL_CSS_KEY, String.valueOf(shopTemplate.getId()));
        previewRenderContext.putResultParam(RenderConstants.GLOBAL_JS_KEY, String.valueOf(shopTemplate.getId()));
        previewRenderContext.putResultParam(RenderConstants.STYLE_KEY, String.valueOf(shopTemplate.getId()));
        // 预览页面的 css
        previewRenderContext.putResultParam(RenderConstants.CSS_ID_CONTEXT_KEY, RenderUtil.buildPageCss(pageStructure));

        return previewRenderContext;
    }

    @Override
    public RenderContext createProdRenderContext(long pageId, Map<String, Object> context) {
        if (logger.isDebugEnabled()) {
            logger.debug("渲染引擎：创建生产环境的渲染上下文，pageId=" + pageId + " context=" + context.toString());

        }
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            String message = "创建生产环境的渲染上下文出错，店铺页面为空，页面Id=" + pageId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        PageStructure pageStructure = pageStructureBuilder.buildProdModePageStructure(shopPage);
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopPage.getShopId());
        ProdRenderContext prodRenderContext = new ProdRenderContext(renderEngine, pageStructure, shopTemplate, shopPage);

        context.put(RenderConstants.RENDER_MOD, RenderConstants.RenderMod.product);

        prodRenderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        prodRenderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        prodRenderContext.setModuleContextExecutor(moduleContextExecutor);
        prodRenderContext.setOpenApiContext(prodOpenApiContext);
        prodRenderContext.putGlobalParameter(context);

        prodRenderContext.putResultParam(RenderConstants.GLOBAL_CSS_KEY, String.valueOf(shopTemplate.getId()));
        prodRenderContext.putResultParam(RenderConstants.GLOBAL_JS_KEY, String.valueOf(shopTemplate.getId()));
        prodRenderContext.putResultParam(RenderConstants.STYLE_KEY, String.valueOf(shopTemplate.getId()));

        // 产品页面的 css
        prodRenderContext.putResultParam(RenderConstants.CSS_ID_CONTEXT_KEY, RenderUtil.buildPageCss(pageStructure));

        prodRenderContext.setCacheService(cacheService);
        if (logger.isDebugEnabled()) {
            logger.debug("渲染引擎：创建生产环境的渲染上下文结束!");
        }
        return prodRenderContext;
    }


    @Override
    public RenderContext createDebugPagePrototypeRenderContext(int pagePrototypeId, Map<String, Object> context) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            String message = "创建debug页面原型上下文出错，页面原型为空,页面原型Id=" + pagePrototypeId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        context.put(RenderConstants.RENDER_MOD, RenderConstants.RenderMod.debug);

        DebugPagePrototypeRenderContext renderContext = new DebugPagePrototypeRenderContext(pagePrototype, renderEngine);
        renderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        renderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        renderContext.putGlobalParameter(context);
        renderContext.setOpenApiContext(prodOpenApiContext);
        renderContext.setCommonModuleService(commonModuleService);
        renderContext.setModuleContextExecutor(moduleContextExecutor);
        return renderContext;
    }

    @Override
    public RenderContext createHeadRenderContext(int shopId, Map<String, Object> context) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        if (shopTemplate == null) {
            String message = "创建渲染头部上下文出错，店铺模板为空，店铺Id=" + shopId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        PageStructure pageStructure = pageStructureBuilder.buildProdHeadFootPageStructure(shopTemplate);
        HeadFootRenderContext renderContext = new HeadFootRenderContext(renderEngine, RenderConstants.RenderArea.head, shopTemplate, pageStructure);
        renderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        renderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        renderContext.putGlobalParameter(context);
        renderContext.setOpenApiContext(prodOpenApiContext);
        renderContext.setModuleContextExecutor(moduleContextExecutor);
        renderContext.setCacheService(cacheService);
        return renderContext;
    }

    @Override
    public RenderContext createFootRenderContext(int shopId, Map<String, Object> context) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        if (shopTemplate == null) {
            String message = "创建渲染尾部上下文出错，店铺模板为空，店铺Id=" + shopId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        PageStructure pageStructure = pageStructureBuilder.buildProdHeadFootPageStructure(shopTemplate);
        HeadFootRenderContext renderContext = new HeadFootRenderContext(renderEngine, RenderConstants.RenderArea.foot, shopTemplate, pageStructure);
        renderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        renderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        renderContext.putGlobalParameter(context);
        renderContext.setOpenApiContext(prodOpenApiContext);
        renderContext.setModuleContextExecutor(moduleContextExecutor);
        renderContext.setCacheService(cacheService);
        return renderContext;
    }

    @Override
    public RenderContext createEditRenderCommonModuleContext(String moduleInstanceId, int prototypeId, Map<String, Object> context, RenderConstants.RenderArea renderArea) {
        CommonModule commonModule = commonModuleContainer.getCommonModuleById(prototypeId);
        if (commonModule == null) {
            String message = "公共模块不存在，模块id=" + prototypeId;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        EditRenderModuleContext renderContext = new EditRenderModuleContext(moduleInstanceId, commonModule, renderEngine, renderArea);
        renderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        renderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        renderContext.putGlobalParameter(context);
        renderContext.setOpenApiContext(prodOpenApiContext);
        renderContext.setModuleContextExecutor(moduleContextExecutor);
        renderContext.setModuleInstanceParamService(moduleInstanceParamService);
        renderContext.setUrlBrokerFactory(urlBrokerFactory);
        return renderContext;
    }

    @Override
    public RenderContext createGlobalCommonModuleRenderContext(String moduleName, Map<String, Object> context) {
        CommonModule commonModule = commonModuleContainer.getCommonModuleByName(moduleName);
        if (commonModule == null) {
            String message = "公共模块不存在，模块名称=" + moduleName;
            this.logger.error(message);
            return new NullRenderContext(message);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(1);
        GlobalCommonModuleRenderContext renderContext = new GlobalCommonModuleRenderContext(commonModule, renderEngine, shopTemplate);
        renderContext.put(RenderConstants.URL_Broker, urlBrokerFactory);
        renderContext.put(RenderConstants.IMG_RESOLVER, productPictureResolver);
        renderContext.putGlobalParameter(context);
        renderContext.setOpenApiContext(prodOpenApiContext);
        renderContext.setModuleContextExecutor(moduleContextExecutor);
        renderContext.setModuleInstanceParamService(moduleInstanceParamService);
        renderContext.setUrlBrokerFactory(urlBrokerFactory);
        return renderContext;
    }

    public void setProdOpenApiContext(Map<String, Object> prodOpenApiContext) {
        this.prodOpenApiContext = prodOpenApiContext;
    }
}
