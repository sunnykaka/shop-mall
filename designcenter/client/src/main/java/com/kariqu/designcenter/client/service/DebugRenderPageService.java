package com.kariqu.designcenter.client.service;

import com.kariqu.designcenter.client.domain.model.RenderResult;

import java.util.Map;

/**
 * @author Tiger
 * @version 1.0
 * @since 2011-4-4 下午07:55:09
 */
public interface DebugRenderPageService {

    /**
     * 调试模板页面
     * 模板页面的调试会同时渲染出模板版本的头尾
     *
     * @param templatePageId
     * @param context
     * @return
     */
    public RenderResult debugRenderPage(int templatePageId, Map<String, Object> context);

    /**
     * 调试页面原型
     * 原型是不包含头尾概念的，所以调试一个原型的时候没有头尾的概念
     *
     * @param pagePrototypeId
     * @param context
     * @return
     */
    public RenderResult debugPagePrototype(int pagePrototypeId, Map<String, Object> context);
}
