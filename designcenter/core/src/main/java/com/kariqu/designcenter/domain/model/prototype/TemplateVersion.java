package com.kariqu.designcenter.domain.model.prototype;

import com.kariqu.designcenter.domain.util.VersionUtil;

import java.io.Serializable;

/**
 * 模板版本对象
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-7 下午07:55:45
 */
public class TemplateVersion implements Serializable {

    private static final long serialVersionUID = 4505041118847562232L;

    private int id;

    private int templateId;

    private String headContent;

    private String footContent;

    private String headConfigContent;

    private String footConfigContent;

    private VersionState state;

    /**
     * 模板版本全局样式文件ID，对应templateResource id
     */
    private int globalCssId;

    private int globalJsId;

    private int defaultStyleId;

    private String version;

    public void updateFrom(TemplateVersion from) {
        this.setHeadContent(from.getHeadContent());
        this.setFootContent(from.getFootContent());
        this.setGlobalCssId(from.getGlobalCssId());
        this.setGlobalJsId(from.getGlobalJsId());
        this.setDefaultStyleId(from.getDefaultStyleId());
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
     * @return the templateId
     */
    public int getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId the templateId to set
     */
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the headContent
     */
    public String getHeadContent() {
        return headContent;
    }

    /**
     * @param headContent the headContent to set
     */
    public void setHeadContent(String headContent) {
        this.headContent = headContent;
    }

    /**
     * @return the footContent
     */
    public String getFootContent() {
        return footContent;
    }

    /**
     * @param footContent the footContent to set
     */
    public void setFootContent(String footContent) {
        this.footContent = footContent;
    }

    /**
     * @return the headConfigContent
     */
    public String getHeadConfigContent() {
        return headConfigContent;
    }

    /**
     * @param headConfigContent the headConfigContent to set
     */
    public void setHeadConfigContent(String headConfigContent) {
        this.headConfigContent = headConfigContent;
    }

    /**
     * @return the footConfigContent
     */
    public String getFootConfigContent() {
        return footConfigContent;
    }

    /**
     * @param footConfigContent the footConfigContent to set
     */
    public void setFootConfigContent(String footConfigContent) {
        this.footConfigContent = footConfigContent;
    }

    /**
     * @return the globalCssId
     */
    public int getGlobalCssId() {
        return globalCssId;
    }

    /**
     * @param globalCssId the globalCssId to set
     */
    public void setGlobalCssId(int globalCssId) {
        this.globalCssId = globalCssId;
    }

    /**
     * @return the state
     */
    public VersionState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(VersionState state) {
        this.state = state;
    }


    /**
     * 升级版本号
     */
    public void upgradeVersion() {
        this.version = VersionUtil.upgradeVersion(version);
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }


    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the globalJsId
     */
    public int getGlobalJsId() {
        return globalJsId;
    }

    /**
     * @param globalJsId the globalJsId to set
     */
    public void setGlobalJsId(int globalJsId) {
        this.globalJsId = globalJsId;
    }

    public int getDefaultStyleId() {
        return defaultStyleId;
    }

    public void setDefaultStyleId(int defaultStyleId) {
        this.defaultStyleId = defaultStyleId;
    }


}
