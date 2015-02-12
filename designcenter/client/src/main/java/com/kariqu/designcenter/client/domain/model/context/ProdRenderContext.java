package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.common.ApplicationContextUtils;
import com.kariqu.common.cache.CacheService;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.RenderConstants.RenderArea;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.open.module.Product;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.domain.util.VersionUtil;
import com.kariqu.designcenter.service.CacheKeyFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生产环境渲染上下文，头尾要从缓存中取
 * body部分如果是首页和搜索结果页则通过店铺ID缓存
 * 商品详情页则通过商品ID缓存
 * <p/>
 * 模块如果为及时渲染，那么在缓存页面中将是一个占位符
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-10 下午05:26:29
 */
public class ProdRenderContext extends PreviewRenderContext {

    protected final Log logger = LogFactory.getLog(ProdRenderContext.class);


    private CacheService cacheService;


    /**
     * @param renderEngine
     * @param shopTemplate
     * @param shopPage
     * @param pageStructure
     */
    public ProdRenderContext(RenderEngine renderEngine, PageStructure pageStructure, ShopTemplate shopTemplate, ShopPage shopPage) {
        super(renderEngine, pageStructure, shopTemplate, shopPage);
    }

    @Override
    public String doRender() {
        if (pageStructure == null) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> allContext = getAllContext();
        //将渲染出来的头尾html代码缓存在一个Map中
        int shopId = shopPage.getShopId();
        String headAndFootKey = CacheKeyFactory.createShopHeadAndFootContentKey(shopId);
        Map<String, String> headFootMap = (Map<String, String>) cacheService.get(headAndFootKey);
        if (headFootMap == null || headFootMap.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("读取头尾缓存失效，将重新缓存");
            }
            headFootMap = new HashMap<String, String>();
            rendArea = RenderArea.head;
            String headContent = renderEngine.render(shopTemplate.getProdHeadContent(), allContext);
            rendArea = RenderArea.foot;
            String footContent = renderEngine.render(shopTemplate.getProdFootContent(), allContext);
            headFootMap.put(CacheKeyFactory.createShopHeadContentKey(shopId), headContent);
            headFootMap.put(CacheKeyFactory.createShopFootContentKey(shopId), footContent);
            cacheService.put(headAndFootKey, headFootMap);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("头尾缓存读取成功");
            }
        }
        rendArea = RenderArea.body;
        String pageContent;
        if (shopPage.getPageType().equals(PageType.detail)) {
            pageContent = renderDetailPage(allContext);
        } else {
            pageContent = renderShopPage(allContext);
        }

        StringBuilder allContent = new StringBuilder();
        allContent.append(renderInTimeModule(headFootMap.get(CacheKeyFactory.createShopHeadContentKey(shopId))));
        allContent.append(renderInTimeModule(pageContent));
        allContent.append(renderInTimeModule(headFootMap.get(CacheKeyFactory.createShopFootContentKey(shopId))));
        return allContent.toString();

    }

    /**
     * 渲染店铺首页和列表页面
     *
     * @param allContext
     * @return
     */
    private String renderShopPage(Map<String, Object> allContext) {
        String pageContent;
        String pageCacheKey = CacheKeyFactory.createShopPageContentKey(shopPage.getId());
        pageContent = (String) cacheService.get(pageCacheKey);
        if (pageContent == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("页面html缓存读取失败，将重新缓存");
            }
            pageContent = renderEngine.render(shopPage.getProdPageContent(), allContext);
            cacheService.put(pageCacheKey, pageContent);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("页面html缓存读取成功");
            }
        }
        return pageContent;
    }

    /**
     * 渲染商品详情页面，商品详情页面根据商品ID来缓存
     *
     * @param allContext
     * @return
     */
    private String renderDetailPage(Map<String, Object> allContext) {
        Object product = allContext.get(RenderConstants.PRODUCT_CONTEXT_KEY);
        //设置个default
        long productId = 1;
        if (product != null) {
            productId = ((Product) product).getId();
        } else {
            logger.warn("在详情页渲染时没有发现商品");
        }
        final String cacheVersion = shopPage.getConfigValue(RenderConstants.CACHE_VERSION);
        String cacheKey = productId + cacheVersion;
        String pageContent = (String) cacheService.get(cacheKey);
        if (null == pageContent) {
            if (logger.isDebugEnabled()) {
                logger.debug("详情页面html缓存读取失败，将重新缓存");
            }
            pageContent = renderEngine.render(shopPage.getProdPageContent(), allContext);
            cacheService.put(cacheKey, pageContent);
            cacheService.remove(productId + VersionUtil.degradeVersion(cacheVersion));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("详情页面html缓存读取成功");
            }
        }
        return pageContent;
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        try {
            final Map<String, Object> allContext = this.getAllContext();
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
                    return "renderCommonModule-渲染区域未指定";
            }
        } catch (Exception e) {
            logger.error("渲染公共模块出错¬", e);
        }
        return StringUtils.EMPTY;
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
                    return "renderTemplateModule-渲染区域未指定";
            }
        } catch (Exception e) {
            logger.error("渲染模板模块出错", e);
        }
        return StringUtils.EMPTY;
    }

    protected String renderModule(Map<String, Object> context, Module module) {
        try {
            if (!module.getModulePrototype().isCacheable()) {
                return module.getInTimeRenderPlaceHolder();
            }
            if (module.isTemplateModule()) {
                return renderEngine.render(module, context);
            } else {
                Map<String, Object> moduleContext = moduleContextExecutor.execute(context, module);
                if (moduleContext == null) {
                    moduleContext = new HashMap<String, Object>();
                    logger.warn("模块:" + module.getName() + "的脚本执行返回空map");
                }
                moduleContext.putAll(context);
                //对用编辑器编辑的html进行图片懒加载处理
                lazyImgTag(module);
                return renderEngine.render(module, moduleContext);
            }
        } catch (Exception e) {
            logger.error("渲染模块出错", e);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 将模块内容的图片标签进行懒加载处理
     *
     * @param module
     */
    private void lazyImgTag(Module module) {
        //只对特殊的一个自定义内容区做处理
        if (module.getModulePrototype().getName().equals(CommonModuleConstants.Custom_Html_Js_Module) ||
                module.getModulePrototype().getName().equals(CommonModuleConstants.Custom_Global_Html_Js_Module)) {
            List<ModuleInstanceParam> params = module.getParams();
            for (ModuleInstanceParam param : params) {
                if (param.getParamName().equals(CommonModuleConstants.Module_Html_ParamName)) {
                    param.setParamValue(param.getParamValue().replaceAll("<img(.+?)src=\"(.+?(?:\\.jpg|\\.gif))\"",
                            "<img data-original=\"$2\" src=\"" +
                                    ApplicationContextUtils.getBean(URLBrokerFactory.class).getUrl("StaticFileDomain").toString() + "/images/white.gif\"$1 "));
                }
            }
        }
    }


    /**
     * 渲染及时渲染的模块，需要把占位符替换成真实的模块内容
     *
     * @param content
     * @return
     */
    private String renderInTimeModule(String content) {
        List<String> moduleIds = RenderUtil.analyzeInTimeModuleIds(content);
        if (logger.isDebugEnabled()) {
            logger.debug("渲染及时模块" + moduleIds.toString());
        }
        for (String moduleId : moduleIds) {
            Module module = pageStructure.getModuleById(moduleId);
            String moduleContent;
            try {
                if (module.isTemplateModule()) {
                    moduleContent = renderEngine.render(module, getAllContext());
                } else {
                    Map<String, Object> moduleContext = moduleContextExecutor.execute(getAllContext(), module);
                    if (moduleContext == null) {
                        moduleContext = new HashMap<String, Object>();
                        logger.warn("模块:" + module.getName() + "的脚本执行返回空map");
                    }
                    moduleContext.putAll(getAllContext());
                    moduleContent = renderEngine.render(module, moduleContext);
                }
            } catch (Exception e) {
                logger.error("渲染模块出错", e);
                moduleContent = StringUtils.EMPTY;
            }
            content = content.replace(module.getInTimeRenderPlaceHolder(), moduleContent);
        }
        return content;
    }

    /**
     * @param cacheService the cacheService to set
     */
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }


}
