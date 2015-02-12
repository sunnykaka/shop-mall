package com.kariqu.designcenter.service;

import com.kariqu.common.cache.CacheService;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.domain.exception.RenderException;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.Region;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.ModulePrototype;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.domain.util.RenderUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * PageStructure构建器
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-21 下午02:35:22
 */
public class PageStructureBuilder {

    protected final Log logger = LogFactory.getLog(PageStructureBuilder.class);

    @Autowired
    private CacheService cacheService;

    /**
     * 模块实例化参数服务
     */
    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;


    @Autowired
    private TemplateService templateService;

    @Autowired
    private CommonModuleContainer commonModuleContainer;


    public PageStructure buildPreviewModePageStructure(ShopPage shopPage, ShopTemplate shopTemplate) {
        if (logger.isDebugEnabled()) {
            logger.debug("开始构建预览模式的页面结构对象,页面是" + shopPage.getName());
        }
        PageStructure pageStructure = buildPageStructure(shopPage, shopTemplate);
        pageStructure = injectModuleContent(pageStructure);
        return pageStructure;
    }

    public PageStructure buildEditModePageStructure(ShopPage shopPage, ShopTemplate shopTemplate) {
        if (logger.isDebugEnabled()) {
            logger.debug("开始构建编辑模式的页面结构对象,页面是" + shopPage.getName());
        }
        PageStructure pageStructure = buildPageStructure(shopPage, shopTemplate);
        pageStructure = injectModuleContent(pageStructure);
        initModuleToolbar(pageStructure, shopPage, shopTemplate);
        return pageStructure;
    }

