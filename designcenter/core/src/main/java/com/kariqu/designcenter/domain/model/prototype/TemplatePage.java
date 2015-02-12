package com.kariqu.designcenter.domain.model.prototype;

import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.Renderable;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 模板页面
 *
 * @author Tiger
 * @version 1.0
 * @since 2010-12-16 下午12:27:13
 */
public class TemplatePage implements Renderable, Serializable {

    private static final long serialVersionUID = 3683742577961647934L;

    private int id;

    private String pageContent;

    private String pageName;

    /**
     * 模板页面对应的模板版本
     */
    private int templateVersionId;

    /**
     * 模板页面初始xml配置文件内容
     */
    private String configContent;

    /**
     * 模板配置文件的键
     */
    private String configKey;

    /**
     * 页面的类型
     */
    private PageType pageType;

    /**
     * 页面参数,留给以后扩展
     */
    private List<Parameter> params = new LinkedList<Parameter>();


    @Override
    public String getContent() {
        return pageContent;
    }

    @Override
    public Map<String, Object> getContext() {
        return Collections.<String, Object>emptyMap();
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
     * @return the pageContent
     */
    public String getPageContent() {
        return pageContent;
    }

    /**
     * @param pageContent the pageContent to set
     */
    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    /**
     * @return the pageName
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * @param pageName the pageName to set
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
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
     * @return the configContent
     */
    public String getConfigContent() {
        return configContent;
    }

    /**
     * @param configContent the configContent to set
     */
    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    /**
     * @return the configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * @param configKey the configKey to set
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * @return the params
     */
    public List<Parameter> getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(List<Parameter> params) {
        this.params = params;
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


}
