package com.kariqu.designcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.*;

import java.util.List;

/**
 * 模板服务，封装了操作模板页，模板模块，资源，风格的所有方法
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-11 下午10:23:51
 */
public interface TemplateService {

    /**
     * 模板版本一旦发布则不能更改，如果需要更改则必须升级该版本号
     * 发布模板版本做了如下几件事
     * 1，修改版本的状态为发布
     * 2，渲染头尾的页面结构描述文件,模块实例ID为domId,在真实应用到店铺的时候需要被替换
     * 3，渲染模板页面的结构描述文件,模块实例ID为domId,在真实应用到店铺的时候需要被替换
     * @param templateVersionId
     */
    void releaseTemplateVersion(int templateVersionId);

    void releaseTemplateModule(int id);

    /**
     * 首页只能有一个，判断这个版本是否已存在首页
     *
     * @param templateVersionId
     * @return
     */
    boolean existIndexPage(int templateVersionId);

    boolean existSearchListPage(int templateVersionId);

    boolean existDetailPage(int templateVersionId);

    /**
     * 一个版本下的模块名字需要唯一
     *
     * @param name
     * @param templateVersionId
     * @return
     */
    boolean existTemplateModule(String name, int templateVersionId);


    /**
     * Template CRUD
     */

    int createTemplate(Template template);

    Template getTemplateById(int id);

    void updateTemplate(Template template);

    void deleteTemplate(int id);

    List<Template> queryAllTemplates();

    /**
     * TemplateModule CRUD
     */
    int createTemplateModule(TemplateModule templateModule);

    TemplateModule getTemplateModuleById(int id);

    List<TemplateModule> queryTemplateModulesByTemplateVersionId(int templateVersionId);

    Page<TemplateModule> queryTemplateModulesByTemplateVersionIdAndPage(int templateVersionId, Page<TemplateModule> page);

    void updateTemplateModule(TemplateModule templateModule);

    void deleteTemplateModule(int id);

    List<TemplateModule> queryTemplateModulesByIds(List<Integer> moduleIds);

    void deleteTemplateModuleByTemplateVersionId(int templateVersionId);


    /**
     * TemplatePage CRUD
     */
    int createTemplatePage(TemplatePage templatePage);

    TemplatePage getTemplatePageById(int id);

    TemplatePage queryTemplatePageByPageNameAndTemplateVersionId(String pageName, int templateVersionId);

    List<TemplatePage> queryTemplatePageByTemplateVersionId(int templateVersionId);

    Page<TemplatePage> queryTemplatePageByTemplateVersionIdAndPage(int templateVersionId, Page<TemplatePage> page);

    void updateTemplatePage(TemplatePage templatePage);

    void deleteTemplatePage(int id);

    void deleteTemplatePageByTemplateVersionId(int templateVersionId);


    /**
     * TemplateResource CRUD
     */
    int createTemplateResource(TemplateResource templateResource);

    TemplateResource getTemplateResourceById(int id);

    List<TemplateResource> queryTemplateResourcesByTemplateVersionId(int templateVersionId);

    Page<TemplateResource> queryTemplateResourcesByTemplateVersionIdAndPage(int templateVersionId, Page<TemplateResource> page);

    List<TemplateResource> queryTemplateResourcesByResourceType(int templateVersionId, ResourceType resourceType);

    void updateTemplateResource(TemplateResource templateResource);

    void deleteTemplateResource(int id);

    void deleteTemplateResourceByTemplateVersionId(int templateVersionId);


    /**
     * 根据模板版本查询全局CSS
     *
     * @param templateVersionId
     * @return
     */
    String queryGlobalCssByTemplateVersionId(int templateVersionId);


    /**
     * TemplateStyle CRUD
     */
    int createTemplateStyle(TemplateStyle templateStyle);

    TemplateStyle getTemplateStyle(int id);

    List<TemplateStyle> queryTemplateStylesByTemplateVersionId(int templateVersionId);

    Page<TemplateStyle> queryTemplateStylesByTemplateVersionIdAndPage(int templateVersionId, Page<TemplateStyle> page);

    void updateTemplateStyle(TemplateStyle templateStyle);

    void deleteTemplateStyle(int id);

    void deleteTemplateStyleByTemplateVersionId(int templateVersionId);

    /**
     * TemplateVersion CRUD
     */
    int createTemplateVersion(TemplateVersion templateVersion);

    TemplateVersion getTemplateVersionById(int id);

    void updateTemplateVersion(TemplateVersion templateVersion);

    void deleteTemplateVersion(int id);

    List<TemplateVersion> queryTemplateVersionsByTemplateId(int templateId);

    TemplateVersion queryDefaultTemplateVersion();


}
