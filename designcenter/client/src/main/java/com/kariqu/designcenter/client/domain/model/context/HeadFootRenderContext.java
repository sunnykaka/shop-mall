package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.common.cache.CacheService;
import com.kariqu.designcenter.client.domain.model.AbstractRenderContext;
import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceIdFactory;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.service.CacheKeyFactory;
import com.kariqu.designcenter.service.ModuleContextExecutor;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 头尾渲染上下文
 *
 * @author Tiger
 * @version 1.0
 * @since 12-5-9 上午11:48
 */
public class HeadFootRenderContext extends AbstractRenderContext {

    private ShopTemplate shopTemplate;

    private PageStructure pageStructure;

    private ModuleContextExecutor moduleContextExecutor;

    private CacheService cacheService;

    private RenderConstants.RenderArea renderArea;


    public HeadFootRenderContext(RenderEngine renderEngine, RenderConstants.RenderArea renderArea, ShopTemplate shopTemplate, PageStructure pageStructure) {
        super(renderEngine);
        this.renderArea = renderArea;
        this.shopTemplate = shopTemplate;
        this.pageStructure = pageStructure;
    }

    @Override
    protected String doRender() {
        Map<String, Object> allContext = getAllContext();
        int shopId = shopTemplate.getShopId();
        String headAndFootKey = CacheKeyFactory.createShopHeadAndFootContentKey(shopId);
        Map<String, String> headFootMap = (Map<String, String>) cacheService.get(headAndFootKey);
        if (headFootMap == null || headFootMap.isEmpty()) {
            headFootMap = new HashMap<String, String>();
            renderArea = RenderConstants.RenderArea.head;
            String headContent = renderEngine.render(shopTemplate.getProdHeadContent(), allContext);
            renderArea = RenderConstants.RenderArea.foot;
            String footContent = renderEngine.render(shopTemplate.getProdFootContent(), allContext);
            headFootMap.put(CacheKeyFactory.createShopHeadContentKey(shopId), headContent);
            headFootMap.put(CacheKeyFactory.createShopFootContentKey(shopId), footContent);
            cacheService.put(headAndFootKey, headFootMap);
        }
        switch (renderArea) {
            case head:
                return renderInTimeModule(headFootMap.get(CacheKeyFactory.createShopHeadContentKey(shopId)));
            case foot:
                return renderInTimeModule(headFootMap.get(CacheKeyFactory.createShopFootContentKey(shopId)));
            default:
                throw new RenderException("不支持非头部尾部的渲染");
        }
    }

    @Override
    public String renderTemplateModule(String name, int domId) {
        throw new UnsupportedOperationException("头部尾部不支持模板模块");
    }

    @Override
    public String renderCommonModule(String name, String version, int domId) {
        try {
            final Map<String, Object> allContext = this.getAllContext();
            switch (renderArea) {
                case head: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfHead(shopTemplate.getShopId(), Integer.toString(domId));
                    Module module = pageStructure.getFixedHeadModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                case foot: {
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfBody(shopTemplate.getId(), Integer.toString(domId), shopTemplate.getShopId());
                    Module module = pageStructure.getFixedFootModule(moduleInstanceId);
                    return renderModule(allContext, module);
                }
                default:
                    throw new RenderException("不支持非头部尾部的渲染");
            }
        } catch (RenderException e) {
            logger.error("渲染系统模块出现异常", e);
        }
        return StringUtils.EMPTY;
    }

    protected String renderModule(Map<String, Object> context, Module module) {
        try {
            if (!module.getModulePrototype().isCacheable()) {
                return module.getInTimeRenderPlaceHolder();
            }
            if (module.isTemplateModule()) {
                throw new RenderException("头部尾部不支持模板模块");
            } else {
                Map<String, Object> moduleContext = moduleContextExecutor.execute(context, module);
                if (moduleContext == null) {
                    moduleContext = new HashMap<String, Object>();
                }
                moduleContext.putAll(context);
                return renderEngine.render(module, moduleContext);
            }
        } catch (RenderException e) {
            logger.error("渲染模块出错", e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        switch (renderArea) {
            case head: {
                List<Module> modules = pageStructure.getModulesFromRegionOfHead(regionName);
                return wrapperRegion(renderModules(modules));
            }
            case foot: {
                List<Module> modules = pageStructure.getModulesFromRegionOfFoot(regionName);
                return wrapperRegion(renderModules(modules));
            }
            default:
                throw new RenderException("不支持非头部尾部的渲染");
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

    /**
     * 渲染及时渲染的模块，需要把占位符替换成真实的模块内容
     *
     * @param content
     * @return
     */
    private String renderInTimeModule(String content) {
        List<String> moduleIds = RenderUtil.analyzeInTimeModuleIds(content);
        for (String moduleId : moduleIds) {
            Module module = pageStructure.getModuleById(moduleId);
            String moduleContent;
            if (module.isTemplateModule()) {
                throw new RenderException("头部尾部不支持模板模块");
            } else {
                try {
                    Map<String, Object> moduleContext = moduleContextExecutor.execute(getAllContext(), module);
                    if (moduleContext == null) {
                        moduleContext = new HashMap<String, Object>();
                        logger.warn("模块:" + module.getName() + "的脚本执行返回空map");
                    }
                    moduleContext.putAll(getAllContext());
                    moduleContent = renderEngine.render(module, moduleContext);
                } catch (Exception e) {
                    logger.error("渲染模块出错", e);
                    moduleContent = StringUtils.EMPTY;
                }
            }
            content = content.replace(module.getInTimeRenderPlaceHolder(), moduleContent);
        }
        return content;
    }


    public void setModuleContextExecutor(ModuleContextExecutor moduleContextExecutor) {
        this.moduleContextExecutor = moduleContextExecutor;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

}
