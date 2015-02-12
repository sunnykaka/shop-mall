package com.kariqu.designcenter.domain.model.shop;

import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.ModuleInstanceIdFactory;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;
import com.kariqu.designcenter.domain.util.PageStructureAndXmlConverter;

import java.io.Serializable;
import java.util.List;


/**
 * 店铺模板领域对象，当某个模板与店铺关联以后，会有对应的店铺模板对象
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-16 下午12:21:55
 */
public class ShopTemplate implements Serializable {

    private static final long serialVersionUID = -7528291375560238029L;

    private int id;

    /**
     * 店铺id
     */
    private int shopId;

    /**
     * 模板Id,目前没有使用
     */
    private int templateVersionId;


    /**
     * 编辑模式的头部配置文件
     */
    private String editHeadConfigContent;

    /**
     * 编辑模式的尾部配置文件
     */
    private String editFootConfigContent;

    /**
     * 编辑模式的头部内容
     */
    private String editHeadContent;

    /**
     * 编辑模式的尾部内容
     */
    private String editFootContent;

    /**
     * 编辑模式的风格
     */
    private String editStyle;

    /**
     * 编辑模式的全局css
     */
    private String editGlobalCss;

    /**
     * 编辑模式的全局js
     */
    private String editGlobalJs;


    /**
     * 发布模式的头部配置文件
     */
    private String prodHeadConfigContent;

    /**
     * 发布模式的尾部配置文件
     */
    private String prodFootConfigContent;

    /**
     * 发布模式的头部内容
     */
    private String prodHeadContent;

    /**
     * 发布模式的尾部内容
     */
    private String prodFootContent;

    /**
     * 发布模式的全局css
     */
    private String prodGlobalCss;

    /**
     * 发布模式的全局js
     */
    private String prodGlobalJs;

    /**
     * 发布模式的风格css
     */
    private String prodStyle;

    /**
     * 全局模块的配置文件
     */
    private String globalModuleInfoConfig;


    public static ShopTemplate generateShopTemplateFromTemplateVersion(int shopId, TemplateVersion templateVersion) {
        /* 首先创建ShopTemplate，关联店铺和版本*/
        ShopTemplate shopTemplate = new ShopTemplate();
        shopTemplate.setShopId(shopId);
        shopTemplate.setTemplateVersionId(templateVersion.getId());

        /* 编辑模式 */
        shopTemplate.setEditHeadConfigContent(templateVersion.getHeadConfigContent());
        shopTemplate.setEditFootConfigContent(templateVersion.getFootConfigContent());
        shopTemplate.setEditHeadContent(templateVersion.getHeadContent());
        shopTemplate.setEditFootContent(templateVersion.getFootContent());

        return shopTemplate;
    }


    /**
     * 使店铺模板产品化，这样买家即可看到最新界面
     */
    public void doProd() {
        this.prodFootConfigContent = editFootConfigContent;
        this.prodHeadConfigContent = editHeadConfigContent;
        this.prodFootContent = editFootContent;
        this.prodHeadContent = editHeadContent;
        this.prodStyle = editStyle;
        this.prodGlobalCss = editGlobalCss;
        this.prodGlobalJs = editGlobalJs;
    }

    /**
     * 实例化真实的头模块ID
     */
    public void headReallyConfig() {
        PageStructure headPs = PageStructureAndXmlConverter.convertXmlToPageStructure(this.getEditHeadConfigContent());
        List<Module> allHeadModules = headPs.getAllHeadModules();
        for (Module module : allHeadModules) {
            if (!module.isTemplateModule()) {
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                if (commonModule.isGlobalGranularity()) {
                    module.setModuleInstanceId(ModuleInstanceIdFactory.createModuleIdOfGlobalGranularity(shopId, commonModule.getId()));
                } else {
                    String domId = module.getModuleInstanceId();//模板发布的时候这里是DomId
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfHead(shopId, domId);
                    module.setModuleInstanceId(moduleInstanceId);
                }
            }

        }
        this.editHeadConfigContent = PageStructureAndXmlConverter.convertPageStructureToXml(headPs);
    }

    /**
     * 实例化真实的尾模块ID
     */
    public void footReallyConfig() {
        PageStructure footPs = PageStructureAndXmlConverter.convertXmlToPageStructure(this.getEditFootConfigContent());
        List<Module> allFootModules = footPs.getAllFootModules();
        for (Module module : allFootModules) {
            if (!module.isTemplateModule()) {
                CommonModule commonModule = (CommonModule) module.getModulePrototype();
                if (commonModule.isGlobalGranularity()) {
                    module.setModuleInstanceId(ModuleInstanceIdFactory.createModuleIdOfGlobalGranularity(shopId, commonModule.getId()));
                } else {
                    String domId = module.getModuleInstanceId();//模板发布的时候这里是DomId
                    String moduleInstanceId = ModuleInstanceIdFactory.createModuleIdOfFoot(shopId, domId);
                    module.setModuleInstanceId(moduleInstanceId);
                }
            }
        }
        this.editFootConfigContent = PageStructureAndXmlConverter.convertPageStructureToXml(footPs);
    }

