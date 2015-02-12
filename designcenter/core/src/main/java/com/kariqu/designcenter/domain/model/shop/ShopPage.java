package com.kariqu.designcenter.domain.model.shop;

import com.kariqu.designcenter.domain.model.*;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;

/**
 * 店铺页面领域对象
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-16 上午11:40:30
 */
public class ShopPage extends ModelConfig {

    private static final long serialVersionUID = 5999764952784697128L;

    private long id;

    private String name;

    private String title;

    private String keywords;

    private String description;

    /**
     * 店铺页面所属店铺的ID
     */
    private int shopId;

    /**
     * 编辑模式配置文件内容
     */
    private String editConfigContent;

    /**
     * 生产模式配置文件内容
     */
    private String prodConfigContent;

    /**
     * 店铺页面对应的模板页面
     */
    private String editPageContent;

    /**
     * 生产环境下的页面内容
     */
    private String prodPageContent;

    /**
     * 店铺页面的类型，表示是模板页面，系统页面，用户自定义页面
     */
    private ShopPageType shopPageType;

    /**
     * 页面类型，表示首页，搜索结果页，以及商品详情页面
     */
    private PageType pageType;

    /**
     * 店铺模板
     */
    private int shopTemplateId;


    /**
     * 页面状态
     */
    private PageStatus pageStatus = PageStatus.NORMAL;


    /**
     * 这个页面动态合并出来的js内容
     * 这些js是页面被发布的时候遍历页面中的模块生成的
     */
    private String jsContent;

    /** 页面动态合并出来的 css 内容 */
    private String cssContent;

    /**
     * 根据TemplatePage生成shopPage,此时不生成shopPage的发布后的配置文件，仅仅生成装修中的配置文件
     *
     * @return
     */
    public static ShopPage generateShopPageFromTemplatePage(int shopId, int shopTemplateId, TemplatePage templatePage) {
        ShopPage shopPage = new ShopPage();
        shopPage.setName(templatePage.getPageName());
        shopPage.setShopId(shopId);
        shopPage.setShopTemplateId(shopTemplateId);
        shopPage.setPageType(templatePage.getPageType());
        shopPage.setShopPageType(ShopPageType.TEMPLATE_PAGE);
        shopPage.setEditPageContent(templatePage.getPageContent());
        shopPage.setEditConfigContent(templatePage.getConfigContent());
        /* 如果是商品详情页，则设置缓存版本 */
        if (templatePage.getPageType().equals(PageType.detail)) {
            shopPage.setConfigValue(RenderConstants.CACHE_VERSION, RenderConstants.INIT_CACHE_VERSION);
        }
        return shopPage;
    }


    public void pageReallyConfig() {
        PageStructure ps = PageStructureAndXmlConverter.convertXmlToPageStructure(this.getEditConfigContent());
        ps.setPageId(this.getId());
        for (Module module : ps.getAllBodyModules()) {
            if (!module.isTemplateModule()) {
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                if (commonModule.isGlobalGranularity()) {
                    module.setModuleInstanceId(ModuleInstanceIdFactory.createModuleIdOfGlobalGranularity(shopId, commonModule.getId()));
                } else {
                    String domId = module.getModuleInstanceId();//模板发布的时候这里是DomId
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfBody(this.getId(), domId, getShopId());
                    module.setModuleInstanceId(moduleInstanceId);
                }
            }
        }
        this.editConfigContent = PageStructureAndXmlConverter.convertPageStructureToXml(ps);
    }

    /**
     * 根据模板页面信息更新店铺页面信息
     *
     * @param templatePage
     * @return
     */
    public void updateFromTemplatePage(TemplatePage templatePage) {
        this.setName(templatePage.getPageName());
        this.setEditPageContent(templatePage.getPageContent());
        this.setEditConfigContent(templatePage.getConfigContent());
    }


    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the shopId
     */
    public int getShopId() {
        return shopId;
    }

    /**
     * @param shopId the shopId to set
     */
    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    /**
     * @return the editConfigContent
     */
    public String getEditConfigContent() {
        return editConfigContent;
    }

    /**
     * @param editConfigContent the editConfigContent to set
     */
    public void setEditConfigContent(String editConfigContent) {
        this.editConfigContent = editConfigContent;
    }


    /**
     * @return the prodPageContent
     */
    public String getProdPageContent() {
        return prodPageContent;
    }

    /**
     * @param prodPageContent the prodPageContent to set
     */
    public void setProdPageContent(String prodPageContent) {
        this.prodPageContent = prodPageContent;
    }

    /**
     * @return the prodConfigContent
     */
    public String getProdConfigContent() {
        return prodConfigContent;
    }

    /**
     * @param prodConfigContent the prodConfigContent to set
     */
    public void setProdConfigContent(String prodConfigContent) {
        this.prodConfigContent = prodConfigContent;
    }

    /**
     * @return the shopPageType
     */
    public ShopPageType getShopPageType() {
        return shopPageType;
    }

    /**
     * @param shopPageType the shopPageType to set
     */
    public void setShopPageType(ShopPageType shopPageType) {
        this.shopPageType = shopPageType;
    }

    /**
     * @return the pageType
     */
    public PageType getPageType() {
        return pageType;
    }

    /**
     * @param pageType the pageType to set
     */
    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    /**
     * @return the editPageContent
     */
    public String getEditPageContent() {
        return editPageContent;
    }

    /**
     * @param editPageContent the editPageContent to set
     */
    public void setEditPageContent(String editPageContent) {
        this.editPageContent = editPageContent;
    }

    /**
     * @return the shopTemplateId
     */
    public int getShopTemplateId() {
        return shopTemplateId;
    }

    /**
     * @param shopTemplateId the shopTemplateId to set
     */
    public void setShopTemplateId(int shopTemplateId) {
        this.shopTemplateId = shopTemplateId;
    }

    public String getJsContent() {
        return jsContent;
    }

    public void setJsContent(String jsContent) {
        this.jsContent = jsContent;
    }

    public String getCssContent() {
        return cssContent;
    }

    public void setCssContent(String cssContent) {
        this.cssContent = cssContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PageStatus getPageStatus() {
        return pageStatus;
    }

    public void setPageStatus(PageStatus pageStatus) {
        this.pageStatus = pageStatus;
    }
}
