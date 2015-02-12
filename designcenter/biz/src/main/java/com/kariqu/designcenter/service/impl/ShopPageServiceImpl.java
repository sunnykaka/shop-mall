package com.kariqu.designcenter.service.impl;

import com.kariqu.common.cache.CacheService;
import com.kariqu.designcenter.domain.exception.DesignCenterRuntimeException;
import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.prototype.*;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopPageType;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;
import com.kariqu.designcenter.domain.util.RenderUtil;
import com.kariqu.designcenter.domain.util.VersionUtil;
import com.kariqu.designcenter.repository.ShopPageRepository;
import com.kariqu.designcenter.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Tiger
 * @version 1.0
 * @since 2010-12-22 下午08:03:42
 */
@Transactional
public class ShopPageServiceImpl implements ShopPageService {

    private static final Log logger = LogFactory.getLog(ShopPageService.class);

    @Autowired
    private ShopPageRepository shopPageRepository;

    @Autowired
    private ShopTemplateService shopTemplateService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private PageStructureBuilder pageStructureBuilder;

    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PagePrototypeService pagePrototypeService;

    /**
     * 用一个模板版本初始化真实的店铺
     * 如果用于初始化的版本没有发布则抛出异常，终止程序
     * 首先构建店铺模板
     * 1，店铺模板有编辑模式和产品模式，用这两种模式来对装修界面和用户所看到的界面进行隔离
     * 2，将模板版本的头尾结构文件用真实的实例ID替换
     * 3，将头尾内容和头尾结构文件set到店铺模板的产品和编辑模式字段
     * 4，用模板页面生成店铺页面，初始化编辑模式字段
     * 5，设置店铺页面的产品内容，设置产品化配置
     * 6，用真实实例ID替换店铺页面编辑模式配置和产品模式配置的domId
     * 7，用真实的头尾配置，店铺ID，页面ID，真实的页面配置初始化数据库中的实例参数表用于用户装修
     * 8，清除掉店铺页面编辑模式配置文件中的参数，因为没有作用，可以节省磁盘空间
     *
     * @param shopId
     * @param templateVersionId
     * @return
     */
    @Override
    @Transactional
    public void initShopPages(int shopId, int templateVersionId) {
        TemplateVersion templateVersion = checkVersionState(templateVersionId);
        ShopTemplate shopTemplate = ShopTemplate.generateShopTemplateFromTemplateVersion(shopId, templateVersion);
        shopTemplate.setEditGlobalCss(templateService.getTemplateResourceById(templateVersion.getGlobalCssId()).getContent());
        shopTemplate.setEditGlobalJs(templateService.getTemplateResourceById(templateVersion.getGlobalJsId()).getContent());
        shopTemplate.setEditStyle(templateService.getTemplateResourceById(templateService.getTemplateStyle(templateVersion.getDefaultStyleId()).getStyleResourceId()).getContent());
        shopTemplate.headReallyConfig();
        shopTemplate.footReallyConfig();
        shopTemplateService.createShopTemplate(shopTemplate);

        /* 根据模板中的模板页面构建店铺页面 */
        List<TemplatePage> templatePages = this.templateService.queryTemplatePageByTemplateVersionId(templateVersionId);

        List<ShopPage> shopPages = new ArrayList<ShopPage>(templatePages.size());
        for (TemplatePage templatePage : templatePages) {
            ShopPage shopPage = ShopPage.generateShopPageFromTemplatePage(shopId, shopTemplate.getId(), templatePage);
            createShopPage(shopPage); //保存
            shopPage.pageReallyConfig();//真实化实例ID
            shopPages.add(shopPage);
        }

        instanceShopTemplateParams(shopTemplate);
        for (ShopPage shopPage : shopPages) {
            instanceShopPageParams(shopPage);
        }

        shopTemplate.doProd();
        shopTemplateService.updateShopTemplate(shopTemplate);
        for (ShopPage shopPage : shopPages) {
            prodShopPage(shopTemplate, shopPage);
        }
        updateShopPages(shopPages);
    }


