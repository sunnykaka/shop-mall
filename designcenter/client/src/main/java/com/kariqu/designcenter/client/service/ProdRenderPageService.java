package com.kariqu.designcenter.client.service;

import com.kariqu.designcenter.client.domain.model.RenderResult;

import java.util.Map;

/**
 * 生产环境页面渲染服务
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-5 下午01:29:29
 */
public interface ProdRenderPageService {

    /**
     * 生产环境页面渲染服务
     *
     * @param pageId
     * @param context
     * @return
     */
    RenderResult prodRenderPage(long pageId, Map<String, Object> context);


    /**
     * 生产环境下渲染全局公共模块，主要用于非装修页面单独渲染此模块
     *
     * @param moduleName
     * @param context
     * @return
     */
    RenderResult prodRenderGlobalCommonModule(String moduleName, Map<String, Object> context);

}
