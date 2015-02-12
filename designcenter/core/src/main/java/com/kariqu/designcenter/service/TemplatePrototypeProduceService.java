package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.prototype.Template;

/**
 * 模板在线制作服务
 * @author Tiger
 * @since 2011-4-11 下午08:32:04
 * @version 1.0.0
 */
public interface TemplatePrototypeProduceService {

    /**
     * 创建模板对象，需要创建对应的模板版本对象，并将其关联，同时设置模板版本的状态为debug
     * 
     * @param template
     * @return 模板版本对象的id
     */
    int createTemplate(Template template);

    /**
     * 按照模板版本清理模板信息
     * @param templateVersionId
     */
    void clearTemplateInfoByTemplateVersionId(int templateVersionId);


    /**
     * 按照模板清理模板信息
     * @param templateId
     */
    void clearTemplateInfoByTemplateId(int templateId);


    /**
     * 克隆一个版本号用于对模板进行升级
     * @param templateId
     */
    int upgradeTemplate(int templateId);


}
