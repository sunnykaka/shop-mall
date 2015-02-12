package com.kariqu.designcenter.client.domain.model;

import com.kariqu.designcenter.domain.model.Renderable;

import java.util.Map;

/**
 * 页面渲染引擎
 * 
 * @author Tiger
 * @since 2010-12-17 上午10:45:44
 * @version 1.0
 */
public interface RenderEngine {

    /**
     * 渲染可渲染的对象
     * @param renderable
     * @param renderContext
     * @return
     */
    String render(Renderable renderable, Map<String, Object> renderContext);

    /**
     * 渲染模板页面
     * @param content
     * @param renderContext
     * @return
     */
    String render(String content, Map<String, Object> renderContext);

}
