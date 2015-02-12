package com.kariqu.designcenter.service;

import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;


/**
 * 负责模板页面初始化生成页面配置文件
 * 
 * @author Tiger
 * @since 2011-4-4 下午08:31:57
 * @version 1.0
 */
public interface InitRenderService {

    /**
     * 返回页面初始化配置文件
     * 
     * @param templatePage
     */
    String initRenderPage(TemplatePage templatePage);

    /**
     * 页面原型初始化，生成页面原型的配置文件
     * @param pagePrototype
     * @return
     */
    String initRenderPagePrototype(PagePrototype pagePrototype);

    /**
     * 返回模板头部的配置文件
     * 
     * @param templateVersion
     * @return
     */
    String initRenderTemplateHeader(TemplateVersion templateVersion);

    /**
     * 返回模板尾部配置文件
     * 
     * @param templateVersion
     * @return
     */
    String initRenderTemplateFooter(TemplateVersion templateVersion);
    
    
    
}