    @Override
    @Transactional
    public Result initShopHeadWithHeadPagePrototype(int pagePrototypeId, int shopId) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            return new Result(false, "指定的页面原型不存在，原型id=" + pagePrototypeId, null);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);//new ShopTemplate();
        if (shopTemplate == null) {
            shopTemplate = new ShopTemplate();
            shopTemplate.setShopId(shopId);
            shopTemplate.setEditHeadContent(pagePrototype.getPageCode());
            shopTemplate.setEditHeadConfigContent(pagePrototype.getConfigContent());
            shopTemplate.headReallyConfig();
            shopTemplateService.createShopTemplate(shopTemplate);
            moduleInstanceParamService.instanceShopHeadParams(shopTemplate);
        } else if (StringUtils.isEmpty(shopTemplate.getEditHeadContent())) {
            updateShopTemplateHeadWithPagePrototype(pagePrototype, shopTemplate);
        } else {
            return new Result(false, "店铺头已经存在，请选择升级", null);
        }
        return new Result(true, null, null);
    }

    private void updateShopTemplateHeadWithPagePrototype(PagePrototype pagePrototype, ShopTemplate shopTemplate) {
        shopTemplate.setEditHeadContent(pagePrototype.getPageCode());
        shopTemplate.setEditHeadConfigContent(pagePrototype.getConfigContent());
        shopTemplate.headReallyConfig();
        shopTemplateService.updateShopTemplate(shopTemplate);
        moduleInstanceParamService.instanceShopHeadParams(shopTemplate);
    }

    private void updateShopTemplateFootWithPagePrototype(PagePrototype pagePrototype, ShopTemplate shopTemplate) {
        shopTemplate.setEditFootContent(pagePrototype.getPageCode());
        shopTemplate.setEditFootConfigContent(pagePrototype.getConfigContent());
        shopTemplate.footReallyConfig();
        shopTemplateService.updateShopTemplate(shopTemplate);
        moduleInstanceParamService.instanceShopFootParams(shopTemplate);
    }

    @Override
    @Transactional
    public Result initShopFootWithFootPagePrototype(int pagePrototypeId, int shopId) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            return new Result(false, "指定的页面原型不存在，原型id=" + pagePrototypeId, null);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);//new ShopTemplate();
        if (shopTemplate == null) {
            shopTemplate = new ShopTemplate();
            shopTemplate.setShopId(shopId);
            shopTemplate.setEditFootContent(pagePrototype.getPageCode());
            shopTemplate.setEditFootConfigContent(pagePrototype.getConfigContent());
            shopTemplate.footReallyConfig();
            shopTemplateService.createShopTemplate(shopTemplate);
            moduleInstanceParamService.instanceShopFootParams(shopTemplate);
        } else if (StringUtils.isEmpty(shopTemplate.getEditFootContent())) {
            updateShopTemplateFootWithPagePrototype(pagePrototype, shopTemplate);
        } else {
            return new Result(false, "店铺尾已经存在，请选择升级", null);
        }
        return new Result(true, null, null);
    }

    @Override
    @Transactional
    public Result initShopPageWithPagePrototype(int pagePrototypeId, int shopId, PageType pageType, String name) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            return new Result(false, "指定的页面原型不存在，原型id=" + pagePrototypeId, null);
        } else if (pagePrototype.getAreaType() == AreaType.HEAD || pagePrototype.getAreaType() == AreaType.FOOT) {
            return new Result(false, "不能用头部和尾部原型来初始化页面，原型id=" + pagePrototypeId, null);
        }
        ShopPage shopPage = new ShopPage();
        shopPage.setShopId(shopId);
        shopPage.setName(name);
        shopPage.setShopPageType(ShopPageType.PROTOTYPE_PAGE);
        shopPage.setEditConfigContent(pagePrototype.getConfigContent());
        shopPage.setEditPageContent(pagePrototype.getPageCode());
        shopPage.setPageType(pageType);
        /* 如果是商品详情页，则设置缓存版本 */
        if (pageType.equals(PageType.detail)) {
            shopPage.setConfigValue(RenderConstants.CACHE_VERSION, RenderConstants.INIT_CACHE_VERSION);
        }
        createShopPage(shopPage);
        shopPage.pageReallyConfig();//真实化实例ID
        instanceShopPageParams(shopPage);
        updateShopPage(shopPage);
        Result result=new Result(true, null, null);
        result.addDataEntry("id",String.valueOf(shopPage.getId()));
        return result;
    }

    @Override
    @Transactional
    public Result applyPagePrototypeToHead(int pagePrototypeId, int shopId) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            return new Result(false, "指定的页面原型不存在，页面原型ID=" + pagePrototypeId, null);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        if (shopTemplate == null) {
            return new Result(false, "店铺模板不存在，请先初始化页面，店铺ID=" + shopId, null);
        }
        moduleInstanceParamService.deleteShopHeadParamByShopId(shopId);
        updateShopTemplateHeadWithPagePrototype(pagePrototype, shopTemplate);
        return new Result(true, null, null);
    }

    @Override
    @Transactional
    public Result applyPagePrototypeToFoot(int pagePrototypeId, int shopId) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            return new Result(false, "指定对象原型不存在，页面原型Id=" + pagePrototypeId, null);
        }
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        if (shopTemplate == null) {
            return new Result(false, "店铺模板不存在，请先初始化页面，店铺ID=" + shopId, null);
        }
        moduleInstanceParamService.deleteShopFootParamByShopId(shopId);
        updateShopTemplateFootWithPagePrototype(pagePrototype, shopTemplate);
        return new Result(true, null, null);
    }

    @Override
    @Transactional
    public Result applyPagePrototypeToShopPage(int pagePrototypeId, long shopPageId) {
        PagePrototype pagePrototype = pagePrototypeService.getPagePrototypeById(pagePrototypeId);
        if (pagePrototype == null) {
            return new Result(false, "指定页面原型不存在，页面原型Id=" + pagePrototypeId, null);
        }
        ShopPage shopPage = shopPageRepository.getShopPageById(shopPageId);
        if (shopPage == null) {
            return new Result(false, "指定的店铺页面不存在，页面id=" + shopPageId, null);
        }
        shopPage.setEditConfigContent(pagePrototype.getConfigContent());
        shopPage.setEditPageContent(pagePrototype.getPageCode());
        shopPage.pageReallyConfig();
        //moduleInstanceParamService.deletePageParamsByPageId(shopPage.getId());
        instanceShopPageParams(shopPage);
        updateShopPage(shopPage);
        return new Result(true, null, null);
    }


    @Override
    @Transactional
    public void publishShopPages(int shopId) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        shopTemplate.doProd();
        PageStructure pageStructure = pageStructureBuilder.buildEditHeadFootPageStructure(shopTemplate);
        Map<String, Region> headRegions = pageStructure.getHeadRegions();

        pageStructure.setHeadRegions(Collections.<String, Region>emptyMap());
        shopTemplate.setProdFootConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));

        pageStructure.setFootRegions(Collections.<String, Region>emptyMap());
        pageStructure.setHeadRegions(headRegions);
        shopTemplate.setProdHeadConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure));
        shopTemplateService.updateShopTemplate(shopTemplate);

        /*清除头尾页面缓存*/
        clearHeadAndFootContentCache(shopTemplate.getShopId());

        List<ShopPage> shopPages = new LinkedList<ShopPage>();
        ShopPage indexShopPage = shopPageRepository.queryIndexShopPage(shopId);
        if (indexShopPage != null) {
            shopPages.add(indexShopPage);
        }
        ShopPage detailShopPage = shopPageRepository.queryDetailShopPage(shopId);
        if (detailShopPage != null) {
            shopPages.add(detailShopPage);
        }
        ShopPage searchListShopPage = shopPageRepository.querySearchListShopPage(shopId);
        if (searchListShopPage != null) {
            shopPages.add(searchListShopPage);
        }
        ShopPage channelShopPage = shopPageRepository.queryChannelShopPage(shopId);
        if (channelShopPage != null) {
            shopPages.add(channelShopPage);
        }

        for (ShopPage shopPage : shopPages) {
            prodShopPage(shopTemplate, shopPage);
            clearPageCache(shopPage);
        }

        updateShopPages(shopPages);
        /**
         * 发布页面把全局模块的参数构建到shopTemplate中
         */
        shopTemplateService.releaseAllGlobalCommonModule(shopTemplate.getId());
    }

    @Override
    @Transactional
    public void publishShopPage(long pageId) {
        ShopPage shopPage = shopPageRepository.getShopPageById(pageId);
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopPage.getShopId());
        prodShopPage(shopTemplate, shopPage);
        clearPageCache(shopPage);
        updateShopPage(shopPage);
        /**
         * 发布页面把全局模块的参数构建到shopTemplate中
         */
        shopTemplateService.releaseAllGlobalCommonModule(shopTemplate.getId());
    }

    @Override
    @Transactional
    public void upgradeShopTemplate(int shopId, boolean modifyConfig) {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        TemplateVersion templateVersion = templateService.getTemplateVersionById(shopTemplate.getTemplateVersionId());
        //升级最新的头尾内容
        shopTemplate.setEditHeadContent(templateVersion.getHeadContent());
        shopTemplate.setEditFootContent(templateVersion.getFootContent());
        //升级最新的css,js,style
        TemplateResource newCss = templateService.getTemplateResourceById(templateVersion.getGlobalCssId());
        TemplateResource newJs = templateService.getTemplateResourceById(templateVersion.getGlobalJsId());
        TemplateStyle newStyle = templateService.getTemplateStyle(templateVersion.getDefaultStyleId());

        shopTemplate.setEditGlobalCss(newCss.getContent());
        shopTemplate.setEditGlobalJs(newJs.getContent());
        shopTemplate.setEditStyle(templateService.getTemplateResourceById(newStyle.getStyleResourceId()).getContent());

        if (modifyConfig) {
            shopTemplate.setEditHeadConfigContent(templateVersion.getHeadConfigContent());
            shopTemplate.setEditFootConfigContent(templateVersion.getFootConfigContent());
            shopTemplate.headReallyConfig();
            shopTemplate.footReallyConfig();
            moduleInstanceParamService.deleteShopHeadAndFootParamByShopId(shopId);
            moduleInstanceParamService.instanceShopHeadParams(shopTemplate);
            moduleInstanceParamService.instanceShopFootParams(shopTemplate);
        }
        shopTemplateService.updateShopTemplate(shopTemplate);
    }

    @Override
    @Transactional
    public void upgradeShopPage(long pageId, boolean modifyConfig) {
        ShopPage shopPage = this.getShopPageById(pageId);
        PageStructure pageStructure = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage.getEditConfigContent());
        TemplatePage templatePage = templateService.getTemplatePageById(pageStructure.getPrototypeId());
        shopPage.setEditPageContent(templatePage.getPageContent());
        if (modifyConfig) {
            shopPage.setEditConfigContent(templatePage.getConfigContent());
            shopPage.pageReallyConfig();
            moduleInstanceParamService.deletePageParamsByPageId(pageId);
            moduleInstanceParamService.instanceShopPageParam(shopPage);
        }
        this.updateShopPage(shopPage);
    }

    /**
     * @param shopId
     * @param templateVersionId
     */
    @Override
    @Transactional
    public void applyTemplateVersion(int shopId, int templateVersionId) {
        TemplateVersion templateVersion = checkVersionState(templateVersionId);
        /* 更新店铺模板对象*/
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        shopTemplate.updateFromTemplateVersion(templateVersion);
        shopTemplate.headReallyConfig();
        shopTemplate.footReallyConfig();
        shopTemplateService.updateShopTemplate(shopTemplate);

        /* 根据模板中的模板页面更新店铺中的模板页面，删除店铺原来模板页中的所有other页面*/
        List<TemplatePage> templatePages = this.templateService.queryTemplatePageByTemplateVersionId(templateVersionId);
        List<ShopPage> shopPages = shopPageRepository.queryShopPageWithShopPageType(shopId, ShopPageType.TEMPLATE_PAGE);
        Map<PageType, List<ShopPage>> pagesMap = convertPageListToMap(shopPages);
        List<ShopPage> otherTypeShopPages = pagesMap.get(PageType.other);
        /*
         * 删除所有other类型的页面
         */
        if (otherTypeShopPages != null && !otherTypeShopPages.isEmpty()) {
            this.deleteShopPages(otherTypeShopPages);
        }

        List<ShopPage> newVersionShopPages = new LinkedList<ShopPage>();
        for (TemplatePage templatePage : templatePages) {
            if (templatePage.getPageType().equals(PageType.other)) {
                ShopPage shopPage = ShopPage.generateShopPageFromTemplatePage(shopId, shopTemplate.getId(), templatePage);
                createShopPage(shopPage); //重新为店铺创建other页面
                shopPage.pageReallyConfig();
                newVersionShopPages.add(shopPage);
            } else {
                List<ShopPage> pages = pagesMap.get(templatePage.getPageType());
                ShopPage shopPage = pages.get(0);
                shopPage.updateFromTemplatePage(templatePage);
                shopPage.pageReallyConfig();
                removeShopPageCache(shopPage);
                newVersionShopPages.add(shopPage);
            }
        }

        instanceShopTemplateParams(shopTemplate);
        for (ShopPage newVersionShopPage : newVersionShopPages) {
            instanceShopPageParams(newVersionShopPage);
        }

        updateShopPages(newVersionShopPages);
    }

    @Override
    @Transactional
    public void upgradeTemplateVersion(int shopId, int templateVersionId) {
        TemplateVersion templateVersion = checkVersionState(templateVersionId);
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(shopId);
        shopTemplate.setTemplateVersionId(templateVersionId);
        shopTemplate.setEditHeadContent(templateVersion.getHeadContent());//头内容升级
        shopTemplate.setEditFootContent(templateVersion.getFootContent());//尾内容升级

        shopTemplateService.updateShopTemplate(shopTemplate);

        List<TemplatePage> templatePages = this.templateService.queryTemplatePageByTemplateVersionId(templateVersionId);
        List<ShopPage> shopPages = shopPageRepository.queryShopPagesByShopId(shopId);

        Map<String, TemplatePage> pageMap = convertTemplatePageListToMap(templatePages);
        for (ShopPage shopPage : shopPages) {
            TemplatePage templatePage = pageMap.get(shopPage.getName());
            shopPage.setEditPageContent(templatePage.getPageContent());//升级页面内容
            removeShopPageCache(shopPage);
        }

        updateShopPages(shopPages);

    }

    /**
     * 产品化店铺页面，产生产品化内容和产品化配置以及包含模块的js合并
     *
     * @param shopTemplate
     * @param shopPage
     */
    private void prodShopPage(ShopTemplate shopTemplate, ShopPage shopPage) {
        //从预览模式构建，参数是从数据库中读取的
        PageStructure pageStructure = pageStructureBuilder.buildPreviewModePageStructure(shopPage, shopTemplate);
        shopPage.setJsContent(RenderUtil.buildPageJavaScript(pageStructure));
        shopPage.setCssContent(RenderUtil.buildPageCss(pageStructure));
        String prodConfigContent = PageStructureAndXmlConverter.convertPageStructureToXml(pageStructure);
        shopPage.setProdConfigContent(prodConfigContent);
        shopPage.setProdPageContent(shopPage.getEditPageContent());
    }

    /*
    * 实例化模板头尾参数，首先需要删除原来的参数
    */
    private void instanceShopTemplateParams(ShopTemplate shopTemplate) {
        moduleInstanceParamService.deleteShopHeadAndFootParamByShopId(shopTemplate.getShopId());
        moduleInstanceParamService.instanceShopHeadParams(shopTemplate);
        moduleInstanceParamService.instanceShopFootParams(shopTemplate);
    }

    /* 实例化页面body部分的参数 */
    private void instanceShopPageParams(ShopPage shopPage) {
        moduleInstanceParamService.deletePageParamsByPageId(shopPage.getId()); //先删除原页面的参数
        moduleInstanceParamService.instanceShopPageParam(shopPage);
    }

    /**
     * 将模板页面列表转为以name为key的map，因为在一个版本中的模板页面名字是唯一的
     *
     * @param templatePages
     * @return
     */
    private Map<String, TemplatePage> convertTemplatePageListToMap(List<TemplatePage> templatePages) {
        Map<String, TemplatePage> map = new HashMap<String, TemplatePage>();
        for (TemplatePage templatePage : templatePages) {
            map.put(templatePage.getPageName(), templatePage);
        }
        return map;
    }

    /**
     * 清除模板头尾的html内容缓存
     *
     * @param shopId
     */
    private void clearHeadAndFootContentCache(int shopId) {
        cacheService.remove(CacheKeyFactory.createShopHeadAndFootContentKey(shopId));
    }

    /**
     * 清除与页面有关的缓存
     *
     * @param shopPage
     */
    private void clearPageCache(ShopPage shopPage) {
        /**
         * 设置缓存版本
         */
        if (shopPage.getPageType().equals(PageType.detail)) {
            shopPage.setConfigValue(RenderConstants.CACHE_VERSION, VersionUtil.upgradeVersion(shopPage.getConfigValue(RenderConstants.CACHE_VERSION)));
        }

        removeShopPageCache(shopPage);

        cacheService.remove(CacheKeyFactory.createShopPageContentKey(shopPage.getId())); //清除页面内容缓存
        cacheService.remove(CacheKeyFactory.createWholePageStructureKey(shopPage.getId())); //清除页面结构配置对象缓存
    }

    /**
     * 清除页面对象缓存
     *
     * @param shopPage
     */
    private void removeShopPageCache(ShopPage shopPage) {

        cacheService.remove(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()));

        if (shopPage.getPageType().equals(PageType.searchList)) {
            cacheService.remove(CacheKeyFactory.createSearchListShopPageKey(shopPage.getShopId()));
        }
        if (shopPage.getPageType().equals(PageType.index)) {
            cacheService.remove(CacheKeyFactory.createIndexShopPageKey(shopPage.getShopId()));
        }
        if (shopPage.getPageType().equals(PageType.detail)) {
            cacheService.remove(CacheKeyFactory.createDetailShopPageKey(shopPage.getShopId()));
        }
        if (shopPage.getPageType().equals(PageType.meal_detail)) {
            cacheService.remove(CacheKeyFactory.createMealSetShopPageKey(shopPage.getShopId()));
        }
    }


    /**
     * 检查版本状态，如果没发布则抛出异常
     *
     * @param templateVersionId
     * @return
     */
    private TemplateVersion checkVersionState(int templateVersionId) {
        TemplateVersion templateVersion = templateService.getTemplateVersionById(templateVersionId);
        if (!templateVersion.getState().equals(VersionState.released)) {
            logger.error("模板版本未发布,不能使用");
            throw new DesignCenterRuntimeException("模板版本未发布,不能使用");
        }
        return templateVersion;
    }


    /**
     * @param shopPages
     * @return
     */
    private Map<PageType, List<ShopPage>> convertPageListToMap(List<ShopPage> shopPages) {
        Map<PageType, List<ShopPage>> map = new HashMap<PageType, List<ShopPage>>();
        for (ShopPage shopPage : shopPages) {
            if (map.containsKey(shopPage.getPageType())) {
                map.get(shopPage.getPageType()).add(shopPage);
            } else {
                List<ShopPage> pageList = new LinkedList<ShopPage>();
                pageList.add(shopPage);
                map.put(shopPage.getPageType(), pageList);
            }
        }
        return map;
    }


    /**
     * @param shopPages
     */
    private void deleteShopPages(List<ShopPage> shopPages) {
        for (ShopPage shopPage : shopPages) {
            this.deleteShopPage(shopPage.getId());
            moduleInstanceParamService.deletePageParamsByPageId(shopPage.getId());
        }
    }

    @Override
    public ShopPage getShopPageById(long id) {
        String shopPageKey = CacheKeyFactory.createShopPageCacheKey(id);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.getShopPageById(id);
            if (shopPage != null) {
                cacheService.put(shopPageKey, shopPage);
            }
        }
        return shopPage;
    }

    @Override
    public long createShopPage(ShopPage shopPage) {
        shopPageRepository.createShopPage(shopPage);
        return shopPage.getId();
    }

    @Override
    public void updateShopPage(ShopPage shopPage) {
        cacheService.remove(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()));
        shopPageRepository.updateShopPage(shopPage);
    }

    @Override
    public void deleteShopPage(long id) {
        cacheService.remove(CacheKeyFactory.createShopPageCacheKey(id));
        shopPageRepository.deleteShopPageById(id);
    }

    @Override
    public List<ShopPage> queryAllShopPages() {
        return shopPageRepository.queryAllShopPages();
    }

    @Override
    public List<ShopPage> queryShopPagesByShopId(int shopId) {
        return shopPageRepository.queryShopPagesByShopId(shopId);
    }

    @Override
    public ShopPage queryIndexShopPage(int shopId) {
        String shopPageKey = CacheKeyFactory.createIndexShopPageKey(shopId);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.queryIndexShopPage(shopId);
            if (null != shopPage) {
                if (logger.isDebugEnabled()) {
                    logger.debug("读取页面数据对象缓存失败，将重新缓存,页面是" + shopPage.getName());
                }
                cacheService.put(shopPageKey, shopPage);
                cacheService.put(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()), shopPage);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("读取页面数据对象缓存成功");
            }
        }
        return shopPage;
    }
    @Override
    public ShopPage querySearchListShopPage(int shopId) {
        String shopPageKey = CacheKeyFactory.createSearchListShopPageKey(shopId);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.querySearchListShopPage(shopId);
            if (null != shopPage) {
                if (logger.isDebugEnabled()) {
                    logger.debug("读取页面数据对象缓存失败，将重新缓存,页面是" + shopPage.getName());
                }
                cacheService.put(shopPageKey, shopPage);
                cacheService.put(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()), shopPage);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("读取页面数据对象缓存成功");
            }
        }
        return shopPage;
    }

    @Override
    public ShopPage queryChannelShopPage(int shopId) {
        String shopPageKey = CacheKeyFactory.createChannelShopPageKey(shopId);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.queryChannelShopPage(shopId);
            if (null != shopPage) {
                if (logger.isDebugEnabled()) {
                    logger.debug("读取页面数据对象缓存失败，将重新缓存,页面是" + shopPage.getName());
                }
                cacheService.put(shopPageKey, shopPage);
                cacheService.put(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()), shopPage);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("读取页面数据对象缓存成功");
            }
        }
        return shopPage;
    }

    @Override
    public ShopPage queryMealSetShopPage(int shopId) {
        String shopPageKey = CacheKeyFactory.createMealSetShopPageKey(shopId);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.queryMealSetShopPage(shopId);
            if (null != shopPage) {
                if (logger.isDebugEnabled()) {
                    logger.debug("读取页面数据对象缓存失败，将重新缓存,页面是" + shopPage.getName());
                }
                cacheService.put(shopPageKey, shopPage);
                cacheService.put(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()), shopPage);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("读取页面数据对象缓存成功");
            }
        }
        return shopPage;
    }

    @Override
    public ShopPage queryDetailShopPage(int shopId) {
        String shopPageKey = CacheKeyFactory.createDetailShopPageKey(shopId);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.queryDetailShopPage(shopId);
            if (null != shopPage) {
                if (logger.isDebugEnabled()) {
                    logger.debug("读取页面数据对象缓存失败，将重新缓存,页面是" + shopPage.getName());
                }
                cacheService.put(shopPageKey, shopPage);
                cacheService.put(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()), shopPage);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("读取页面数据对象缓存成功");
            }
        }
        return shopPage;
    }

    @Override
    public ShopPage queryDetailIntegralShopPage(int shopId) {
        String shopPageKey = CacheKeyFactory.createDetailIntegralShopPageKey(shopId);
        ShopPage shopPage = (ShopPage) cacheService.get(shopPageKey);
        if (null == shopPage) {
            shopPage = shopPageRepository.queryDetailIntegralShopPage(shopId);
            if (null != shopPage) {
                if (logger.isDebugEnabled()) {
                    logger.debug("读取页面数据对象缓存失败，将重新缓存,页面是" + shopPage.getName());
                }
                cacheService.put(shopPageKey, shopPage);
                cacheService.put(CacheKeyFactory.createShopPageCacheKey(shopPage.getId()), shopPage);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("读取页面数据对象缓存成功");
            }
        }
        return shopPage;
    }


    /**
     * 删除页面编辑模式下配置文件中的参数信息，编辑模式下参数信息全部保存到数据库表中了
     * 所以这里面的参数没有作用了，为了节省磁盘空间把它清除掉
     *
     * @param shopPages
     * @return
     */
    private List<ShopPage> clearUselessParams(List<ShopPage> shopPages) {
        for (ShopPage shopPage : shopPages) {
            PageStructure ps = PageStructureAndXmlConverter.convertXmlToPageStructure(shopPage.getEditConfigContent());
            List<Module> allModules = ps.getAllModules();
            for (Module module : allModules) {
                module.setParams(Collections.<ModuleInstanceParam>emptyList());
            }
            shopPage.setEditConfigContent(PageStructureAndXmlConverter.convertPageStructureToXml(ps));
        }
        return shopPages;
    }

    private void updateShopPages(List<ShopPage> waitToUpdateShopPages) {
        for (ShopPage shopPage : waitToUpdateShopPages) {
            updateShopPage(shopPage);
        }
    }

}