    public void updateFromTemplateVersion(TemplateVersion templateVersion) {
        this.templateVersionId = templateVersion.getId();
        this.editFootConfigContent = templateVersion.getFootConfigContent();
        this.editHeadConfigContent = templateVersion.getHeadConfigContent();
        this.editFootContent = templateVersion.getFootContent();
        this.editHeadContent = templateVersion.getHeadContent();
    }


    public String getProdStyle() {
        return prodStyle;
    }

    public void setProdStyle(String prodStyle) {
        this.prodStyle = prodStyle;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
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
     * @return the templateVersionId
     */
    public int getTemplateVersionId() {
        return templateVersionId;
    }

    /**
     * @param templateVersionId the templateVersionId to set
     */
    public void setTemplateVersionId(int templateVersionId) {
        this.templateVersionId = templateVersionId;
    }


    /**
     * @return the editHeadContent
     */
    public String getEditHeadContent() {
        return editHeadContent;
    }

    /**
     * @param editHeadContent the editHeadContent to set
     */
    public void setEditHeadContent(String editHeadContent) {
        this.editHeadContent = editHeadContent;
    }

    /**
     * @return the editFootContent
     */
    public String getEditFootContent() {
        return editFootContent;
    }

    /**
     * @param editFootContent the editFootContent to set
     */
    public void setEditFootContent(String editFootContent) {
        this.editFootContent = editFootContent;
    }

    /**
     * @param editHeadConfigContent the editHeadConfigContent to set
     */
    public void setEditHeadConfigContent(String editHeadConfigContent) {
        this.editHeadConfigContent = editHeadConfigContent;
    }

    /**
     * @param editFootConfigContent the editFootConfigContent to set
     */
    public void setEditFootConfigContent(String editFootConfigContent) {
        this.editFootConfigContent = editFootConfigContent;
    }

    /**
     * @return the headerConfigContent
     */
    public String getEditHeadConfigContent() {
        return editHeadConfigContent;
    }

    /**
     * @return the footerConfigContent
     */
    public String getEditFootConfigContent() {
        return editFootConfigContent;
    }

    /**
     * @return the prodGlobalCss
     */
    public String getProdGlobalCss() {
        return prodGlobalCss;
    }

    /**
     * @param prodGlobalCss the prodGlobalCss to set
     */
    public void setProdGlobalCss(String prodGlobalCss) {
        this.prodGlobalCss = prodGlobalCss;
    }

    /**
     * @return the prodHeadConfigContent
     */
    public String getProdHeadConfigContent() {
        return prodHeadConfigContent;
    }

    /**
     * @param prodHeadConfigContent the prodHeadConfigContent to set
     */
    public void setProdHeadConfigContent(String prodHeadConfigContent) {
        this.prodHeadConfigContent = prodHeadConfigContent;
    }

    /**
     * @return the prodFootConfigContent
     */
    public String getProdFootConfigContent() {
        return prodFootConfigContent;
    }

    /**
     * @param prodFootConfigContent the prodFootConfigContent to set
     */
    public void setProdFootConfigContent(String prodFootConfigContent) {
        this.prodFootConfigContent = prodFootConfigContent;
    }

    /**
     * @return the prodHeadContent
     */
    public String getProdHeadContent() {
        return prodHeadContent;
    }

    /**
     * @param prodHeadContent the prodHeadContent to set
     */
    public void setProdHeadContent(String prodHeadContent) {
        this.prodHeadContent = prodHeadContent;
    }

    /**
     * @return the prodFootContent
     */
    public String getProdFootContent() {
        return prodFootContent;
    }

    /**
     * @param prodFootContent the prodFootContent to set
     */
    public void setProdFootContent(String prodFootContent) {
        this.prodFootContent = prodFootContent;
    }

    /**
     * @return the prodGlobalJs
     */
    public String getProdGlobalJs() {
        return prodGlobalJs;
    }

    /**
     * @param prodGlobalJs the prodGlobalJs to set
     */
    public void setProdGlobalJs(String prodGlobalJs) {
        this.prodGlobalJs = prodGlobalJs;
    }

    public String getEditStyle() {
        return editStyle;
    }

    public void setEditStyle(String editStyle) {
        this.editStyle = editStyle;
    }

    public String getEditGlobalCss() {
        return editGlobalCss;
    }

    public void setEditGlobalCss(String editGlobalCss) {
        this.editGlobalCss = editGlobalCss;
    }

    public String getEditGlobalJs() {
        return editGlobalJs;
    }

    public void setEditGlobalJs(String editGlobalJs) {
        this.editGlobalJs = editGlobalJs;
    }

    public String getGlobalModuleInfoConfig() {
        return globalModuleInfoConfig;
    }

    public void setGlobalModuleInfoConfig(String globalModuleInfoConfig) {
        this.globalModuleInfoConfig = globalModuleInfoConfig;
    }
}
