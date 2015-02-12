package com.kariqu.designcenter.client.domain.model.context;

import com.kariqu.common.uri.URLBroker;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.ModuleInfo;
import com.kariqu.designcenter.client.domain.model.RenderEngine;
import com.kariqu.designcenter.domain.model.Module;
import com.kariqu.designcenter.domain.model.PageStructure;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;

import java.util.List;
import java.util.Map;

/**
 * 编辑模式渲染上下文
 * 编辑模式也是装修模式，这个模式和预览模式的唯一差别就是就是多了到参数编辑表单的链接
 * 所以这个渲染场景继承预览渲染场景
 *
 * @Author: Tiger
 * @Since: 2011-5-8 23:38:21
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class EditRenderContext extends PreviewRenderContext {

    private URLBrokerFactory urlBrokerFactory;

    public EditRenderContext(RenderEngine renderEngine, PageStructure pageStructure, ShopTemplate shopTemplate, ShopPage shopPage) {
        super(renderEngine, pageStructure, shopTemplate, shopPage);
    }

    /**
     * 在超类渲染出的模块上加一个工具条
     *
     * @param context
     * @param module
     * @return
     */
    @Override
    protected String renderModule(Map<String, Object> context, Module module) {
        context.put(RenderConstants.TOOL_BAR, module.getToolbar().toString());
        return super.renderModule(context, module);
    }

    @Override
    public String renderRegion(String regionName, List<ModuleInfo> moduleInfoList) {
        switch (rendArea) {
            case head: {
                List<Module> modules = pageStructure.getModulesFromRegionOfHead(regionName);
                return wrapperRegion(renderModules(modules), regionName);
            }
            case body: {
                List<Module> modules = pageStructure.getModulesFromRegionOfBody(regionName);
                return wrapperRegion(renderModules(modules), regionName);
            }
            case foot: {
                List<Module> modules = pageStructure.getModulesFromRegionOfFoot(regionName);
                return wrapperRegion(renderModules(modules), regionName);
            }
            default:
                return "renderRegion-渲染区域未指定";
        }
    }


    protected String wrapperRegion(String originalContent, String regionName) {
        URLBroker urlBroker = urlBrokerFactory.getUrl("getModules");
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"e_region\"")
                .append("data-url=\"");
        urlBroker.addQueryData("regionName", regionName);
        switch (rendArea) {
            case head:
                result.append(urlBroker.addQueryData("area", RenderConstants.RenderArea.head).toString());
                break;
            case foot:
                result.append(urlBroker.addQueryData("area", RenderConstants.RenderArea.foot).toString());
                break;
            case body:
                result.append(urlBroker.addQueryData("area", RenderConstants.RenderArea.body).toString());
                break;
            default:

        }
        result.append("\"");
        result.append("data-name=\"");
        result.append(regionName);
        return result.append("\">")
                .append(originalContent)
                .append("</div>").toString();
    }

    private boolean isRenderingBody() {
        return RenderConstants.RenderArea.body.equals(super.rendArea);
    }

    private boolean isRenderingFoot() {
        return RenderConstants.RenderArea.foot.equals(super.rendArea);
    }

    private boolean isRenderingHead() {
        return RenderConstants.RenderArea.head.equals(super.rendArea);
    }


    public void setUrlBrokerFactory(URLBrokerFactory urlBrokerFactory) {
        this.urlBrokerFactory = urlBrokerFactory;
    }
}
