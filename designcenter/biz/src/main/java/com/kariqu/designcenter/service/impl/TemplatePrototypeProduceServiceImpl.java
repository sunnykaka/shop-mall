package com.kariqu.designcenter.service.impl;

import com.kariqu.designcenter.domain.model.prototype.*;
import com.kariqu.designcenter.service.TemplatePrototypeProduceService;
import com.kariqu.designcenter.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-12 下午02:43:24
 */

@Transactional
public class TemplatePrototypeProduceServiceImpl implements TemplatePrototypeProduceService {

    @Autowired
    private TemplateService templateService;

    @Override
    public int createTemplate(Template template) {
        int templateId = templateService.createTemplate(template);
        TemplateVersion templateVersion = new TemplateVersion();
        templateVersion.setTemplateId(templateId);
        templateVersion.setState(VersionState.debug);
        templateVersion.setVersion("1.0.0");
        int templateVersionId = templateService.createTemplateVersion(templateVersion);
        return templateVersionId;
    }

    /**
     * 升级模板
     * 复制模板下的最高版本号的所有数据，然后建立新版本号
     * 复制过程原来有ID关联的需要用新的ID替换，比如原来版本号关联的js,css ID,风格关联的CSS ID，这些地方
     * 就需要用新生成的ID替换
     *
     * @param templateId
     */
    @Override
    public int upgradeTemplate(int templateId) {
        List<TemplateVersion> versions = templateService.queryTemplateVersionsByTemplateId(templateId);
        TemplateVersion highestVersion = versions.get(0);
        for (TemplateVersion version : versions) {
            if (version.getId() > highestVersion.getId())
                highestVersion = version;
        }
        if (highestVersion.getState() == VersionState.debug)
            throw new RuntimeException(("不能对未发布的版本号进行升级"));
        List<TemplatePage> templatePages = templateService.queryTemplatePageByTemplateVersionId(highestVersion.getId());
        List<TemplateModule> templateModules = templateService.queryTemplateModulesByTemplateVersionId(highestVersion.getId());
        List<TemplateResource> templateResources = templateService.queryTemplateResourcesByTemplateVersionId(highestVersion.getId());
        List<TemplateStyle> templateStyles = templateService.queryTemplateStylesByTemplateVersionId(highestVersion.getId());
        highestVersion.upgradeVersion();
        highestVersion.setState(VersionState.debug);
        templateService.createTemplateVersion(highestVersion);

        //这个map通过资源ID为key,方便在科隆新资源的时候替换掉Style中的资源ID
        Map<Integer, TemplateStyle> map = new HashMap<Integer, TemplateStyle>();

        for (TemplateStyle templateStyle : templateStyles) {
            templateStyle.setTemplateVersionId(highestVersion.getId());
            if (templateStyle.getId() == highestVersion.getDefaultStyleId()) {
                templateService.createTemplateStyle(templateStyle);
                highestVersion.setDefaultStyleId(templateStyle.getId());
            } else {
                templateService.createTemplateStyle(templateStyle);
            }
            map.put(templateStyle.getStyleResourceId(), templateStyle);
        }

        for (TemplateResource templateResource : templateResources) {
            templateResource.setTemplateVersionId(highestVersion.getId());
            if (templateResource.getId() == highestVersion.getGlobalCssId()) {
                templateService.createTemplateResource(templateResource);
                highestVersion.setGlobalCssId(templateResource.getId());
            } else if (templateResource.getId() == highestVersion.getGlobalJsId()) {
                templateService.createTemplateResource(templateResource);
                highestVersion.setGlobalJsId(templateResource.getId());
            } else if (map.get(templateResource.getId()) != null) {
                TemplateStyle templateStyle = map.get(templateResource.getId());
                templateService.createTemplateResource(templateResource);
                templateStyle.setStyleResourceId(templateResource.getId());
            } else {
                templateService.createTemplateResource(templateResource);
            }

        }

        for (TemplateModule templateModule : templateModules) {
            templateModule.setTemplateVersionId(highestVersion.getId());
            templateService.createTemplateModule(templateModule);
        }

        for (TemplatePage templatePage : templatePages) {
            templatePage.setTemplateVersionId(highestVersion.getId());
            templateService.createTemplatePage(templatePage);
        }

        for (TemplateStyle templateStyle : templateStyles) {
            templateService.updateTemplateStyle(templateStyle);
        }

        templateService.updateTemplateVersion(highestVersion);

        return highestVersion.getId();
    }

    @Override
    public void clearTemplateInfoByTemplateVersionId(int templateVersionId) {
        TemplateVersion templateVersion = templateService.getTemplateVersionById(templateVersionId);
        if (templateVersion.getState() == VersionState.released) {
            throw new RuntimeException("不能删除已发布的版本");
        }
        List<TemplateVersion> versions = templateService.queryTemplateVersionsByTemplateId(templateVersion.getTemplateId());
        if (versions.size() == 1) { //如果这个模板只有一个未发布的版本，那么把模板也删除
            templateService.deleteTemplate(templateVersion.getTemplateId());
        }
        templateService.deleteTemplateVersion(templateVersionId);
        templateService.deleteTemplatePageByTemplateVersionId(templateVersionId);
        templateService.deleteTemplateModuleByTemplateVersionId(templateVersionId);
        templateService.deleteTemplateResourceByTemplateVersionId(templateVersionId);
        templateService.deleteTemplateStyleByTemplateVersionId(templateVersionId);
    }


    @Override
    public void clearTemplateInfoByTemplateId(int templateId) {
        List<TemplateVersion> versions = templateService.queryTemplateVersionsByTemplateId(templateId);
        if (versions.size() == 0) {
            templateService.deleteTemplate(templateId);
        } else {
            if (checkIfHasReleaseVersion(versions))
                throw new RuntimeException("该模板有发布了的版本，不能删除");
            else {
                for (TemplateVersion version : versions) {
                    clearTemplateInfoByTemplateVersionId(version.getId());
                }
            }
        }
    }


    private boolean checkIfHasReleaseVersion(List<TemplateVersion> versions) {
        for (TemplateVersion version : versions) {
            if (version.getState() == VersionState.released)
                return true;
        }
        return false;
    }
}