    /**
     * 构建的PageStructure对象hold了完整的模块对象和参数，然后把它放入缓存，以后的页面渲染只和缓存中的这个对象有关
     * 和数据库已经没有关系
     *
     * @param shopPage
     * @return
     */
    public PageStructure buildProdModePageStructure(ShopPage shopPage) {
        if (logger.isDebugEnabled()) {
            logger.debug("开始构建产品模式的页面结构对象,页面是" + shopPage.getName());
        }
        String cacheKey = CacheKeyFactory.createWholePageStructureKey(shopPage.getId());
        PageStructure pageStructure = (PageStructure) cacheService.get(cacheKey);
        if (null == pageStructure) {
            if (logger.isDebugEnabled()) {
                logger.debug("产品模式页面结构缓存读取失败,将重新缓存");
            }
            String configContent = shopPage.getProdConfigContent();
            if (StringUtils.isBlank(configContent)) {
                return null;
            }
            pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(configContent);
            cacheService.put(cacheKey, pageStructure);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("产品模式页面结构缓存读取成功");
            }
        }
        return injectModuleContent(pageStructure);
    }

    public PageStructure buildProdHeadFootPageStructure(ShopTemplate shopTemplate) {
        if (logger.isDebugEnabled()) {
            logger.debug("开始构建产品模式店铺头尾的页面结构");
        }
        PageStructure headPageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getProdHeadConfigContent());
        PageStructure footPageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getProdFootConfigContent());
        headPageStructure.setFootRegions(footPageStructure.getFootRegions());
        injectModuleContent(headPageStructure);
        return headPageStructure;
    }

    public PageStructure buildEditHeadFootPageStructure(ShopTemplate shopTemplate) {
        if (logger.isDebugEnabled()) {
            logger.debug("开始构建编辑模式店铺头尾的页面结构");
        }
        PageStructure headPageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditHeadConfigContent());
        PageStructure footPageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate.getEditFootConfigContent());
        initParams(headPageStructure);
        initParams(footPageStructure);
        headPageStructure.setFootRegions(footPageStructure.getFootRegions());
        return headPageStructure;
    }

    /**
     * 注入模块的内容，通过xml构建出来的页面，页面中模块的内容为空
     *
     * @param pageStructure
     * @return
     */
    private PageStructure injectModuleContent(PageStructure pageStructure) {
        if (pageStructure == null) {
            return null;
        }
        Set<Integer> waitToInjectTemplateModuleIds = new HashSet<Integer>();
        Set<Integer> waitToInjectCommonModuleIds = new HashSet<Integer>();
        for (Module module : pageStructure.getAllModules()) {
            if (module.isTemplateModule()) {
                waitToInjectTemplateModuleIds.add(module.getModulePrototype().getId());
            } else {
                waitToInjectCommonModuleIds.add(module.getModulePrototype().getId());
            }
        }
        Map<Integer, TemplateModule> templateModuleMap = null;
        Map<Integer, CommonModule> commonModuleMap = convertCommonModule(commonModuleContainer
                .queryCommonModulesByIds(new LinkedList<Integer>(waitToInjectCommonModuleIds)));
        for (Module module : pageStructure.getAllModules()) {
            if (module.isTemplateModule()) {
                if (templateModuleMap == null) {
                    templateModuleMap = convertTemplateModule(templateService.queryTemplateModulesByIds(new LinkedList<Integer>(waitToInjectTemplateModuleIds)));
                }
                TemplateModule templateModule = templateModuleMap.get(module.getModulePrototype().getId());
                if (templateModule == null)
                    throw new RenderException("渲染的模板模块不存在，模块原型Id=" + module.getModulePrototype().getId());
                module.setModulePrototype(templateModule);
            } else {
                ModulePrototype commonModule = commonModuleMap.get(module.getModulePrototype().getId());
                if (commonModule == null)
                    throw new RenderException("渲染的公共模块不存在，模块原型Id=" + module.getModulePrototype().getId());
                module.setModulePrototype(commonModule);
            }
        }
        return pageStructure;
    }


    private PageStructure buildPageStructure(ShopPage shopPage, ShopTemplate shopTemplate) {
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage.getEditConfigContent());
        initParams(pageStructure);

        PageStructure headPageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate
                .getEditHeadConfigContent());
        PageStructure footPageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopTemplate
                .getEditFootConfigContent());

        initParams(headPageStructure);
        initParams(footPageStructure);

        pageStructure.setHeadRegions(headPageStructure.getHeadRegions());
        pageStructure.setFootRegions(footPageStructure.getFootRegions());
        return pageStructure;
    }

    /**
     * 编辑模式下需要初始化模块的toolbar
     *
     * @param pageStructure
     * @param shopPage
     * @param shopTemplate
     */
    private void initModuleToolbar(PageStructure pageStructure, ShopPage shopPage, ShopTemplate shopTemplate) {
        for (Region region : pageStructure.getHeadRegions().values()) {
            for (Module module : region.getModules()) {
                module.setToolbar(RenderUtil.initHeadToolbar(module, urlBrokerFactory));
            }
        }

        for (Region region : pageStructure.getFootRegions().values()) {
            for (Module module : region.getModules()) {
                module.setToolbar(RenderUtil.initFootToolbar(module, urlBrokerFactory));
            }
        }

        for (Region region : pageStructure.getBodyRegions().values()) {
            for (Module module : region.getModules()) {
                module.setToolbar(RenderUtil.initBodyToolbar(module, urlBrokerFactory));
            }
        }
    }


    /**
     * @param params
     * @return
     */
    private Map<String, List<ModuleInstanceParam>> convertListToMap(List<ModuleInstanceParam> params) {
        Map<String, List<ModuleInstanceParam>> paramMap = new HashMap<String, List<ModuleInstanceParam>>();
        for (ModuleInstanceParam param : params) {
            if (paramMap.containsKey(param.getModuleInstanceId())) {
                paramMap.get(param.getModuleInstanceId()).add(param);
            } else {
                List<ModuleInstanceParam> moduleParams = new LinkedList<ModuleInstanceParam>();
                moduleParams.add(param);
                paramMap.put(param.getModuleInstanceId(), moduleParams);
            }
        }
        return paramMap;
    }

    /**
     * @param pageStructure
     */
    private void initParams(PageStructure pageStructure) {
        for (Module module : pageStructure.getAllModules()) {
            List<ModuleInstanceParam> params = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(module.getModuleInstanceId());
            module.setParams(params);
        }
    }

    /**
     * @param commonModules
     * @return
     */
    private Map<Integer, CommonModule> convertCommonModule(List<CommonModule> commonModules) {
        Map<Integer, CommonModule> map = new HashMap<Integer, CommonModule>();
        for (CommonModule commonModule : commonModules) {
            map.put(commonModule.getId(), commonModule);
        }
        return map;
    }

    /**
     * @param templateModules
     * @return
     */
    private Map<Integer, TemplateModule> convertTemplateModule(List<TemplateModule> templateModules) {
        Map<Integer, TemplateModule> map = new HashMap<Integer, TemplateModule>();
        for (TemplateModule templateModule : templateModules) {
            map.put(templateModule.getId(), templateModule);
        }
        return map;
    }

}
