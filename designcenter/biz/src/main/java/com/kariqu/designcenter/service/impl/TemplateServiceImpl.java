package com.kariqu.designcenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.exception.ModuleConfigException;
import com.kariqu.designcenter.domain.model.prototype.*;
import com.kariqu.designcenter.domain.util.ModuleParamAndXmlConverter;
import com.kariqu.designcenter.repository.*;
import com.kariqu.designcenter.service.InitRenderService;
import com.kariqu.designcenter.service.TemplateService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板服务，服务与模板相关的所有对象，比如模板，版本，模板页，模块，资源，风格
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-1-12 下午03:23:25
 */
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateModuleRepository templateModuleRepository;

    @Autowired
    private TemplatePageRepository templatePageRepository;

    @Autowired
    private TemplateResourceRepository templateResourceRepository;

    @Autowired
    private TemplateVersionRepository templateVersionRepository;

    @Autowired
    private TemplateStyleRepository templateStyleRepository;

    @Autowired
    private InitRenderService initRenderService;


    /**
     * 模板版本一旦发布则不能更改，如果需要更改则必须升级该版本号
     * 发布模板版本做了如下几件事
     * 1，修改版本的状态为发布
     * 2，渲染头尾的页面结构描述文件,模块实例ID为domId,在真实应用到店铺的时候需要被替换
     * 3，渲染模板页面的结构描述文件,模块实例ID为domId,在真实应用到店铺的时候需要被替换
     *
     * @param templateVersionId
     */
    @Override
    public void releaseTemplateVersion(int templateVersionId) {

        /* 更新模板版本状态为发布并提取头尾页面结构文件*/
        TemplateVersion templateVersion = getTemplateVersionById(templateVersionId);
        templateVersion.setState(VersionState.released);
        String headPageStructure = initRenderService.initRenderTemplateHeader(templateVersion);
        templateVersion.setHeadConfigContent(headPageStructure);
        String footPageStructure = initRenderService.initRenderTemplateFooter(templateVersion);
        templateVersion.setFootConfigContent(footPageStructure);
        updateTemplateVersion(templateVersion);

        /* 提取所有模板页的页面结构文件*/
        List<TemplatePage> templatePages = queryTemplatePageByTemplateVersionId(templateVersionId);
        for (TemplatePage templatePage : templatePages) {
            String bodyPageStructure = initRenderService.initRenderPage(templatePage);
            templatePage.setConfigContent(bodyPageStructure);
            updateTemplatePage(templatePage);
        }
    }

    @Override
    public void releaseTemplateModule(int id) {
        TemplateModule currentTemplateModule = this.getTemplateModuleById(id);
        currentTemplateModule.setModuleContent(currentTemplateModule.getEditModuleContent());
        try {
            currentTemplateModule.setConfig(ModuleParamAndXmlConverter.readModuleConfig(currentTemplateModule.getModuleConfig()));
        } catch (ModuleConfigException e) {
            logger.error("模块的配置有错", e);
            currentTemplateModule.setConfig("");
        }
        this.updateTemplateModule(currentTemplateModule);
    }

    @Override
    public int createTemplateModule(TemplateModule templateModule) {
        try {
            templateModule.setConfig(ModuleParamAndXmlConverter.readModuleConfig(templateModule.getModuleConfig()));
        } catch (ModuleConfigException e) {
            logger.error("模块的配置有错", e);
            templateModule.setConfig("");
        }
        templateModuleRepository.createTemplateModule(templateModule);
        return templateModule.getId();
    }

    @Override
    public void updateTemplateModule(TemplateModule templateModule) {
        templateModuleRepository.updateTemplateModule(templateModule);
    }

    @Override
    public int createTemplate(Template template) {
        templateRepository.createTemplate(template);
        return template.getId();
    }

    @Override
    public void deleteTemplate(int id) {
        templateRepository.deleteTemplateById(id);
    }

    @Override
    public Template getTemplateById(int templateId) {
        return templateRepository.getTemplateById(templateId);
    }

    @Override
    public void updateTemplate(Template template) {
        templateRepository.updateTemplate(template);
    }

    @Override
    public TemplateModule getTemplateModuleById(int id) {
        return templateModuleRepository.getTemplateModuleById(id);
    }

    @Override
    public List<TemplateModule> queryTemplateModulesByTemplateVersionId(int templateId) {
        return templateModuleRepository.queryTemplateModulesByTemplateVersionId(templateId);
    }

    @Override
    public void deleteTemplateModule(int id) {
        templateModuleRepository.deleteTemplateModuleById(id);
    }

    @Override
    public int createTemplatePage(TemplatePage templatePage) {
        templatePageRepository.createTemplatePage(templatePage);
        return templatePage.getId();
    }

    @Override
    public void deleteTemplatePage(int id) {
        templatePageRepository.deleteTemplatePageById(id);
    }

    @Override
    public TemplatePage getTemplatePageById(int id) {
        return templatePageRepository.getTemplatePageById(id);
    }

    @Override
    public TemplatePage queryTemplatePageByPageNameAndTemplateVersionId(String pageName, int templateVersionId) {
        return templatePageRepository.queryTemplatePageByPageNameAndTemplateVersionId(pageName, templateVersionId);
    }

    @Override
    public List<TemplatePage> queryTemplatePageByTemplateVersionId(int templateVersionId) {
        return templatePageRepository.queryTemplatePagesByTemplateVersionId(templateVersionId);
    }

    @Override
    public void updateTemplatePage(TemplatePage templatePage) {
        templatePageRepository.updateTemplatePage(templatePage);
    }

    @Override
    public int createTemplateResource(TemplateResource templateResource) {
        templateResourceRepository.createTemplateResource(templateResource);
        return templateResource.getId();
    }

    @Override
    public TemplateResource getTemplateResourceById(int id) {
        return templateResourceRepository.getTemplateResourceById(id);
    }

    @Override
    public void deleteTemplateResource(int id) {
        templateResourceRepository.deleteTemplateResourceById(id);
    }

    @Override
    public void updateTemplateResource(TemplateResource templateResource) {
        templateResourceRepository.updateTemplateResource(templateResource);
    }

    @Override
    public List<TemplateResource> queryTemplateResourcesByTemplateVersionId(int templateVersionId) {
        return templateResourceRepository.queryTemplateResourcesByTemplateVersionId(templateVersionId);
    }

    @Override
    public int createTemplateVersion(TemplateVersion templateVersion) {
        templateVersionRepository.createTemplateVersion(templateVersion);
        return templateVersion.getId();
    }

    @Override
    public TemplateVersion getTemplateVersionById(int id) {
        return templateVersionRepository.getTemplateVersionById(id);
    }

    @Override
    public void updateTemplateVersion(TemplateVersion templateVersion) {
        templateVersionRepository.updateTemplateVersion(templateVersion);
    }

    @Override
    public void deleteTemplateVersion(int id) {
        templateVersionRepository.deleteTemplateVersionById(id);
    }

    @Override
    public int createTemplateStyle(TemplateStyle templateStyle) {
        templateStyleRepository.createTemplateStyle(templateStyle);
        return templateStyle.getId();
    }

    @Override
    public void deleteTemplateStyle(int id) {
        templateStyleRepository.deleteTemplateStyleById(id);
    }

    @Override
    public TemplateStyle getTemplateStyle(int id) {
        return templateStyleRepository.getTemplateStyleById(id);
    }

    @Override
    public void updateTemplateStyle(TemplateStyle templateStyle) {
        templateStyleRepository.updateTemplateStyle(templateStyle);
    }

    @Override
    public String queryGlobalCssByTemplateVersionId(int templateVersionId) {
        TemplateVersion templateVersion = this.getTemplateVersionById(templateVersionId);
        return this.getTemplateResourceById(templateVersion.getGlobalCssId()).getContent();
    }

    @Override
    public List<TemplateModule> queryTemplateModulesByIds(List<Integer> moduleIds) {
        List<TemplateModule> templateModules = new ArrayList<TemplateModule>();
        for (Integer id : moduleIds)
            templateModules.add(this.getTemplateModuleById(id));
        return templateModules;
    }

    @Override
    public List<Template> queryAllTemplates() {
        return templateRepository.queryAllTemplates();
    }

    @Override
    public List<TemplateVersion> queryTemplateVersionsByTemplateId(int templateId) {
        return templateVersionRepository.queryTemplateVersionsByTemplateId(templateId);
    }

    @Override
    public List<TemplateStyle> queryTemplateStylesByTemplateVersionId(int templateVersionId) {
        return templateStyleRepository.queryTemplateStylesByTemplateVersionId(templateVersionId);
    }

    @Override
    public List<TemplateResource> queryTemplateResourcesByResourceType(int templateVersionId, ResourceType resourceType) {
        return templateResourceRepository.queryTemplateResourcesByResourceType(templateVersionId, resourceType);
    }

    @Override
    public Page<TemplatePage> queryTemplatePageByTemplateVersionIdAndPage(int templateVersionId, Page<TemplatePage> page) {
        return templatePageRepository.queryTemplatePagesByTemplateVersionIdAndPage(templateVersionId, page);
    }


    @Override
    public Page<TemplateModule> queryTemplateModulesByTemplateVersionIdAndPage(int templateVersionId,
                                                                               Page<TemplateModule> page) {
        return templateModuleRepository.queryTemplateModulesByTemplateVersionIdAndPage(templateVersionId, page);
    }


    @Override
    public Page<TemplateResource> queryTemplateResourcesByTemplateVersionIdAndPage(int templateVersionId,
                                                                                   Page<TemplateResource> page) {
        return templateResourceRepository.queryTemplateResourcesByTemplateVersionIdAndPage(templateVersionId, page);
    }


    @Override
    public Page<TemplateStyle> queryTemplateStylesByTemplateVersionIdAndPage(int templateVersionId,
                                                                             Page<TemplateStyle> page) {
        return templateStyleRepository.queryTemplateStylesByTemplateVersionIdAndPage(templateVersionId, page);
    }


    @Override
    public boolean existIndexPage(int templateVersionId) {
        if (templatePageRepository.queryIndexTemplatePage(templateVersionId) != null)
            return true;
        return false;
    }

    @Override
    public boolean existSearchListPage(int templateVersionId) {
        if (templatePageRepository.querySearchListTemplatePage(templateVersionId) != null)
            return true;
        return false;
    }

    @Override
    public boolean existDetailPage(int templateVersionId) {
        if (templatePageRepository.queryDetailTemplatePage(templateVersionId) != null)
            return true;
        return false;
    }


    @Override
    public boolean existTemplateModule(String name, int templateVersionId) {
        return templateModuleRepository.queryTemplateModulesByTemplateVersionIdAndName(name, templateVersionId).size() > 0 ? true : false;
    }


    @Override
    public TemplateVersion queryDefaultTemplateVersion() {
        //todo
        Template template = templateRepository.queryTemplateByName("标准店铺");
        return this.templateVersionRepository.queryTemplateVersionsByTemplateId(template.getId()).get(0);
    }

    @Override
    public void deleteTemplateModuleByTemplateVersionId(int templateVersionId) {
        this.templateModuleRepository.deleteByTemplateVersionId(templateVersionId);
    }

    @Override
    public void deleteTemplatePageByTemplateVersionId(int templateVersionId) {
        this.templatePageRepository.deleteByTemplateVersionId(templateVersionId);
    }

    @Override
    public void deleteTemplateResourceByTemplateVersionId(int templateVersionId) {
        this.templateResourceRepository.deleteByTemplateVersionId(templateVersionId);
    }

    @Override
    public void deleteTemplateStyleByTemplateVersionId(int templateVersionId) {
        this.templateStyleRepository.deleteByTemplateVersionId(templateVersionId);
    }


}
